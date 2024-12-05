package com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.*;

/** The type Logbook config. */
@Configuration
public class LogbookConfig {

  /**
   * Config log book logbook.
   *
   * @return the logbook
   */
  @Bean
  public Logbook configLogBook() {
    return Logbook.builder()
        .sink(new DefaultSink(new SplunkHttpLogFormatter(), new DefaultHttpLogWriter()))
        .condition(Conditions.exclude(Conditions.requestTo("/actuator/**")))
        .queryFilter(QueryFilters.defaultValue())
        .headerFilter(HeaderFilters.defaultValue())
        .headerFilter(
            HeaderFilters.removeHeaders(
                "traceparent", "tracestate", HttpHeaders.CONNECTION, HttpHeaders.TRANSFER_ENCODING))
        .bodyFilter(BodyFilters.defaultValue())
        .requestFilter(RequestFilters.defaultValue())
        .responseFilter(ResponseFilters.defaultValue())
        .build();
  }
}
