package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.*;

/** Response of fare tariff details from the Fare service */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FareTariffResponse implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private DataDTO data;

  @NoArgsConstructor
  @Data
  public static class DataDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private Double bookingFee;
    private Integer levy;
    private Boolean gstInclusive;
    private Boolean collectFare;
    private Double gstAmount;
    private List<TariffList> tariffList;
  }
}