package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.client.api.service.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobDispatchDetailsRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobDispatchPostEventsRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobOffersResponseData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.JobDispatchAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.NetworkException;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JsonHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.VehicleCommFrameworkConstants;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.apis.JobControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.apis.JobEventLogControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.apis.JobNoBlockControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers.JobDispatchMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/** Class for calling JobDispatch service */
@RequiredArgsConstructor
@ServiceComponent
@Slf4j
public class JobDispatchAPIServiceImpl implements JobDispatchAPIService {
  private final JobControllerApi jobControllerApi;
  private final JobEventLogControllerApi jobEventLogControllerApi;
  private final JobNoBlockControllerApi jobNoBlockControllerApi;
  private final JobDispatchMapper jobDispatchMapper;
  private final JsonHelper jsonHelper;

  /**
   * This method is used to call driver response
   *
   * @param jobNo - search api request
   */
  @Override
  public void getDriverResponse(String jobNo, JobDispatchDetailsRequest jobDispatchDetailsRequest) {
    try {
      log.info(
          "[getDriverResponse] Request - JobNo: {} {}",
          jobNo,
          jsonHelper.pojoToJson(jobDispatchDetailsRequest));
      jobControllerApi
          .handleDriverResponse(
              jobNo, jobDispatchMapper.mapToHandleDriverRequest(jobDispatchDetailsRequest))
          .block();
    } catch (WebClientResponseException exception) {
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.JOB_DISPATCH_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
      throw new NetworkException(
          exception.getResponseBodyAsString(),
          ErrorCode.JOB_DISPATCH_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  /**
   * This method is used to call job-offers response
   *
   * @param vehicleId - search api request
   */
  @Override
  public JobOffersResponseData getJobOffersResponse(String vehicleId, String driverId) {
    try {
      log.info("[getJobOffersResponse] Request - VehicleId: {},DriverId : {}", vehicleId, driverId);
      com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.models
              .JobOffersResponse
          jobOffersResponse = jobControllerApi.getJobOfferByVehicleId(vehicleId, driverId).block();
      log.info("[getJobOffersResponse] Response - {}", jsonHelper.pojoToJson(jobOffersResponse));
      return jobDispatchMapper.mapToJobOffersResponse(jobOffersResponse);
    } catch (WebClientResponseException exception) {
      log.error(
          "errorCode:{} errorMsg:{}",
          ErrorCode.JOB_DISPATCH_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
      throw new NetworkException(
          exception.getResponseBodyAsString(),
          ErrorCode.JOB_DISPATCH_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  @Override
  public void postJobEvents(JobDispatchPostEventsRequest jobDispatchPostEventsRequest) {
    try {
      log.info("[postJobEvents] Request - {}", jsonHelper.pojoToJson(jobDispatchPostEventsRequest));
      jobEventLogControllerApi
          .insertJobEventLog(
              jobDispatchMapper.mapToJobEventLogRequest(jobDispatchPostEventsRequest))
          .block();
    } catch (WebClientResponseException exception) {
      log.error(
          "Job dispatch Service PostJobEvents API{} - {}",
          ErrorCode.JOB_DISPATCH_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
      throw new NetworkException(
          exception.getResponseBodyAsString(),
          ErrorCode.JOB_DISPATCH_SERVICE_NETWORK_ERROR.getCode());
    }
  }
}
