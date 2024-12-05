package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VehicleCommFrameworkConstants {
  public static final int BUF_SIZE = 65536;
  public static final String ADVANCE_JOB_TYPE = "ADVANCE";
  public static final String OPENAPI = "OPENAPI";
  public static final String IVR_P = "IVR_P";
  public static final String Y = "y";
  public static final int ENABLE_AUTO_BID = 1;
  public static final int IPADDR_LEN = 15;
  public static final int JOB_DISP_INCL_MP_INFO_FLAG = 1;
  public static final String LOG_BRACKETS_BRACKETS = "{} - {}";
  public static final String DEFAULT_IP = "0.0.0.0.0";
  public static final String REGULAR_REPORT_CONSUMER_CLASS = "EsbRegularReportEventConsumer";
  public static final String REGULAR_REPORT_CONVERTER = "regularConverter";
}
