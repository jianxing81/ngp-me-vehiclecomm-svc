package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Data;

@Data
public class MdtRequestCommand implements Serializable {

  @Serial private static final long serialVersionUID = 6432775842153817149L;

  @NotNull(message = "ByteString is required")
  private String byteString;

  @NotNull(message = "EventDate is required")
  private Instant eventDate;
}
