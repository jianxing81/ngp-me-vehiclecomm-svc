package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.driverevent.DriverEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbjobevent.EsbJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbregularreportevent.EsbRegularReportEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbvehicleevent.EsbVehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.jobdispatchevent.JobDispatchEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.notificationmessageevent.NotificationMessageEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.producercsaevent.ProduceRcsaEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.rcsaevent.RcsaEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.rcsamessageevent.RcsaMessageEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.tripupload.UploadTripEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.vehiclecommfailedrequest.VehicleCommFailedRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.vehicleevent.VehicleEvent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, // Jackson will use the unqualified class name as the type value
    property = "eventType" // Jackson will use eventType as the type key
    )
@JsonSubTypes({
  @JsonSubTypes.Type(value = EsbJobEvent.class, name = "EsbJobEvent"),
  @JsonSubTypes.Type(value = VehicleCommFailedRequest.class, name = "VehicleCommFailedRequest"),
  @JsonSubTypes.Type(value = UploadTripEvent.class, name = "UploadTripEvent"),
  @JsonSubTypes.Type(value = JobDispatchEvent.class, name = "JobDispatchEvent"),
  @JsonSubTypes.Type(value = IvdResponse.class, name = "IvdResponse"),
  @JsonSubTypes.Type(value = EsbVehicleEvent.class, name = "EsbVehicleEvent"),
  @JsonSubTypes.Type(value = NotificationMessageEvent.class, name = "NotificationMessageEvent"),
  @JsonSubTypes.Type(value = ProduceRcsaEvent.class, name = "ProduceRcsaEvent"),
  @JsonSubTypes.Type(value = RcsaEvent.class, name = "RcsaEvent"),
  @JsonSubTypes.Type(value = RcsaMessageEvent.class, name = "RcsaMessageEvent"),
  @JsonSubTypes.Type(value = VehicleEvent.class, name = "VehicleEvent"),
  @JsonSubTypes.Type(value = DriverEvent.class, name = "DriverEvent"),
  @JsonSubTypes.Type(value = EsbRegularReportEvent.class, name = "EsbRegularReportEvent"),
})
@Data
public class ParentRequest {
  private UUID eventId = UUID.randomUUID();
  private LocalDateTime occurredAt = LocalDateTime.now();
}
