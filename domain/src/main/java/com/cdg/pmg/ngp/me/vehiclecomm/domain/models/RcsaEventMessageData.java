package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RcsaEventMessageData implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private Integer selection;
  private String uniqueMsgId;
  private String msgContent;
}
