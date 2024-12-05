package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobStatusEnum {
  PENDING(1, "PENDING"),
  CONFIRMED(2, "CONFIRMED"),
  FAILED(3, "FAILED"),
  ONBOARD(4, "ONBOARD"),
  ARRIVAL(5, "ARRIVAL"),
  NO_SHOW(6, "NO_SHOW"),
  CANCELLED(7, "CANCELLED"),
  COMPLETED(8, "COMPLETED"),
  CANCELLED_UR(9, "CANCELLED_UR"),
  NO_SHOW_UR(10, "NO_SHOW_UR"),

  /*
     The enums defined down below are done as part of ME-2709. Since we do not expect a status called CONFIRMING,
     we will send a notification/event to App/MDT with the ID of the status corresponding to CONFIRMED, however the other
     status are added to have a proper mapping from the auto generated class created from JSON Schema to the domain DTO.
  */
  CONFIRMING(2, "CONFIRMING"),
  NEW(0, "NEW"),
  NTA(0, "NTA"),
  MODIFY(0, "MODIFY"),
  STC_COMPLETED(0, "STC_COMPLETED");

  private final int id;
  private final String status;
}
