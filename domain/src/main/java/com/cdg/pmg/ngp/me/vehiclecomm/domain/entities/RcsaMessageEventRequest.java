package com.cdg.pmg.ngp.me.vehiclecomm.domain.entities;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RequestServiceTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommApplicationCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommFailedRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RcsaMessageEventRequest implements VehicleCommApplicationCommand {
  @NotNull(message = "ivdNo is required")
  private Integer ivdNo;

  private String driverId;

  private String vehicleId;

  private DeviceType deviceType;

  @NotBlank(message = "ipAddr is required")
  private String ipAddr;

  @NotNull(message = "msgId is required")
  private Integer msgId;

  private String messageSerialNo;

  private RequestServiceTypeEnum requestServiceType;

  private String canMessageId;

  private String commandVariable;

  private String msgContent;

  private String messageType;

  @Override
  public Class<? extends DomainEvent> fetchFailedEventClass() {
    return VehicleCommFailedRequest.class;
  }
}
