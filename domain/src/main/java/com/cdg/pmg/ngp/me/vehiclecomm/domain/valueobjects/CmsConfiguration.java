package com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmsConfiguration {

  private Long offsetMultiplier;
  private Long coordinateMultiplier;
  private Double longitudeOrigin;
  private Double latitudeOrigin;
  private String storeForwardEvents;
}
