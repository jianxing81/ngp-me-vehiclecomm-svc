package com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ByteArrayData implements Serializable {

  @Serial private static final long serialVersionUID = 7725773171700538445L;

  private byte[] byteArray;
  private Integer byteArraySize;
  private String byteArrayMessage;
}
