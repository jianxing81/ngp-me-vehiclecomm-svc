package com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.converters;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.valueobjects.SchedulerPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

/** This class acts as a bridge to convert scheduler payload to a JSON string and vice versa */
@Converter
@RequiredArgsConstructor
@Component
public class JsonAttributeConverter implements AttributeConverter<SchedulerPayload, String> {

  private final ObjectMapper objectMapper;

  @Override
  @SneakyThrows
  public String convertToDatabaseColumn(SchedulerPayload attribute) {
    if (Objects.isNull(attribute)) {
      return null;
    }
    return objectMapper.writeValueAsString(attribute);
  }

  @Override
  @SneakyThrows
  public SchedulerPayload convertToEntityAttribute(String dbData) {
    if (Objects.isNull(dbData)) {
      return null;
    }
    return objectMapper.readValue(dbData, SchedulerPayload.class);
  }
}
