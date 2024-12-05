package com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.repositories;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.entities.SchedulerEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulerRepository extends JpaRepository<SchedulerEntity, Long> {
  List<SchedulerEntity> findByEventName(String eventName);
}
