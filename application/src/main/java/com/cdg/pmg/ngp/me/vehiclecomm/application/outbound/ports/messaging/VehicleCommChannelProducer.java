package com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.messaging;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobOffersResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.IvdMessageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.*;

/** The interface Vehicle comm producer. */
public interface VehicleCommChannelProducer {
  void sendFailedEvent(VehicleCommFailedRequest vehicleCommFailedRequest);

  void sendTripUploadEvent(TripInfo tripInfo);

  void sendToIVDResponseEvent(IvdResponseData ivdResponseData);

  void sendNotificationMessageEvent(String userId, NotificationMessage notificationMessage);

  void sendJobDispatchEvent(JobOffersResponse jobOffersResponse, String ivdNo, String ipAddress);

  void sendToRcsaEvent(RcsaEventProducerData rcsaEventProducerData);

  void sendToRcsaMessageEvent(RcsaMessageEventProducerData rcsaMessageEventProducerData);

  void sendToIvdJobEvent(IvdMessageRequest ivdMessageRequest);
}
