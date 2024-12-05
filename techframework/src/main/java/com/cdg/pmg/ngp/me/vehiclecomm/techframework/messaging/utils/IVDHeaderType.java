package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import lombok.Getter;

@Getter
public enum IVDHeaderType {

  /** Common Header + IVD Report Data (12 bytes) */
  COMM_RPTDATA(12),

  /** Common Header + IVD Report data + Timestamp (17 bytes) + Sequence Number */
  COMM_RPTDATA_TSTMP(17),

  /** Common Header + Timestamp (9 bytes) + Sequence Number */
  COMM_TSTMP(9);

  private int length;

  IVDHeaderType(int length) {
    this.length = length;
  }
}
