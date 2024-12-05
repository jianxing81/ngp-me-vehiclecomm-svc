package com.cdg.pmg.ngp.me.vehiclecomm.application.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ObjectUtils {

  private final ObjectMapper objectMapper;

  public ObjectUtils(@Qualifier("jsonEncoder") ObjectMapper jsonEncoder) {
    this.objectMapper = jsonEncoder;
  }

  /**
   * Method which converts a pojo into a map
   *
   * @param pojo pojo
   * @return Map<String, Object>
   * @param <T> Generic input pojo
   */
  public <T> Map<String, Object> pojoToMap(T pojo) {
    try {
      return objectMapper.convertValue(pojo, new TypeReference<>() {});
    } catch (Exception e) {
      log.error("[pojoToMap] Error while converting pojo to map", e);
    }
    return Collections.emptyMap();
  }
}
