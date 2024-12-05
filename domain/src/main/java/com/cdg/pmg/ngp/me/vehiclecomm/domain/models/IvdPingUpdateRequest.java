package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IvdPingUpdateRequest {
  private List<IvdPingMessageData> ivdPingData;
  private String requestNumber;
  private String sequenceNumber;
  private String messageId;
  private Integer ivdNo;
}
