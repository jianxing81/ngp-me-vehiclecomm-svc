package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class MDTIVDDeviceConfigAPIResponse implements Serializable {

  @Serial private static final long serialVersionUID = 1L;
  private String ipAddress;
}
