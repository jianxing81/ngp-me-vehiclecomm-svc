package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.IvdInfo;
import java.io.Serial;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyOtpAPIResponse implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String passApproval;
  private IvdInfo ivdInfo;
}
