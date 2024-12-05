package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MdtPowerUpApiRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String messageId;
  private String mobileNo;
  private Integer ivdNo;
  private LocalDateTime timeStamp;
  private String firmwareVersion;
  private String modelNo;
  private List<FileDetails> fileList;
  private String selfTestResult;
  private String imsi;
  private String vehiclePlateNum;
  private String telcoId;
  private String meterVersion;
  private String tariffCheckSum;
  private String firmwareCheckSum;
  private String gisVersion;
  private String mapVersion;
  private String screenSaverVersion;
  private String jobNoBlockStart;
  private String jobNoBlockEnd;
  private IvdInfoDetail ivdInfoList;
  private String ipAddr;
  private Double offsetLatitude;
  private Double offsetLongitude;
  private Double speed;
  private Integer heading;
  private String serialNo;
  private Boolean isAckRequired;

  @Data
  public static class FileDetails implements Serializable {
    @Serial private static final long serialVersionUID = -6088152956520748663L;

    private String fileName;
    private String fileCheckSum;
  }

  @Data
  public static class IvdInfoDetail implements Serializable {
    @Serial private static final long serialVersionUID = -6088152956520748663L;

    private String ivdModel;
    private String ivdParam;
    private String ivdValue;
  }
}
