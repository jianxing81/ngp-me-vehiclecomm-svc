package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RequestServiceTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RcsaMessageEventProducerData extends DomainEvent {

  @Serial private static final long serialVersionUID = 7816611451460267759L;

  private Integer ivdNo;
  private String driverId;
  private String vehicleId;
  private DeviceType deviceType;
  private String ipAddr;
  private Integer msgId;
  private String messageSerialNo;
  private RequestServiceTypeEnum requestServiceType;
  private String canMessageId;
  private String commandVariable;
  private String msgContent;
  private String messageType;
}
