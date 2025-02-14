package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request fields for calling the JobDispatch service */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobNoBlockRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String vehicleId;
  private String jobNoBlockStart;
  private String jobNoBlockEnd;
}
