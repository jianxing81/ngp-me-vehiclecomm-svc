package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/** CLass to generate App Status Sync message request to driver app. */
@Data
@Builder
@AllArgsConstructor
public class SendAppStatusSyncRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String vehicleId;
  private String driverId;
  private String appstatus;
  private Integer type;
}
