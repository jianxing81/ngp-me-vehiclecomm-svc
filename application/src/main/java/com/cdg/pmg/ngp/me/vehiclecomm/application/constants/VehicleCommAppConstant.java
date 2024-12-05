package com.cdg.pmg.ngp.me.vehiclecomm.application.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Constant value fields */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VehicleCommAppConstant {
  public static final String STORE_FORWARD_MSG_CACHE_NAME = "STORE_FORWARD_MSG";
  public static final String VEHICLE_COMM_PAYMENT_METHOD = "VEHICLE_COMM_PAYMENT_METHOD";
  public static final String VEHICLE_COMM_BOOKING_PRODUCT = "VEHICLE_COMM_BOOKING_PRODUCT";
  public static final String VEHICLE_COMM_IN_VEHICLE_DEVICE_CODE =
      "VEHICLE_COMM_IN_VEHICLE_DEVICE_CODE";
  public static final String REDUNDANT_MSG_CACHE = "REDUNDANT_MSG_CACHE";
  public static final String PAYMENT_METHOD_API_LOG_EXCEPTION =
      "Exception in v1.0/payment-method API call:";
  public static final String BOOKING_PRODUCT_API_LOG_EXCEPTION =
      "Exception in  /v1.0/booking/product/ API call:";
  public static final String DRIVER = "driver";
  public static final String LANG_EN = "EN";
  public static final String DEFAULTS = "DEFAULTS";

  public static final String MDT = "MDT";
  public static final int AUTO_BID_COMMAND_TYPE = 5;
  public static final String COMMAND_TYPE_FIVE = "5";
  public static final String COMMAND_TYPE_FOUR = "4";
  public static final int AUTO_BID_STATUS_SUSPEND = 1;
  public static final int AUTO_BID_BTN_ENABLE_SUSPEND = 1;
  public static final boolean IS_LOGIN_EVENT = Boolean.FALSE;
  public static final boolean IS_LOGOUT_EVENT = Boolean.FALSE;
  public static final int BYTES_START_LENGTH_FOR_VOICE_STREAM = 4;
  public static final int BYTES_END_LENGTH_FOR_VOICE_STREAM = 15;
  public static final String REPORT_TOTAL_MILEAGE_EVENT_ID = "212";
  public static final String JOB_NUMBER_BLOCK_REQUEST_ZERO_VALUE = "0";
  public static final int JOB_CONFIRM_AUTO_ACCEPT_FLAG = 2;
  /* For LOGGING Message*/
  public static final String LOG_MESSAGE_ID_ALREADY_PROCESS = "Message ID {} is already processed";
  public static final String INVALID_EVENT_ID = "Invalid event ID";
  public static final String NEW_LINE = "\n";
  public static final Integer COMMAND_MESSAGE = 109;
  public static final Integer SIMPLE_MESSAGE = 40;
  public static final String SIMPLE_MESSAGE_SERIAL_NUMBER = "35000";

  public static final String DRIVER_SUSPEND = "DriverSuspend";
  public static final String DRIVER_SUSPEND_EVENT = "DRIVER_SUSPEND";
  public static final String SPACE_SEPARATOR = " ";
  public static final String MINUTES = "Minutes";
  public static final String CAN_MESSAGE_VALUE_TWO = "2";
  public static final String CAN_MESSAGE_VALUE_ONE = "1";
  public static final String AUTOBID_STATUS_EVENT = "AUTOBID_STATUS";
  public static final String POWERUP_ERR_UNKNOWN = "99";
  public static final String ADVANCE = "ADVANCE";
  public static final String DOMAIN_EXCEPTION = "Domain exception";
  public static final Integer REGULAR_REPORT_EVENT_ID = 136;
  public static final Integer AUTOBID_EVENT_ID = 2001;
  public static final String PRODUCT_ID_STD001 = "STD001";
  public static final String FARE_CALCULATION_REQUEST_STREET_JOB_NO = "0";
  public static final String DASH = "_";
  public static final String INVALID_LOCATION = "Driver location is invalid";

  public static final String EXTRA_DISTANCE = "EXDT";
  public static final String EXTRA_STOP = "EXST";
}
