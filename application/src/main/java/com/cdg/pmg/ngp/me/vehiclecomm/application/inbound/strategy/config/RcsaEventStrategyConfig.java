package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.config;

import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.RcsaEventStrategy;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
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
public class RcsaEventStrategyConfig {

  private final List<RcsaEventStrategy> rcsaEventStrategies;

  @Bean
  public Map<IvdMessageEnum, RcsaEventStrategy> sendRcsaEventByType() {
    Map<IvdMessageEnum, RcsaEventStrategy> rcsaEventByType = new EnumMap<>(IvdMessageEnum.class);
    rcsaEventStrategies.forEach(
        rcsaEventStrategy ->
            rcsaEventByType.put(rcsaEventStrategy.ivdMessageEnum(), rcsaEventStrategy));
    return rcsaEventByType;
  }
}
