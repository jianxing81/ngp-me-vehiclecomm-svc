package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedundantMessageKeyData {

  private UUID eventID;
  private Integer messageID;
  private String uniqueKey;
}
