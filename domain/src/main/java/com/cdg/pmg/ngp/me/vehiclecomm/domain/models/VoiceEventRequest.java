package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoiceEventRequest implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private Integer ivdNo;
  private String ipAddress;
  private Integer id;
  private Integer duration;
}
