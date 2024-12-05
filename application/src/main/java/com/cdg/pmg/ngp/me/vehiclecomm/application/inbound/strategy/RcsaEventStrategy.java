package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Rcsa;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;

public interface RcsaEventStrategy {
  void handleRcsaEvent(Rcsa rcsa, String rcsaEventTopic);

  IvdMessageEnum ivdMessageEnum();
}
