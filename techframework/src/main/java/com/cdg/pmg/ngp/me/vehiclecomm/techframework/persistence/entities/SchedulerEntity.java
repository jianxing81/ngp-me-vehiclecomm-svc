package com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.entities;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.converters.JsonAttributeConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.enums.RetryStatus;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.valueobjects.SchedulerPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vehicle_comm_scheduler")
@ToString
@Setter
@Getter
public class SchedulerEntity extends BaseAuditEntity {

  @Serial private static final long serialVersionUID = 3L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_comm_scheduler_seq")
  @SequenceGenerator(
      name = "vehicle_comm_scheduler_seq",
      sequenceName = "SEQ_vehicle_comm_scheduler__vehicle_comm_scheduler_id",
      allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @Column(name = "event_name")
  private String eventName;

  @Column(name = "payload", columnDefinition = "jsonb")
  @Convert(converter = JsonAttributeConverter.class)
  @ColumnTransformer(write = "?::jsonb")
  private SchedulerPayload payload;

  @Column(name = "remarks")
  private String remarks;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private RetryStatus status;
}
