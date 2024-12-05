package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MdtResponseCommand implements Serializable {

  @Serial private static final long serialVersionUID = 6432775842153817149L;

  private String byteString;
  private int byteArraySize;
  private byte[] byteArray;
  private String eventDate;
}
