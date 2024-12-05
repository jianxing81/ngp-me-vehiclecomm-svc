package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.LocalDateTimeDeSerializer;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.LocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class KafkaObjectMapper extends ObjectMapper {

  public KafkaObjectMapper(
      LocalDateTimeSerializer localDateTimeSerializer,
      LocalDateTimeDeSerializer localDateTimeDeSerializer) {

    JavaTimeModule javatimeModule = new JavaTimeModule();

    javatimeModule.addSerializer(localDateTimeSerializer);

    javatimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeSerializer);

    this.registerModule(javatimeModule)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        .configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false)
        .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
  }
}
