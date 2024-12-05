package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RetryableEvents;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventRetryMapper {

  RetryableEvents mapToDomainEnum(
      com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.RetryableEvents
          retryableEvents);
}
