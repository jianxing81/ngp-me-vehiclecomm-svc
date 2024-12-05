package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.config;

import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.GenericByteToBeanStrategy;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ByteToBeanEvent;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ByteToBeanConverterStrategyConfig {

  private final List<GenericByteToBeanStrategy> genericByteToBeanStrategyList;

  @Bean
  public Map<ByteToBeanEvent, GenericByteToBeanStrategy> byteToBeanStrategies() {
    return genericByteToBeanStrategyList.stream()
        .collect(Collectors.toMap(GenericByteToBeanStrategy::getEventId, Function.identity()));
  }
}
