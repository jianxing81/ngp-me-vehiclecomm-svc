package com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
@ComponentScan(
    basePackages = {"com.cdg.pmg.ngp.me.vehiclecomm"},
    includeFilters =
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = ServiceComponent.class))
@RequiredArgsConstructor
public class BeanConfig {

  private final LocalDateTimeSerializer localDateTimeSerializer;
  private final LocalDateTimeDeSerializer localDateTimeDeSerializer;

  /**
   * New object mapper object mapper.
   *
   * @return the object mapper
   */
  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    JavaTimeModule javatimeModule = new JavaTimeModule();
    javatimeModule.addSerializer(localDateTimeSerializer);
    javatimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeSerializer);
    return new ObjectMapper()
        .registerModule(javatimeModule)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        .configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
  }

  /**
   * This bean is created exclusively for logging requests and response in JSON format. A new bean
   * was created to set the default property inclusion to not null, so that the null values in a
   * POJO won't appear as key value pairs in logs
   *
   * @return ObjectMapper
   */
  @Bean("jsonEncoder")
  public ObjectMapper jsonEncoder() {
    JavaTimeModule javatimeModule = new JavaTimeModule();
    javatimeModule.addSerializer(localDateTimeSerializer);
    javatimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeSerializer);
    return new ObjectMapper()
        .registerModule(javatimeModule)
        .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        .configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
  }

  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasenames("messages");
    messageSource.setDefaultEncoding("UTF-8");
    messageSource.setDefaultLocale(Locale.ENGLISH);
    return messageSource;
  }
}
