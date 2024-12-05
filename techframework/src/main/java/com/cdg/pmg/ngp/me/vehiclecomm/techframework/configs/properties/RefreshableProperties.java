package com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/** Configuration class for retrieving configs from CMS. */
@Configuration
@Setter
@Getter
@RefreshScope
@ConfigurationProperties
public class RefreshableProperties {
  /** Offset multiplier from cms */
  private Long offsetMultiplier;

  /** Coordinate multiplier from cms */
  private Long coordinateMultiplier;

  /** Longitude origin from cms */
  private Double longitudeOrigin;

  /** latitude origin from cms */
  private Double latitudeOrigin;

  /** store forward life span from cms */
  private Integer storeForwardLifeSpan; // ##

  /** store forward life span from cms */
  private Integer redundantMessageLifeSpan; // ##

  /** store forward events from cms */
  private String storeForwardEvents; // ##

  private String jobDispInclMpInfoFlag;

  /** sJob cancel time from cms */
  private Integer jobCancelTime;

  /** Job confirm time from cms */
  private Integer jobConfirmTime;

  /** Suspend Message */
  private String autoBidSuspendMessage;

  /** Suspend Message */
  private String autoAcceptSuspendMessage;
}
