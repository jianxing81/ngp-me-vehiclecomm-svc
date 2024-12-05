package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobDispatchDetailsRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobDispatchPostEventsRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobOffersResponseData;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.models.HandleDriverRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.models.JobEventLogRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.models.JobOffersResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobDispatchMapper {

  HandleDriverRequest mapToHandleDriverRequest(JobDispatchDetailsRequest jobDispatchDetailsRequest);

  JobEventLogRequest mapToJobEventLogRequest(
      JobDispatchPostEventsRequest jobDispatchPostEventsRequest);

  JobOffersResponseData mapToJobOffersResponse(JobOffersResponse jobOffersResponse);
}
