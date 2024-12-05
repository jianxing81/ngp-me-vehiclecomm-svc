package com.cdg.pmg.ngp.me.vehiclecomm.domain.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ZoneInfoDetails extends BaseEntity<String> {
  private String zoneIvdDesc;
  private Integer roofTopIndex;
}
