package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serializable;
import lombok.Data;

@Data
public class MultiStop implements Serializable {

  private String intermediateAddr;
  private Double intermediateLat;
  private Double intermediateLng;
  private String intermediatePt;
  private String intermediateZoneId;
}
