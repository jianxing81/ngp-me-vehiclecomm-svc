package com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobDispatchDetailsRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobDispatchPostEventsRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobOffersResponseData;

/* Internal class for wrapper API calls to Job dispatch Service*/
public interface JobDispatchAPIService {
  /**
   * This method is used to call job dispatch service
   *
   * @param jobNo - api request
   * @param jobDispatchDetailsRequest jobDispatchDetailsRequest
   */
  void getDriverResponse(String jobNo, JobDispatchDetailsRequest jobDispatchDetailsRequest);

  /**
   * This method is used to call job-offers
   *
   * @param vehicleId - api request
   */
  JobOffersResponseData getJobOffersResponse(String vehicleId, String driverId);

  /**
   * This method is used to call job dispatch event service
   *
   * @param jobDispatchPostEventsRequest jobDispatchPostEventsRequest
   */
  void postJobEvents(JobDispatchPostEventsRequest jobDispatchPostEventsRequest);
}
