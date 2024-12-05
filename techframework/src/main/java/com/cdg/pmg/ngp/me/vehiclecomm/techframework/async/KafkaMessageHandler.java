package com.cdg.pmg.ngp.me.vehiclecomm.techframework.async;

import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.port.VehicleCommApplicationService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Driver;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Rcsa;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.RcsaMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.JobDispatchEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMessageHandler {

  private final VehicleCommApplicationService vehiclecommApplicationService;

  @Async("commonEventExecutor")
  @SneakyThrows
  public void handleDriverEvent(Driver driverInboundEvent, String driverEventTopic) {
    vehiclecommApplicationService.processDriverSuspendEvent(driverInboundEvent, driverEventTopic);
  }

  @Async("ivdJobEventExecutor")
  @SneakyThrows
  public void handleEsbJobEvent(EsbJob ivdInboundEvent, String ivdJobEventTopic) {
    vehiclecommApplicationService.processJobEvent(ivdInboundEvent, ivdJobEventTopic);
  }

  @Async("ivdRcsaEventExecutor")
  @SneakyThrows
  public void handleEsbRcsaEvent(Rcsa rcsaInboundEvent, String rcsaEventTopic) {
    vehiclecommApplicationService.processRcsaEvents(rcsaInboundEvent, rcsaEventTopic);
  }

  @Async("ivdVehicleEventExecutor")
  @SneakyThrows
  public void esbVehicleEventHandler(EsbVehicle ivdInboundEvent, String ivdVehicleEventTopic) {
    vehiclecommApplicationService.processVehicleEvent(ivdInboundEvent, ivdVehicleEventTopic, false);
  }

  @Async("ivdRegularReportEventExecutor")
  @SneakyThrows
  public void esbRegularReportEventHandler(
      EsbVehicle ivdInboundEvent, String ivdRegularReportEventTopic) {
    vehiclecommApplicationService.processVehicleEvent(
        ivdInboundEvent, ivdRegularReportEventTopic, true);
  }

  @Async("commonEventExecutor")
  @SneakyThrows
  public void handleJobDispatchEvent(
      JobDispatchEventRequest jobEventRequest, String jobEventTopic) {
    vehiclecommApplicationService.sendJobDispatchEvent(jobEventRequest, jobEventTopic);
  }

  @Async("commonEventExecutor")
  @SneakyThrows
  public void handleRcsaMessageEvent(
      RcsaMessage rcsaMessageInboundEvent, String rcsaMessageEventTopic) {
    vehiclecommApplicationService.processRcsaMessageEvent(
        rcsaMessageInboundEvent, rcsaMessageEventTopic);
  }

  @Async("commonEventExecutor")
  @SneakyThrows
  public void handleVehicleEvent(
      VehicleEventRequest vehicleMessageInboundEvent, String vehicleMessageEventTopic) {
    vehiclecommApplicationService.sendVehicleMessageEvent(
        vehicleMessageInboundEvent, vehicleMessageEventTopic);
  }
}
