package com.cdg.pmg.ngp.me.vehiclecomm.techframework;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/** The type Tech framework application. */
@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
public class TechFrameworkApplication {
  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    SpringApplication.run(TechFrameworkApplication.class, args);
  }
}
