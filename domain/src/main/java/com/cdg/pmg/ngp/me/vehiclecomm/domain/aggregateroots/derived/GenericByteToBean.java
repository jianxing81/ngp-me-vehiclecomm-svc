package com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.AggregateRoot;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.ByteData;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericByteToBean extends AggregateRoot<String> {

  private String eventId;
  private String hexString;
  private ByteData byteData;
  private transient Map<String, Object> responseMap;

  @Override
  public boolean equals(final Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
