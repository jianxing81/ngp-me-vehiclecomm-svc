package com.cdg.pmg.ngp.me.vehiclecomm.domain.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class BaseEntity<T extends Serializable> implements Serializable {

  protected T id;

  protected String createdBy;

  protected LocalDateTime createdDt;

  protected String updatedBy;

  protected LocalDateTime updatedDt = LocalDateTime.now();

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final BaseEntity<?> that = (BaseEntity<?>) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
