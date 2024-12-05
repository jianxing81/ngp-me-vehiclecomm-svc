package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;

public interface JobEventStrategy {
  void handleJobEvent(EsbJob esbJob, String ivdJobEventTopic);

  IvdMessageEnum jobEventType();
}
