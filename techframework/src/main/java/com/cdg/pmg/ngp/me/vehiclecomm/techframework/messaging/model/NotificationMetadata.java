package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMetadata<T> {

  private String subject;
  private String userId;
  private String userType;
  private List<Attachment> attachments;
  private String from;
  private T placeHolder;
  private String httpMethod;
  private String broadcastType;
}
