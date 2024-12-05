package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JsonHelper {

  private final ObjectMapper jsonEncoder;

  public JsonHelper(@Qualifier("jsonEncoder") ObjectMapper jsonEncoder) {
    this.jsonEncoder = jsonEncoder;
  }

  public <T> String pojoToJson(T pojo) {
    try {
      return jsonEncoder.writeValueAsString(pojo);
    } catch (Exception e) {
      log.error("[pojoToJson] Error while converting pojo to json", e);
    }
    return null;
  }

  public String exceptionToJsonString(Exception ex) {
    try {
      return jsonEncoder.writeValueAsString(ex);
    } catch (Exception e) {
      log.error("[pojoToJson] Error while converting exception to json", e);
    }
    return null;
  }
}
