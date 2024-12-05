package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.config;

import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.JobEventStrategy;
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
public class JobEventStrategyConfig {

  private final List<JobEventStrategy> jobEventStrategies;

  @Bean
  public Map<IvdMessageEnum, JobEventStrategy> sendJobEventByType() {
    Map<IvdMessageEnum, JobEventStrategy> jobEventByType = new EnumMap<>(IvdMessageEnum.class);
    jobEventStrategies.forEach(
        jobEventStrategy -> jobEventByType.put(jobEventStrategy.jobEventType(), jobEventStrategy));
    return jobEventByType;
  }
}
