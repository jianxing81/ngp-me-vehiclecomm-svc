package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.model;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.NotificationChannel;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.NotificationStatus;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.NotificationTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ParentRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessageEvent<T> extends ParentRequest {

  private String eventType;
  private String notificationEventType;
  private String recipientDeviceToken;
  private String recipientPhoneNumber;
  private String recipientEmail;
  private NotificationTypeEnum type;
  private String content;
  private String pushApis;
  private String targetApp;
  private String userId;
  private String userType;

  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  private List<NotificationChannel> channel;

  private NotificationTemplateData template;
  private NotificationMetadata<T> metadata;

  private NotificationStatus status;
  private String errorMessage;
  private String errorCode;
}
