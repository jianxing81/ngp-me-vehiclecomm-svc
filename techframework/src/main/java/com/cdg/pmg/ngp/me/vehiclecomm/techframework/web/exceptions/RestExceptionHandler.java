package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.exceptions;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.constants.VehicleCommDomainConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.DomainException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.NotFoundException;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.BadRequestError;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.Error;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.ErrorData;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.ErrorDataData;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.FieldItem;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.InternalServerErrorResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.NotFoundResponse;
import datadog.trace.api.GlobalTracer;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Global Exception Handler used for this service.
 *
 * <p>In a Spring Boot application, when an exception occurs, the specific @ExceptionHandler method
 * that gets triggered depends on the type of exception thrown. Spring's exception handling
 * mechanism respects the specificity of exception types. A handler for a specific exception type
 * will always take precedence over a handler for a more general exception type.
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class RestExceptionHandler {

  private final MessageSource messageSource;

  /**
   * Handle not found exception response.
   *
   * @param ex the exception
   * @return the response
   */
  @ResponseBody
  @ExceptionHandler(value = NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public NotFoundResponse handleNotFoundException(
      final HttpServletRequest request, final NotFoundException ex) {

    return new NotFoundResponse()
        .error(
            new Error()
                .code(ex.getErrorCode().toString())
                .message(ex.getMessage() + Arrays.toString(ex.getArguments())))
        .timestamp(Instant.now())
        .path(request.getRequestURI())
        .traceId(GlobalTracer.get().getTraceId());
  }

  /**
   * Handle Internal server exception response.
   *
   * @param ex the exception
   * @return the response
   */
  @ExceptionHandler(DomainException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public InternalServerErrorResponse handleInternalServerException(
      HttpServletRequest request, DomainException ex) {

    return new InternalServerErrorResponse()
        .error(
            new Error()
                .code(ex.getErrorCode().toString())
                .message(
                    messageSource.getMessage(ex.getErrorCode().toString(), null, Locale.ENGLISH)))
        .timestamp(Instant.now())
        .path(request.getRequestURI())
        .traceId(GlobalTracer.get().getTraceId());
  }

  /**
   * Handle Bad Request exception response.
   *
   * @param request request
   * @param ex exception
   * @return BadRequestError
   */
  @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public BadRequestError handleBadRequestException(HttpServletRequest request, Exception ex) {

    if (ex instanceof MethodArgumentNotValidException subex) {
      return handleMethodArgumentNotValidException(request, subex);
    } else if (ex instanceof HttpMessageNotReadableException subex) {
      return handleHttpMessageNotReadableException(request, subex);
    }

    throw new IllegalArgumentException(
        "Unexpected exception type: " + ex.getClass().getSimpleName());
  }

  /**
   * Private method to handle MethodArgumentNotValidException. This is part of the
   * handleBadRequestException method.
   *
   * @param request request
   * @param ex exception
   * @return BadRequestError
   */
  private BadRequestError handleMethodArgumentNotValidException(
      HttpServletRequest request, MethodArgumentNotValidException ex) {

    Map<String, FieldItem> fieldErrorDetailMap = new HashMap<>();
    for (var fieldError : ex.getBindingResult().getFieldErrors()) {
      String fieldName = fieldError.getField();
      String message = fieldError.getDefaultMessage();

      if (fieldErrorDetailMap.containsKey(fieldName)) {
        // If the map already contains the field, append the new message
        List<String> messages = fieldErrorDetailMap.get(fieldName).getMessage();
        messages.add(message);

      } else {
        // Otherwise, just put the new message
        fieldErrorDetailMap.put(
            fieldName, new FieldItem().name(fieldName).message(List.of(message)));
      }
    }
    ErrorDataData errorData =
        new ErrorDataData().fields(new ArrayList<>(fieldErrorDetailMap.values()));
    return new BadRequestError()
        .error(
            new ErrorData()
                .code(HttpStatus.BAD_REQUEST.toString())
                .message(MethodArgumentNotValidException.class.getSimpleName())
                .data(errorData))
        .timestamp(Instant.now())
        .path(request.getRequestURI())
        .traceId(GlobalTracer.get().getTraceId());
  }

  /**
   * Private method to handle HttpMessageNotReadableException. This is part of the
   * handleBadRequestException method.
   *
   * @param request
   * @param ex
   * @return
   */
  private BadRequestError handleHttpMessageNotReadableException(
      HttpServletRequest request, HttpMessageNotReadableException ex) {

    // attempt to extract offending field name from detailed cause message
    String causeMessage = ex.getCause().getLocalizedMessage();
    String pattern = VehicleCommDomainConstant.PATTERN;
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(causeMessage);
    String fieldName = ex.getCause().getClass().getSimpleName();
    if (m.find()) {
      fieldName = m.group(1);
    }

    // extract general message from exception
    String message = ex.getMessage();

    ErrorDataData errorData =
        new ErrorDataData()
            .fields(
                Collections.singletonList(
                    new FieldItem().name(fieldName).message(Collections.singletonList(message))));
    return new BadRequestError()
        .error(
            new ErrorData()
                .code(HttpStatus.BAD_REQUEST.toString())
                .message(HttpMessageNotReadableException.class.getSimpleName())
                .data(errorData))
        .timestamp(Instant.now())
        .path(request.getRequestURI())
        .traceId(GlobalTracer.get().getTraceId());
  }
}
