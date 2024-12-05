package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.advice;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageKeyData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageRequestHolder;
import com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.handlers.ApplicationExceptionHandler;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.ApplicationException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.DomainException;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.jobdispatchevent.JobDispatchEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JsonHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.VehicleCommFrameworkConstants;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@SuppressWarnings("unchecked")
public class ApplicationAdvice<T extends DomainException> {

  private final Map<Class<T>, ApplicationExceptionHandler<T>> exceptionHandlerMap;
  private final JsonHelper jsonHelper;
  private final CacheManager cacheManager;

  public ApplicationAdvice(
      List<ApplicationExceptionHandler<T>> exceptionHandlerList,
      JsonHelper jsonHelper,
      CacheManager cacheManager) {
    this.exceptionHandlerMap =
        exceptionHandlerList.stream()
            .collect(
                Collectors.toMap(
                    ApplicationExceptionHandler::getSupportedException, Function.identity()));
    this.jsonHelper = jsonHelper;
    this.cacheManager = cacheManager;
  }

  @Before(
      value =
          "execution(* com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.consumer.*.*(..))")
  public void beforeConsumingKafkaMessage(JoinPoint joinPoint) {
    if (!VehicleCommFrameworkConstants.REGULAR_REPORT_CONSUMER_CLASS.equalsIgnoreCase(
        joinPoint.getTarget().getClass().getSimpleName())) {
      if (joinPoint.getArgs()[0] instanceof JobDispatchEvent jobDispatchEvent) {
        log.info(
            "[{}] [{}] [{}] [{}] [{}] [{}] [{}] {}",
            joinPoint.getSignature().getName(),
            jobDispatchEvent.getJobNo(),
            jobDispatchEvent.getEventName(),
            jobDispatchEvent.getIvdNo(),
            jobDispatchEvent.getVehicleId(),
            jobDispatchEvent.getDriverId(),
            jobDispatchEvent.getOfferableDevice(),
            jsonHelper.pojoToJson(joinPoint.getArgs()[0]));
      }
      log.info(
          "[{}] {}",
          joinPoint.getSignature().getName(),
          jsonHelper.pojoToJson(joinPoint.getArgs()[0]));
    }
  }

  @Around(
      value =
          "execution(* com.cdg.pmg.ngp.me.vehiclecomm.techframework.async.KafkaMessageHandler.*(..))")
  public void aroundKafkaMessageHandler(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
      joinPoint.proceed();
    } catch (ApplicationException applicationException) {
      DomainException domainException = applicationException.getDomainException();
      ApplicationExceptionHandler<T> exceptionHandler =
          exceptionHandlerMap.get(domainException.getClass());
      if (isErrorOutRequired()) {
        exceptionHandler.handleException(
            (T) domainException,
            applicationException.getCommand(),
            applicationException.getTopic());
      }
    } finally {
      RedundantMessageRequestHolder.clear();
    }
  }

  @AfterReturning(
      value =
          "execution(public * com.cdg.pmg.ngp.me.vehiclecomm.techframework.internal.service.impl.ByteToBeanConverterImpl.*(..))",
      returning = "result")
  public void afterByteToBeanConversion(JoinPoint joinPoint, Object result) {
    String methodName = joinPoint.getSignature().getName();
    if (!VehicleCommFrameworkConstants.REGULAR_REPORT_CONVERTER.equalsIgnoreCase(methodName)) {
      log.info("[{}] {}", methodName, jsonHelper.pojoToJson(result));
    }
  }

  /**
   * Check if details are not present in ThreadLocal then send to error out If details are present
   * in ThreadLocal and cache do have entry send to error out or else do not
   *
   * @return boolean
   */
  private boolean isErrorOutRequired() {
    RedundantMessageKeyData cacheKeyDetails = RedundantMessageRequestHolder.get();

    if (Objects.nonNull(cacheKeyDetails)) {
      String cacheKey = cacheKeyDetails.getMessageID() + cacheKeyDetails.getUniqueKey();
      UUID eventId = cacheKeyDetails.getEventID();
      Cache cache = cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE);

      if (Objects.nonNull(cache)) {
        var isDuplicate = cache.get(cacheKey) != null;
        log.info(
            "[isErrorOutRequired] The message for cacheKey {} and eventId {} isDuplicate: {} ",
            cacheKey,
            eventId,
            isDuplicate);
        return BooleanUtils.negate(isDuplicate);
      }
    }
    return true;
  }
}
