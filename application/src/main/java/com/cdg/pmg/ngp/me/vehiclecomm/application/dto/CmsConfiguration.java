package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** cms configuration dto */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmsConfiguration {
  private Long offsetMultiplier;
  private Double longitudeOrigin;
  private Double latitudeOrigin;
  private Integer storeForwardLifeSpan;
  private String storeForwardEvents;
  private Integer jobDispInclMpInfoFlag;
  private Integer jobCancelTime;
  private Integer jobConfirmTime;
  private String autoAcceptSuspendMessage;
  private String autoBidSuspendMessage;
}
