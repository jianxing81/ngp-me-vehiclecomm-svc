package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models;

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
public class VehicleDetailsAPIResponse implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private String vehicleId;
  private String ipAddress;
}
