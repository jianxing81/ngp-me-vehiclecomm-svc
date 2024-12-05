package com.cdg.pmg.ngp.me.vehiclecomm.techframework.cache.configs;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Data
@Validated
@ConfigurationProperties(prefix = "app.redis")
public class RedisServerProperties {
  private Integer port;
  private String host;
  private String username;
  private String password;
  private boolean ssl;
  private RedisCluster cluster;

  @Data
  public static class RedisCluster {
    private List<String> nodes;
  }
}
