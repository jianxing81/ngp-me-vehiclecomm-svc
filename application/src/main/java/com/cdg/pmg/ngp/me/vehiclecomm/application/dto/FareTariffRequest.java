package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FareTariffRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private String jobNo;
  private String vehicleId;
  private String ivdCode;
  private String corpCardNo;
  private String tripNo;
  private String payment;
  private List<FareTariffInfo> tarifflist;
  private String accountId;
}
