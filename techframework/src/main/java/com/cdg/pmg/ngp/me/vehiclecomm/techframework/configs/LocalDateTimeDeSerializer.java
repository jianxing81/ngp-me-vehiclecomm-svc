package com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.utils.Helper;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/**
 * The built-in serializers in the {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeModule} can
 * not overcome the Schema Validation process. So we need to customize one to adapt the validation
 * requirements.
 */
@Component
public class LocalDateTimeDeSerializer extends StdDeserializer<LocalDateTime> {

  public LocalDateTimeDeSerializer() {
    super(LocalDateTime.class);
  }

  /**
   * Serializes the {@link LocalDateTime} value to the ISO-8601 string representation without time
   * zone.
   */
  /**
   * Serializes the {@link LocalDateTime} value to the ISO-8601 string representation without time
   * zone.
   */
  @Override
  public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return Helper.parseLocaleDate(p.getValueAsString());
  }
}
