package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.NotificationMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.NotificationRequest;
import java.util.Collections;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(imports = Collections.class, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SendNotificationRequestMapper {

  /**
   * Method for mapping NotificationRequest to NotificationMessage
   *
   * @param notificationRequest notificationRequest
   * @return NotificationMessage
   */
  NotificationMessage map(NotificationRequest notificationRequest);
}
