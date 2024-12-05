package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ByteDataRepresentation;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class GenericByteData extends ByteDataRepresentation {

  // Additional fields in logon
  private Integer mobileId;
  private LocalDateTime logonTimeStamp;
  private String driverPin;
  private String pendingOnCallJobNo;
  private String serialNo;
  private String ipAddress;

  // Additional fields in logoff
  private LocalDateTime timeStamp;

  // Additional fields in powerUp
  private String mobileNo;
  private String firmwareVersion;
  private String modelNo;
  private List<GenericByteData.FileDetails> fileList;
  private String selfTestResult;
  private String imsi;
  private String telcoId;
  private String meterVersion;
  private String tariffCheckSum;
  private String firmwareCheckSum;
  private String gisVersion;
  private String mapVersion;
  private String screenSaverVersion;
  private String jobNoBlockStart;
  private String jobNoBlockEnd;
  private GenericByteData.IvdInfoDetail ivdInfoList;

  // Additional Fields for IVD Device Config
  private List<IvdDeviceConfig> ivdDeviceConfig;

  @Getter
  @Setter
  public static class FileDetails implements Serializable {
    private String fileName;
    private String fileCheckSum;
  }

  @Getter
  @Setter
  public static class IvdInfoDetail implements Serializable {
    private String ivdModel;
    private String ivdParam;
    private String ivdValue;
  }
}
