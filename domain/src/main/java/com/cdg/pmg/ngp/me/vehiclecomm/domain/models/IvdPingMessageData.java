package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.PingMessageEvent;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IvdPingMessageData {
  private PingMessageEvent pingMessageEvent;
  private LocalDateTime timestamp;
}
