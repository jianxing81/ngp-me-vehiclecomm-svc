package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.JobEventLogEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.OriginatorEnum;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;

/** Request fields for calling the JobDispatch service */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDispatchPostEventsRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String jobNo;
  private String driverId;
  private String vehicleId;
  private JobEventLogEvent event;
  private OriginatorEnum originator;
  private String eventMessage;
  private LocalDateTime eventTime;
}
