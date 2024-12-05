package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.config;

import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.VehicleEventStrategy;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdVehicleEventEnum;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@Slf4j
public class VehicleEventStrategyConfig {
  private final List<VehicleEventStrategy> vehicleEventStrategies;

  @Bean
  public Map<IvdVehicleEventEnum, VehicleEventStrategy> sendVehicleEventByType() {
    Map<IvdVehicleEventEnum, VehicleEventStrategy> vehicleEventByType =
        new EnumMap<>(IvdVehicleEventEnum.class);
    vehicleEventStrategies.forEach(
        vehicleEventStrategy ->
            vehicleEventByType.put(vehicleEventStrategy.vehicleEventType(), vehicleEventStrategy));
    return vehicleEventByType;
  }
}
