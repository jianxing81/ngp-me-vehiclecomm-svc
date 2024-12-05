package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateData {

  private String lang;
  private String jobType;
  private String customerType;
  private String productType;
  private String eventType;
  private String channel;
}
