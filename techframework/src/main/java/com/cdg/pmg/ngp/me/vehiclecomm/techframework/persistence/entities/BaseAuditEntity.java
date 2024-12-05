package com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditEntity implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @CreatedDate
  @Column(name = "created_dt", updatable = false, nullable = false)
  @Builder.Default
  private LocalDateTime createdDt = LocalDateTime.now();

  @UpdateTimestamp
  @Column(name = "updated_dt")
  @Builder.Default
  private LocalDateTime updatedDt = LocalDateTime.now();
}
