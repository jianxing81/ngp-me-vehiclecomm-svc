package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serializable;
import lombok.Data;

@Data
public class ExtraStopsInfo implements Serializable {

  private String extraStopName;
  private Integer extraStopQty;
  private String extraStopDetail;
}
