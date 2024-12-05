package com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.utils.Helper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/**
 * The built-in serializers in the {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeModule} can
 * not overcome the Schema Validation process. So we need to customize one to adapt the validation
 * requirements.
 */
@Component
public class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

  public LocalDateTimeSerializer() {
    super(LocalDateTime.class);
  }

  /**
   * Serializes the {@link LocalDateTime} value to the ISO-8601 string representation without time
   * zone.
   */
  @Override
  public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeString(Helper.formatLocaleDate(value));
  }
}
