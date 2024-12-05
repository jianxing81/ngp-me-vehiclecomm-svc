package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EsbVehicleData implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private UUID eventId;
  private String eventIdentifier;
  private LocalDateTime eventDate;
  private ByteData byteData;

  @Data
  @AllArgsConstructor
  @Builder
  public static class ByteData implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
    private Integer ivdNo;
    private String jobNo;
    private String code;
    private String mobileNo;
    private String driverId;
    private Double offsetX;
    private Double offsetY;
    private LocalDateTime eventTime;
    private Double speed;
    private Integer heading;
    private Integer serialNumber;
    private Integer messageId;
    private Integer refNo;
    private Integer seqNo;
    private String eventContent;
    private String zoneId;
    private String oldPin;
    private String newPin;
    private String emergencyId;

    /**
     * from vehicleComm UCD <a href="https://comfortdelgrotaxi.atlassian.net/wiki/x/UwZBT">
     * 4.2.1.15.16 Report Total Mileage Event Use Case Design Document </a>
     */
    private Integer totalMileage;
  }
}
