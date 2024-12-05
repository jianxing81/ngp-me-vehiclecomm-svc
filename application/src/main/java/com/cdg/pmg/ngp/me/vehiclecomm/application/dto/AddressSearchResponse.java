package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressSearchResponse {
  private String addressRef;
  private String building;
  private String buildingType;
  private String block;
  private String road;
  private String postCode;
}
