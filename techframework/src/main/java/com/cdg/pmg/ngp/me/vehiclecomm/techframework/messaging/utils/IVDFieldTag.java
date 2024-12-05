package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesSize;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.TagByteSize;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Enum class for IVD message field tags. */
@Getter
@AllArgsConstructor
public enum IVDFieldTag {

  /** Account Company Name (0x0001), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  ACCOUNT_COMPANY_NAME(0x0001, "accountCompanyName"),
  /** Account ID (0x0002), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  ACCOUNT_ID(0x0002, "accountId"),
  /** Acknowledgement (0x0003), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  ACKNOWLEDGEMENT(0x0003, "acknowledgement"),
  /** Address Info (0x0004), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  ADDRESS_INFO(0x0004, "addressInfo"),
  /** Admin Fee (0x0005), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  ADMIN_FEE(0x0005, "adminFee"),
  /** Alert Sound (0x0006), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  ALERT_SOUND(0x0006, "alertSound"),
  /** Amount Paid (0x0007), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  AMOUNT_PAID(0x0007, "amountPaid"),
  /** App Name (0x0A41), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  APP_NAME(0x0A41, "appName"),
  /** App Version (0x0A42), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  APP_VERSION(0x0A42, "appVersion"),
  /** Approval Code (0x0008), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  APPROVAL_CODE(0x0008, "approvalCode"),
  /** Arrival Required (0x0009), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  ARRIVAL_REQUIRED(0x0009, "arrivalRequired"),
  /** Arrive Before Call Out (0x000A), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  ARRIVE_BEFORE_CALL_OUT(0x000A, "arriveBeforeCallOut"),
  /** Auto Busy Driver Reject (0x000B), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  AUTO_BUSY_DRIVER_REJECT(0x000B, "autoBusyDriverReject"),
  /** Auto Busy Timeout (0x000C), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  AUTO_BUSY_TIMEOUT(0x000C, "autoBusyTimeout"),
  /** Auto Close Offer (0x00CB), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  AUTO_CLOSE_OFFER(0x00CB, "autoCloseOffer"),
  /** Auto Logoff Timeout (0x00B2), normally represented as {@link BytesSize#TIME} */
  @TagByteSize(size = BytesSize.TIME)
  AUTO_LOGOFF_TIMEOUT(0x00B2, "autoLogoffTimeout"),
  /** Auto reject count (0x00B8), normally represented as {@link BytesSize#UINT16} */
  @TagByteSize(size = BytesSize.UINT16)
  AUTO_REJECT_COUNT(0x00B8, "autoRejectCount"),
  /** Balance Due (0x000D), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  BALANCE_DUE(0x000D, "balanceDue"),
  /** Bitmap Number (0x0A75), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  BITMAP_NUMBER(0x0A75, "bitmapNumber"),
  /** Booking Channel (0x000E), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  BOOKING_CHANNEL(0x000E, "bookingChannel"),
  /** Booking Fee (0x000F), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  BOOKING_FEE(0x000F, "bookingFee"),
  /** Cab Rewards Flag (0x00BB), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  CAB_REWARDS_FLAG(0x00BB, "cabRewardsFlag"),
  /** Cab Rewards Amt (0x00BC), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  CAB_REWARDS_AMT(0x00BC, "cabRewardsAmount"),
  /** Call Out Status (0x0010), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  CALL_OUT_STATUS(0x0010, "callOutStatus"),
  /** Callout After ETA (0x0011), normally represented as {@link BytesSize#TIME}. */
  @TagByteSize(size = BytesSize.TIME)
  CALLOUT_AFTER_ETA(0x0011, "calloutAfterEta"),
  /** CAN Message ID (0x0012), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  CAN_MESSAGE_ID(0x0012, "canMessageId"),
  /** Card Expiry Date (0x0013), normally represented as {@link BytesSize#TEXT} . */
  @TagByteSize(size = BytesSize.TEXT)
  CARD_EXPIRY_DATE(0x0013, "cardExpiryDate"),
  /** Card Number (0x0014), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  CARD_NUMBER(0x0014, "cardNumber"),
  /** CashlessApp (0x0A40), normally represented as {@link BytesSize#}. */
  CASHLESSAPP(0x0A40, "cashlessapp"),
  /** CashlessApp Param (0x0A43), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  CASHLESSAPP_PARAM(0x0A43, "cashlessappParam"),
  /** CashlessApp Value (0x0A44), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  CASHLESSAPP_VALUE(0x0A44, "cashlessappValue"),
  /** Change PIN Result (0x0015), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  CHANGE_PIN_RESULT(0x0015, "changePinResult"),
  /** CJ Offer Timeout (0x0016), normally represented as {@link BytesSize#TIME} . */
  @TagByteSize(size = BytesSize.TIME)
  CJ_OFFER_TIMEOUT(0x0016, "cjOfferTimeout"),
  /** Collect Fare (0x00C6), normally represented as {@link BytesSize#BOOLEAN} */
  @TagByteSize(size = BytesSize.BOOLEAN)
  COLLECT_FARE(0x00C6, "collectFare"),
  /** Colour (0x0A72), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BYTE)
  COLOUR(0x0A72, "colour"),
  /** Command Type (0x0017), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  COMMAND_TYPE(0x0017, "commandType"),
  /** Command Variable (0x0018), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BYTE)
  COMMAND_VARIABLE(0x0018, "commandVariable"),
  /** Company ID (0x0019), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  COMPANY_ID(0x0019, "companyId"),
  /** Convert Street Hail Response (0x00C4, normally represented as {@link BytesSize#BYTE} */
  @TagByteSize(size = BytesSize.BYTE)
  CONVERT_STREET_HAIL_RESPONSE(0X00C4, "convertStreetHailResponse"),

  /**
   * SCREEN_SAVER_VER (0x00C7, normally represented as {@link BytesSize#TEXT} FOR check sum security
   * check purpose
   */
  @TagByteSize(size = BytesSize.TEXT)
  SCREEN_SAVER_VER(0X00C7, "screenSaverVer"),
  /** CoordX (0x0A82), normally represented as {@link BytesSize#COORD}. */
  @TagByteSize(size = BytesSize.COORD)
  COORDX(0x0A82, "coordx"),
  /** CoordY (0x0A83), normally represented as {@link BytesSize#COORD}. */
  @TagByteSize(size = BytesSize.COORD)
  COORDY(0x0A83, "coordy"),
  /** Corporate Card Number (0x001A), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  CORPORATE_CARD_NUMBER(0x001A, "corporateCardNumber"),
  /** MasterPass Card Expiry Date (0x0013), normally represented as {@link BytesSize#TEXT} . */
  @TagByteSize(size = BytesSize.TEXT)
  CC_EXPIRY_DATE(0x00B5, "ccExpiryDate"),
  /** MasterPass Card Number (0x0014), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  CC_NUMBER(0x00B4, "ccNumber"),
  /** Customer Priority (0x001B), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  CUSTOMER_PRIORITY(0x001B, "customerPriority"),
  /** Deposit (0x001C), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  DEPOSIT(0x001C, "deposit"),
  /** Destination Address (0x001D), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  DESTINATION_ADDRESS(0x001D, "destinationAddress"),
  /** Destination Show Offer Screen (0x001E), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  DESTINATION_SHOW_OFFER_SCREEN(0x001E, "destinationShowOfferScreen"),
  /** Destination X (0x001F), normally represented as {@link BytesSize#COORD}. */
  @TagByteSize(size = BytesSize.COORD)
  DESTINATION_X(0x001F, "destinationX"),
  /** Destination Y (0x0020), normally represented as {@link BytesSize#COORD}. */
  @TagByteSize(size = BytesSize.COORD)
  DESTINATION_Y(0x0020, "destinationY"),

  /** TO DEFINE IT AS MULTI STOP, normally represented as {@link BytesSize#}. */
  MULTI_STOP(0x0B10, "multiStop"),
  /** Intermediate Destination X (0x0020), normally represented as {@link BytesSize#COORD}. */
  @TagByteSize(size = BytesSize.COORD)
  MULTI_STOP_X(0x0B12, "intermediateX"),
  /** Intermediate Destination Y (0x0020), normally represented as {@link BytesSize#COORD}. */
  @TagByteSize(size = BytesSize.COORD)
  MULTI_STOP_Y(0x0B13, "intermediateY"),
  /** Intermediate Destination ADDRESS */
  @TagByteSize(size = BytesSize.TEXT)
  MULTI_STOP_ADDRESS(0x0B11, "multiStopAddress"),

  /** Device (0x0A51), normally represented as {@link BytesSize#TEXT}. */
  DEVICE(0x0A51, "device"),
  /** Device Attribute ID (0x0AF4), normally represented as {@link BytesSize#INT16}. */
  @TagByteSize(size = BytesSize.INT16)
  DEVICE_ATTRIBUTE_ID(0x0AF4, "deviceAttributeId"),
  /** Device Attribute Value (0x0AF5), normally represented as bytes array. */
  DEVICE_ATTRIBUTE_VALUE(0x0AF5, "deviceAttributeValue"),
  /** Device Component ID (0x0AF2), normally represented as {@link BytesSize#INT16}. */
  @TagByteSize(size = BytesSize.INT16)
  DEVICE_COMPONENT_ID(0x0AF2, "deviceComponentId"),
  /** Device Configuration List (0x0AF0), normally represented as {@link BytesSize#}. */
  DEVICE_CONFIG_LIST(0x0AF0, "deviceConfigList"),
  /** Device Facility ID (0x0AF3), normally represented as {@link BytesSize#INT16}. */
  @TagByteSize(size = BytesSize.INT16)
  DEVICE_FACILITY_COMP_ID(0x0AF3, "deviceFacilityCompId"),
  /** Device Info (0x0A50), normally represented as {@link BytesSize#}. */
  DEVICE_INFO(0x0A50, "deviceInfo"),
  /** Device Info Param (0x0A53), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  DEVICE_INFO_PARAM(0x0A53, "deviceInfoParam"),
  /** Device Info Type (0x0AF1), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  DEVICE_INFO_TYPE(0x0AF1, "deviceInfoType"),
  /** Device Info Value (0x0A54), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  DEVICE_INFO_VALUE(0x0A54, "deviceInfoValue"),
  /** Discount (0x0021), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  DISCOUNT(0x0021, "discount"),
  /** Dispatch Fee (0x0022), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  DISPATCH_FEE(0x0022, "dispatchFee"),
  /** Dispatch Method (0x0023), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  DISPATCH_METHOD(0x0023, "dispatchMethod"),
  /** Driver IC (0x0024), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  DRIVER_IC(0x0024, "driverIc"),
  /** Driver ID (0x0025), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  DRIVER_ID(0x0025, "driverId"),
  /** Driver Name (0x0026), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  DRIVER_NAME(0x0026, "driverName"),
  /** Driver Option (0x0AB0), normally represented as {@link BytesSize#}. */
  DRIVER_OPTION(0x0AB0, "driverOption"),
  /** Driver PIN (0x0027), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  DRIVER_PIN(0x0027, "driverPin"),
  /** Duration (0x0028), normally represented as {@link BytesSize#TIME}. */
  @TagByteSize(size = BytesSize.TIME)
  DURATION(0x0028, "duration"),
  /** Duration in seconds (0x00B9), normally represented as {@link BytesSize#UINT16} */
  @TagByteSize(size = BytesSize.UINT16)
  DURATION_IN_SECS(0x00B9, "durationInSecs"),
  /** Emergency ID (0x0029), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  EMERGENCY_ID(0x0029, "emergencyId"),
  /** Enforce Modification (0x002A), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  ENFORCE_MODIFICATION(0x002A, "enforceModification"),
  /** Entry Mode (0x002B), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  ENTRY_MODE(0x002B, "entryMode"),
  /** ERP Fee (0x002C), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  ERP_FEE(0x002C, "erpFee"),
  /** ETA (0x002D), normally represented as {@link BytesSize#TIME}. */
  @TagByteSize(size = BytesSize.TIME)
  ETA(0x002D, "eta"),
  /** Event Binary Data (0x002E). */
  EVENT_BINARY_DATA(0x002E, "eventBinaryData"),
  /** Event Category (0x002F), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  EVENT_CATEGORY(0x002F, "eventCategory"),
  /** Event Content (0x00BE), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  EVENT_CONTENT(0x00BE, "eventContent"),
  /** Event Date Time (0x0030), normally represented as {@link BytesSize#DATETIME}. */
  @TagByteSize(size = BytesSize.DATETIME)
  EVENT_DATE_TIME(0x0030, "eventDateTime"),
  /** Event ID (0x0031), normally represented as {@link BytesSize#UINT32}. */
  @TagByteSize(size = BytesSize.UINT32)
  EVENT_ID(0x0031, "eventId"),
  /** Event Log Description (0x00B3), normally represented as {@link BytesSize#TEXT} */
  @TagByteSize(size = BytesSize.TEXT)
  EVENT_LOG_DESCRIPTION(0x00B3, "eventLogDescription"),
  /** Event Severity (0x0032), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  EVENT_SEVERITY(0x0032, "eventSeverity"),
  /** Event Source (0x0033), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  EVENT_SOURCE(0x0033, "eventSource"),
  /** Extended Offer Display Time (0x00C9), normally represented as {@link BytesSize#TIME}. */
  @TagByteSize(size = BytesSize.TIME)
  EXTENDED_OFFER_DISPLAY_TIME(0x00C9, "extendedOfferDisplayTime"),
  /** Extra Stop Detail (0x0A22), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  EXTRA_STOP_DETAIL(0x0A22, "extraStopDetail"),
  /** Extra Stop Name (0x0A21), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  EXTRA_STOP_NAME(0x0A21, "extraStopName"),
  /** Extra Stop Quatity (0x0A23), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  EXTRA_STOP_QUANTITY(0x0A23, "extraStopQuantity"),
  /** Extra Stops (0x0A20), normally represented as {@link BytesSize#}. */
  EXTRA_STOPS(0x0A20, "extraStops"),
  /** False Alarm Type (0x0034), normally represented as {@link BytesSize#BYTE} . */
  @TagByteSize(size = BytesSize.BYTE)
  FALSE_ALARM_TYPE(0x0034, "falseAlarmType"),
  /** Fare Adjustment (0x0035), normally represented as {@link BytesSize#MONEY} . */
  @TagByteSize(size = BytesSize.MONEY)
  FARE_ADJUSTMENT(0x0035, "fareAdjustment"),
  /**
   * FARE TYPE (0x00C5, normally represented as {@link BytesSize#BYTE} Tag Format 1 = Meter Fare 2 =
   * Fixed Fare 3 = Driver Input 4 = Flat Fare
   */
  @TagByteSize(size = BytesSize.BYTE)
  FARE_TYPE(0X00C5, "fareType"),
  /** File Checksum (0x0A32), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  FILE_CHECKSUM(0x0A32, "fileChecksum"),
  /** Filename (0x0A31), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  FILE_FILENAME(0x0A31, "fileFilename"),
  /** File (0x0A30), normally represented as {@link BytesSize#}. */
  FILE_LIST(0x0A30, "fileList"),
  /** Firmware Checksum (0x0036), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  FIRMWARE_CHECKSUM(0x0036, "firmwareChecksum"),
  /** Fixed Price (0x0037), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  FIXED_PRICE(0x0037, "fixedPrice"),
  /** FORGOT_PWD_RESULT (0x00E4), normally represented as {@link BytesSize#TEXT} */
  @TagByteSize(size = BytesSize.TEXT)
  FORGOT_PWD_RESULT(0x00E9, "forgotPwdResult"),

  /** GIS Version (0x0038), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  GIS_VERSION(0x0038, "gisVersion"),
  /** GST (0x0039), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  GST(0x0039, "gst"),
  /** GST Inclusive (0x003A), normally represented as {@link BytesSize#BOOLEAN} . */
  @TagByteSize(size = BytesSize.BOOLEAN)
  GST_INCLUSIVE(0x003A, "gstInclusive"),
  /** Hired Distance (0x003B), normally represented as {@link BytesSize#DISTANCE}. */
  @TagByteSize(size = BytesSize.DISTANCE)
  HIRED_DISTANCE(0x003B, "hiredDistance"),
  /** Hotspot (0x0A80), normally represented as {@link BytesSize#}. */
  HOTSPOT(0x0A80, "hotspot"),
  /** Hotspot Name (0x0A81), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  HOTSPOT_NAME(0x0A81, "hotspotName"),
  /** IMSI (0x003C), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  IMSI(0x003C, "imsi"),
  /** IP Address (0x003D), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  IP_ADDRESS(0x003D, "ipAddress"),
  /** IVD Event Description (0x0AD0), normally represented as {@link BytesSize#}. */
  IVD_EVENT_DESCRIPTION(0x0AD0, "ivdEventDescription"),
  /** IVD Firmware Version (0x003E), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  IVD_FIRMWARE_VERSION(0x003E, "ivdFirmwareVersion"),
  /** IVD Info (0x0A60), normally represented as {@link BytesSize#}. */
  IVD_INFO(0x0A60, "ivdInfo"),
  /** IVD Model (0x003F), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  IVD_INFO_MODEL(0x003F, "ivdInfoModel"),
  /** IVD Model (0x0A61), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  IVD_MODEL(0x0A61, "ivdModel"),
  /** IVD Param (0x0A62), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  IVD_PARAM(0x0A62, "ivdParam"),
  /** IVD Value (0x0A63), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  IVD_VALUE(0x0A63, "ivdValue"),
  /** Job Confirmation Timeout (0x0040), normally represented as {@link BytesSize#TIME}. */
  @TagByteSize(size = BytesSize.TIME)
  JOB_CONFIRMATION_TIMEOUT(0x0040, "jobConfirmationTimeout"),
  /** Job Merit Points (0x0041), normally represented as {@link BytesSize#INT32}. */
  @TagByteSize(size = BytesSize.INT32)
  JOB_MERIT_POINTS(0x0041, "jobMeritPoints"),
  /** Job Modification Response (0x0042), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  JOB_MODIFICATION_RESPONSE(0x0042, "jobModificationResponse"),
  /** Job Notes (0x0043), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  JOB_NOTES(0x0043, "jobNotes"),
  /** Job Number (0x0044), normally represented as {@link BytesSize#UINT32}. */
  @TagByteSize(size = BytesSize.UINT64)
  JOB_NUMBER(0x0044, "jobNumber"),
  /** Job Number Block End (0x0045), normally represented as {@link BytesSize#UINT32}. */
  @TagByteSize(size = BytesSize.UINT64)
  JOB_NUMBER_BLOCK_END(0x0045, "jobNumberBlockEnd"),
  /** Job Number Block Start (0x0046), normally represented as {@link BytesSize#UINT32}. */
  @TagByteSize(size = BytesSize.UINT64)
  JOB_NUMBER_BLOCK_START(0x0046, "jobNumberBlockStart"),
  /** Job Number End (0x0047), normally represented as {@link BytesSize#UINT32} . */
  @TagByteSize(size = BytesSize.UINT64)
  JOB_NUMBER_END(0x0047, "jobNumberEnd"),
  /** Job Number Start (0x0048), normally represented as {@link BytesSize#UINT32}. */
  @TagByteSize(size = BytesSize.UINT64)
  JOB_NUMBER_START(0x0048, "jobNumberStart"),
  /** Job Query Result Code (0x0049), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  JOB_QUERY_RESULT_CODE(0x0049, "jobQueryResultCode"),
  /** Job Sync Event (0x00D0), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  JOB_SYNC_EVENT(0x00D0, "jobSyncEvent"),
  /**
   * Auto Bid Flag (0x00D1), normally represented as {@link BytesSize#BOOLEAN}. Indicates whether
   * the job offer is auto bid job to driver or not
   */
  @TagByteSize(size = BytesSize.BYTE)
  AUTO_BID_FLAG(0x00D1, "autoBidFlag"),
  @TagByteSize(size = BytesSize.BYTE)
  AUTO_BID_DEFAULT_VALUE_FLAG(0x00DB, "autoBidDefaultValueFlag"),
  /** Auto Bid Job Offer Timeout (0x00D2), normally represented as {@link BytesSize#TIME}. */
  @TagByteSize(size = BytesSize.TIME)
  AUTO_BID_JOB_OFFER_TMOUT(0x00D2, "autoBidJobOfferTmout"),
  /** WAIVED_LEVY_AMT (0x00D3), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  WAIVED_LEVY_AMT(0x00D3, "waivedLevyAmt"),
  /** DISP_CALL_OUT_BTN (0x00D4), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  DISP_CALL_OUT_BTN(0x00D4, "dispCallOutBtn"),
  /** STC_TRACKING_DIST (0x00D5), normally represented as {@link BytesSize#INT32}. */
  @TagByteSize(size = BytesSize.INT32)
  STC_TRACKING_DIST(0x00D5, "stcTrackingDist"),
  /** CBD Surcharge Flag (0x00D6), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  CBD_SURCHARGE_FLG(0x00D6, "cbdSurchargeFlg"),
  /** CBD Surcharge Amount (0x00D7), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  CBD_SURCHARGE_AMT(0x00D7, "cbdSurchargeAmt"),
  /** Dynamic Pricing indicator (0x00D8), normally represented as {@link BytesSize#INT32}. */
  @TagByteSize(size = BytesSize.INT32)
  DYNA_PRC_INDICATOR(0x00D8, "dynaPrcIndicator"),
  /**
   * Private Field (0x00D9), normally represented as {@link BytesSize#TEXT}. This is to support
   * In-App payment for NOF(Nets-On-File), PayLah-On File, or future inapp payment related. Maximum
   * is 50 ACII
   */
  @TagByteSize(size = BytesSize.TEXT)
  PRIVATE_FIELD(0x00D9, "privateField"),
  /**
   * Private Field (0x00DA), normally represented as {@link BytesSize#MONEY}. Amount send back from
   */
  @TagByteSize(size = BytesSize.MONEY)
  ADMIN_FEE_DISCOUNT(0x00DA, "adminFeeDiscount"),
  /** Job Type (0x004A), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  JOB_TYPE(0x004A, "jobType"),
  /** Levy (0x004B), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  LEVY(0x004B, "levy"),
  /** Location Check Radius (0x004C), normally represented as {@link BytesSize#DISTANCE}. */
  @TagByteSize(size = BytesSize.DISTANCE)
  LOCATION_CHECK_RADIUS(0x004C, "locationCheckRadius"),
  /** Log Off Status (0x004D), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  LOG_OFF_STATUS(0x004D, "logOffStatus"),
  /** Log On Status (0x004E), normally represented as {@link BytesSize#CHAR}. */
  @TagByteSize(size = BytesSize.CHAR)
  LOGON_STATUS(0x004E, "logonStatus"),
  /** Loyalty Status (0x004F), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  LOYALTY_STATUS(0x004F, "loyaltyStatus"),
  /** MapData version (0x0050), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  MAPDATA_VERSION(0x0050, "mapdataVersion"),
  /** Maximum Speed (0x0051), normally represented as {@link BytesSize#SPEED}. */
  @TagByteSize(size = BytesSize.SPEED)
  MAXIMUM_SPEED(0x0051, "maximumSpeed"),
  /** MDT Sync Blocking Interval (0x00C0), normally represented as {@link BytesSize#TIME}. */
  @TagByteSize(size = BytesSize.TIME)
  MDT_SYNC_BLOCKING_INTERVAL(0x00C0, "mdtSyncBlockingInterval"),
  /** MDT Sync Flag (0x00C1), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  MDT_SYNC_FLAG(0x00C1, "mdtSyncFlag"),
  /** Message (0x0A71), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  MESSAGE(0x0A71, "message"),
  /** Message Content (0x0052), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  MESSAGE_CONTENT(0x0052, "messageContent"),
  /** Message Selection (0x0053), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  MESSAGE_SELECTION(0x0053, "messageSelection"),
  /** Message Serial No (0x0054), normally represented as {@link BytesSize#UINT16}. */
  @TagByteSize(size = BytesSize.UINT16)
  MESSAGE_SERIAL_NO(0x0054, "messageSerialNo"),
  /** Meter Edit Flag (0x0055), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  METER_EDIT_FLAG(0x0055, "meterEditFlag"),
  /** Meter Extra (0x0056), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  METER_EXTRA(0x0056, "meterExtra"),
  /** Meter Fare (0x0057), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  METER_FARE(0x0057, "meterFare"),
  /** Meter Off Timestamp (0x0058), normally represented as {@link BytesSize#DATETIME}. */
  @TagByteSize(size = BytesSize.DATETIME)
  METER_OFF_TIMESTAMP(0x0058, "meterOffTimestamp"),
  /** Meter On Timestamp (0x0059), normally represented as {@link BytesSize#DATETIME}. */
  @TagByteSize(size = BytesSize.DATETIME)
  METER_ON_TIMESTAMP(0x0059, "meterOnTimestamp"),
  /** Meter Read Error (0x005A), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  METER_READ_ERROR(0x005A, "meterReadError"),
  /** Meter Version (0x005B), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  METER_VERSION(0x005B, "meterVersion"),
  /** MOBILE_NUMBER (0x00E8), normally represented as {@link BytesSize#INT32}. */
  @TagByteSize(size = BytesSize.TEXT)
  MOBILE_NUMBER(0x00E8, "mobileNumber"),

  /** Multi line Text (0x0B00), normally represented as {@link BytesSize#TEXT}. */
  MULTI_LINE_TEXT(0x0B00, "multiLineText"),
  /** Need Job Confirmation (0x005C), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  NEED_JOB_CONFIRMATION(0x005C, "needJobConfirmation"),
  /** New Offer Job Number (0x00CC), normally represented as {@link BytesSize#UINT32}. */
  @TagByteSize(size = BytesSize.UINT64)
  NEW_OFFER_JOB_NUMBER(0x00CC, "newOfferJobNumber"),
  /** New PIN (0x005D), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  NEW_PIN(0x005D, "newPin"),
  /** No Show ETA (0x005E), normally represented as {@link BytesSize#TIME}. */
  @TagByteSize(size = BytesSize.TIME)
  NO_SHOW_ETA(0x005E, "noShowEta"),
  /** No Show Event Type (0x005F), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  NO_SHOW_EVENT_TYPE(0x005F, "noShowEventType"),
  /** No Show Result (0x0060), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  NO_SHOW_RESULT(0x0060, "noShowResult"),
  /** No Show Timeout (0x0061), normally represented as {@link BytesSize#TIME}. */
  @TagByteSize(size = BytesSize.TIME)
  NO_SHOW_TIMEOUT(0x0061, "noShowTimeout"),
  /** No Field Tag Assigned (0x000) */
  NONE(0x0000, "none"),
  /**
   * Number of Callout failure before No Show (0x0062), normally represented as {@link
   * BytesSize#BYTE}.
   */
  @TagByteSize(size = BytesSize.BYTE)
  NUMBER_OF_CALLOUT_FAILURE_BEFORE_NO_SHOW(0x0062, "numberOfCalloutFailureBeforeNoShow"),
  /** Number of Pending AJs (0x00A8), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  NUMBER_OF_PENDING_AJS(0x00A8, "numberOfPendingAjs"),
  /** Offer Response (0x0AC0), normally represented as {@link BytesSize#}. */
  OFFER_RESPONSE(0x0AC0, "offerResponse"),
  /** Offer Response Name (0x0AC1), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  OFFER_RESPONSE_NAME(0x0AC1, "offerResponseName"),
  /** Offer Response Type (0x0AC2), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  OFFER_RESPONSE_TYPE(0x0AC2, "offerResponseType"),
  /** Offer Response Value (0x0AC3), normally represented as {@link BytesSize#INT16}. */
  @TagByteSize(size = BytesSize.INT16)
  OFFER_RESPONSE_VALUE(0x0AC3, "offerResponseValue"),
  /** Offline (0x0063), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  OFFLINE(0x0063, "offline"),
  /** Old PIN (0x0064), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  OLD_PIN(0x0064, "oldPin"),
  /** Option Key (0x0AB1), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  OPTION_KEY(0x0AB1, "optionKey"),
  /** Option Value (0x0AB2), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  OPTION_VALUE(0x0AB2, "optionValue"),
  /** Paper Voucher (0x0065), normally represented as {@link BytesSize#BOOLEAN} . */
  @TagByteSize(size = BytesSize.BOOLEAN)
  PAPER_VOUCHER(0x0065, "paperVoucher"),
  /** Passenger Contact Number (0x0066), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  PASSENGER_CONTACT_NUMBER(0x0066, "passengerContactNumber"),
  /** Passenger Name (0x0067), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  PASSENGER_NAME(0x0067, "passengerName"),
  /** Payment Announcement Message (0x00CA), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  PAYMENT_ANNOUNCEMENT_MESSAGE(0x00CA, "paymentAnnouncementMessage"),
  /** Payment Info (0x00CD), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  PAYMENT_INFO(0x00CD, "paymentInfo"),
  /** Payment Description (0x00A4), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  PAYMENT_DESCRIPTION(0x00B0, "paymentDescription"),
  /** Payment Method (0x0068), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  PAYMENT_METHOD(0x0068, "paymentMethod"),
  /** Payment Method Name (0x00C8), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  PAYMENT_METHOD_NAME(0x00C8, "paymentMethodName"),
  /** Payment Module IP (0x0069), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  PAYMENT_MODULE_IP(0x0069, "paymentModuleIp"),
  /** Payment Module Port (0x006A), normally represented as {@link BytesSize#UINT16}. */
  @TagByteSize(size = BytesSize.UINT16)
  PAYMENT_MODULE_PORT(0x006A, "paymentModulePort"),
  /** Pending AJ Response Code (0x00A7), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  PENDING_AJ_RESPONSE_CODE(0x00A7, "pendingAjResponseCode"),
  /** Pending AJ Response Type (0x00A6), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  PENDING_AJ_RESPONSE_TYPE(0x00A6, "pendingAjResponseType"),
  /** Pending AJ Type (0x00A9), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  PENDING_AJ_TYPE(0x00A9, "pendingAjType"),
  /** Pending Oncall Job Status (0x006C), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  PENDING_ONCALL_JOB_STATUS(0x006C, "pendingOncallJobStatus"),
  /** Pending Oncall Job Number (0x006B), normally represented as {@link BytesSize#UINT32}. */
  @TagByteSize(size = BytesSize.UINT64)
  PENDING_ONCALL_JOBNO(0x006B, "pendingOncallJobno"),
  /** PERFORMANCE CARD Flag (0x00C2), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  PERFORMANCE_CARD_FLAG(0x00C2, "performanceCardFlag"),
  /** Pickup Address (0x006D), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  PICKUP_ADDRESS(0x006D, "pickupAddress"),
  /** Pickup Time (0x006E), normally represented as {@link BytesSize#DATETIME}. */
  @TagByteSize(size = BytesSize.DATETIME)
  PICKUP_TIME(0x006E, "pickupTime"),
  /** Pickup X (0x006F), normally represented as {@link BytesSize#COORD}. */
  @TagByteSize(size = BytesSize.COORD)
  PICKUP_X(0x006F, "pickupX"),
  /** Pickup Y (0x0070), normally represented as {@link BytesSize#COORD}. */
  @TagByteSize(size = BytesSize.COORD)
  PICKUP_Y(0x0070, "pickupY"),
  /** Premier Allowed (0X00C3), normally represented as {@link BytesSize#BOOLEAN} */
  @TagByteSize(size = BytesSize.BOOLEAN)
  PREMIER_ALLOWED(0X00C3, "premierAllowed"),
  /** Product (0x0A00), normally represented as {@link BytesSize#}. */
  PRODUCT(0x0A00, "product"),
  /** Product Code (0x0A01), normally represented as {@link BytesSize#INT16}. */
  @TagByteSize(size = BytesSize.INT16)
  PRODUCT_CODE(0x0A01, "productCode"),
  /** Product ID (0x0071), normally represented as {@link BytesSize#UINT16}. */
  @TagByteSize(size = BytesSize.UINT16)
  PRODUCT_ID(0x0071, "productId"),
  /** Promo code (0x00B6), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  PROMO_CODE(0x00B6, "promoCode"),
  /** Promo amount (0x00B7), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  PROMO_AMOUNT(0x00B7, "promoAmount"),
  /** Reason Code (0x00B1), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  REASON_CODE(0x00B1, "reasonCode"),
  /** Receipt Number (0x0072), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  RECEIPT_NUMBER(0x0072, "receiptNumber"),
  /** Received Message ID (0x0073), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  RECEIVED_MESSAGE_ID(0x0073, "receivedMessageId"),
  /** Received Message SN (0x0074), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  RECEIVED_MESSAGE_SN(0x0074, "receivedMessageSn"),
  /** Reference Number (0x0075), normally represented as {@link BytesSize#BYTE} . */
  @TagByteSize(size = BytesSize.BYTE)
  REFERENCE_NUMBER(0x0075, "referenceNumber"),
  /** Refund (0x0076), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  REFUND(0x0076, "refund"),
  /** Register Rank Action (0x0077), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  REGISTER_RANK_ACTION(0x0077, "registerRankAction"),
  /** Register Rank Type (0x0078), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  REGISTER_RANK_TYPE(0x0078, "registerRankType"),
  /** Reject Reason Code (0x0079), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  REJECT_REASON_CODE(0x0079, "rejectReasonCode"),
  /** Reporting Interval (0x007A), normally represented as {@link BytesSize#TIME}. */
  @TagByteSize(size = BytesSize.TIME)
  REPORTING_INTERVAL(0x007A, "reportingInterval"),
  /** Request Fair On Trip Start (0x007B), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  REQUEST_FAIR_ON_TRIP_START(0x007B, "requestFairOnTripStart"),
  /** Request Fare On Edit (0x007C), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  REQUEST_FARE_ON_EDIT(0x007C, "requestFareOnEdit"),
  /** Request ID (0x007D), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  REQUEST_ID(0x007D, "requestId"),
  /** Request Service Type (0x007E), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  REQUEST_SERVICE_TYPE(0x007E, "requestServiceType"),
  /** Retain IVD Status (0x007F), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  RETAIN_IVD_STATUS(0x007F, "retainIvdStatus"),
  /** Rooftop Message (0x0A70), normally represented as {@link BytesSize#}. */
  ROOFTOP_MESSAGE(0x0A70, "rooftopMessage"),
  /** ScreenSaver Version (0x00C7), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  SCREEN_SAVER_VERSION(0x00C7, "screenSaverVersion"),
  /** Scroll (0x0A73), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  SCROLL(0x0A73, "scroll"),
  /** Self test result (0x0080), normally represented as {@link BytesSize#TEXT} . */
  @TagByteSize(size = BytesSize.TEXT)
  SELF_TEST_RESULT(0x0080, "selfTestResult"),
  /** Sequence (0x0A84), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  SEQUENCE(0x0A84, "sequence"),
  /** Sequence Number (0x0081), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  SEQUENCE_NUMBER(0x0081, "sequenceNumber"),
  /** Serial Number (0x0A52), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  SERIAL_NUMBER(0x0A52, "serialNumber"),
  /** Show Fare Receipt (0x0082), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  SHOW_FARE_RECEIPT(0x0082, "showFareReceipt"),
  /** Show Fare Screen (0x0083), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  SHOW_FARE_SCREEN(0x0083, "showFareScreen"),
  /** Show Offer Screen (0x0084), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  SHOW_OFFER_SCREEN(0x0084, "showOfferScreen"),
  /** Sing-JB (0x0085), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  SING_JB(0x0085, "singJb"),
  /** Single line text (0x0B01), normally represented as {@link BytesSize#TEXT} */
  SINGLE_LINE_TEXT(0x0B01, "singleLineText"),
  /** Sound Alert (0x0086), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  SOUND_ALERT(0x0086, "soundAlert"),
  /** Source (0x0087), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  SOURCE(0x0087, "source"),
  /** Special Fare (0x0088), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  SPECIAL_FARE(0x0088, "specialFare"),
  /** Speed (0x0A74), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  SPEED(0x0A74, "speed"),
  /** Store & Forward Content (0x00A5), normally represented as {@link BytesSize}. */
  STORE_FORWARD_CONTENT(0x00A5, "storeForwardContent"),
  /** Star Driver Flag (0x00BD), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  STAR_DRIVER_FLAG(0x00BD, "starDriverFlag"),
  /** Star Driver Greeting (0x00BF), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  STAR_DRIVER_GREETING(0x00BF, "starDriverGreeting"),
  /** Surcharge (0x0A90), normally represented as {@link BytesSize#}. */
  SURCHARGE(0x0A90, "surcharge"),
  /** Surcharge Amount (0x0A92), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  SURCHARGE_AMOUNT(0x0A92, "surchargeAmount"),
  /** Surcharge ID (0x0A91), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  SURCHARGE_ID(0x0A91, "surchargeId"),
  /** Suspension Reason (0x008A), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  SUSPENSION_REASON(0x008A, "suspensionReason"),
  /** Tariff (0x0A10), normally represented as {@link BytesSize#}. */
  TARIFF(0x0A10, "tariff"),
  /** Tariff Amount (0x0A12), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  TARIFF_AMOUNT(0x0A12, "tariffAmount"),
  /** Tariff Checksum (0x008B), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  TARIFF_CHECKSUM(0x008B, "tariffChecksum"),
  /** Tariff Code (0x0A11), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  TARIFF_CODE(0x0A11, "tariffCode"),
  /** Tariff Quantity (0x0A13), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  TARIFF_QUANTITY(0x0A13, "tariffQuantity"),
  /** Taxi Tour Guide (0x008C), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  TAXI_TOUR_GUIDE(0x008C, "taxiTourGuide"),
  /** Telco ID (0x008D), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  TELCO_ID(0x008D, "telcoId"),
  /** Time Left for Suspension (0x008E), normally represented as {@link BytesSize#TIME}. */
  @TagByteSize(size = BytesSize.TIME)
  TIME_LEFT_FOR_SUSPENSION(0x008E, "timeLeftForSuspension"),
  /** Total Amount (0x008F), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  TOTAL_AMOUNT(0x008F, "totalAmount"),
  /** Total Distance (0x0090), normally represented as {@link BytesSize#DISTANCE}. */
  @TagByteSize(size = BytesSize.DISTANCE)
  TOTAL_DISTANCE(0x0090, "totalDistance"),
  /** Total Mileage (0x0091), normally represented as {@link BytesSize#DISTANCE}. */
  @TagByteSize(size = BytesSize.DISTANCE)
  TOTAL_MILEAGE(0x0091, "totalMileage"),
  /** Total Paid (0x0092), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  TOTAL_PAID(0x0092, "totalPaid"),
  /** Trip End Time (0x0093), normally represented as {@link BytesSize#TIMESTAMP}. */
  @TagByteSize(size = BytesSize.TIMESTAMP)
  TRIP_END_TIME(0x0093, "tripEndTime"),
  /** Trip End X (0x0094), normally represented as {@link BytesSize#COORD}. */
  @TagByteSize(size = BytesSize.COORD)
  TRIP_END_X(0x0094, "tripEndX"),
  /** Trip End Y (0x0095), normally represented as {@link BytesSize#COORD}. */
  @TagByteSize(size = BytesSize.COORD)
  TRIP_END_Y(0x0095, "tripEndY"),
  /** Trip Number (0x0096), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  TRIP_NUMBER(0x0096, "tripNumber"),
  /** Trip Start Time (0x0097), normally represented as {@link BytesSize#TIMESTAMP}. */
  @TagByteSize(size = BytesSize.TIMESTAMP)
  TRIP_START_TIME(0x0097, "tripStartTime"),
  /** Trip Start X (0x0098), normally represented as {@link BytesSize#COORD}. */
  @TagByteSize(size = BytesSize.COORD)
  TRIP_START_X(0x0098, "tripStartX"),
  /** Trip Start Y (0x0099), normally represented as {@link BytesSize#COORD}. */
  @TagByteSize(size = BytesSize.COORD)
  TRIP_START_Y(0x0099, "tripStartY"),
  /** Trip Type (0x009A), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  TRIP_TYPE(0x009A, "tripType"),
  /** TTS Status (0x009B), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  TTS_STATUS(0x009B, "ttsStatus"),
  /** Un-hired Distance (0x009C), normally represented as {@link BytesSize#DISTANCE}. */
  @TagByteSize(size = BytesSize.DISTANCE)
  UNHIRED_DISTANCE(0x009C, "unhiredDistance"),
  /** Update Type (0x009D), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  UPDATE_TYPE(0x009D, "updateType"),
  /** Valid Location Flag (0x009E), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  VALID_LOCATION_FLAG(0x009E, "validLocationFlag"),
  /** Vehicle Plate Number (0x009F), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  VEHICLE_PLATE_NUMBER(0x009F, "vehiclePlateNumber"),
  /** Vehicle Type ID (0x00A0), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  VEHICLE_TYPE_ID(0x00A0, "vehicleTypeId"),
  /** Voice data (0x00A1), normally represented as . */
  VOICE_DATA(0x00A1, "voiceData"),
  /** Voucher (0x0AA0), normally represented as {@link BytesSize#}. */
  VOUCHER(0x0AA0, "voucher"),
  /** Voucher Amount (0x0AA2), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  VOUCHER_AMOUNT(0x0AA2, "voucherAmount"),
  /** Voucher ID (0x0AA1), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  VOUCHER_ID(0x0AA1, "voucherId"),
  /** Voucher Payment Amount (0x00A2), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  VOUCHER_PAYMENT_AMOUNT(0x00A2, "voucherPaymentAmount"),
  /** Voucher ID (0x0AA3), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  VOUCHER_QUANTITY(0x0AA3, "voucherQuantity"),
  /** Waiting Point (0x00A3), normally represented as {@link BytesSize#TEXT}. */
  @TagByteSize(size = BytesSize.TEXT)
  WAITING_POINT(0x00A3, "waitingPoint"),
  /** Duration in seconds (0x00BA), normally represented as {@link BytesSize#UINT16} */
  @TagByteSize(size = BytesSize.UINT16)
  WARNING_COUNT(0x00BA, "warningCount"),
  /** Zone ID (0x00A4), normally represented as {@link BytesSize#BYTE}. */
  @TagByteSize(size = BytesSize.BYTE)
  ZONE_ID(0x00A4, "zoneId"),
  /** AUTO_STC (0x00CE), normally represented as {@link BytesSize#INT32}. */
  @TagByteSize(size = BytesSize.INT32)
  AUTO_STC(0x00CE, "autoStc"),
  /** AUTO_ASSIGN_FLAG (0x00CF), normally represented as {@link BytesSize#BOOLEAN}. */
  @TagByteSize(size = BytesSize.BOOLEAN)
  AUTO_ASSIGN_FLAG(0x00CF, "autoAssignFlag"),

  /** UNIQUE_DRIVER_ID (0x00DD), normally represented as {@link BytesSize#TEXT} */
  @TagByteSize(size = BytesSize.TEXT)
  UNIQUE_DRIVER_ID(0x00DD, "uniqueDriverId"),

  /** Auto accept dialog box timeout (0x00DC), normally represented as {@link BytesSize#INT32} */
  @TagByteSize(size = BytesSize.INT32)
  AUTO_ACCEPT_DIALOG_BOX_TIMEOUT(0x00DC, "autoAcceptDialogBoxTimeout"),

  /** Partner discount type (0x00E1), normally represented as {@link BytesSize#TEXT} */
  @TagByteSize(size = BytesSize.TEXT)
  PARTNER_DISCOUNT_TYPE(0x00E1, "partnerDiscountType"),
  /** partner discount value (0x00E2), normally represented as {@link BytesSize#MONEY} */
  @TagByteSize(size = BytesSize.MONEY)
  PARTNER_DISCOUNT_VALUE(0x00E2, "partnerDiscountValue"),
  /** partner discount amount (0x00E3), normally represented as {@link BytesSize#MONEY} */
  @TagByteSize(size = BytesSize.MONEY)
  PARTNER_DISCOUNT_AMT(0x00E3, "partnerDiscountAmt"),
  /** partner order id (0x00E4), normally represented as {@link BytesSize#TEXT} */
  @TagByteSize(size = BytesSize.TEXT)
  PARTNER_ORDER_ID(0x00E4, "partnerOrderId"),
  /** ADMIN_GST_FLAG(0x00E5), normally represented as {@link BytesSize#BOOLEAN} */
  @TagByteSize(size = BytesSize.BOOLEAN)
  ADMIN_GST_FLAG(0x00E5, "adminGstFlag"),
  @TagByteSize(size = BytesSize.MONEY)
  POLICY_AMOUNT_CAP(0x010E, "policyAmountCap"),
  @TagByteSize(size = BytesSize.MONEY)
  HLA_FEE(0x00F1, "hlaFee"),

  @TagByteSize(size = BytesSize.BOOLEAN)
  IS_FEE_WAIVED(0x00F2, "isFeeWaived"),

  /** TO DEFINE IT AS POLICY CRITERIA LIST, normally represented as {@link BytesSize#}. */
  POLICY_LOCATION_CHECK_CRITERIA_LIST(0x0C00, "policyLocationCheckCriteriaList"),

  @TagByteSize(size = BytesSize.COORD)
  POLICY_PICKUP_X(0x0C01, "policyPickupX"),

  @TagByteSize(size = BytesSize.COORD)
  POLICY_PICKUP_Y(0x0C02, "policyPickupY"),

  @TagByteSize(size = BytesSize.COORD)
  POLICY_DEST_X(0x0C03, "policyDestX"),

  @TagByteSize(size = BytesSize.COORD)
  POLICY_DEST_Y(0x0C04, "policyDestY"),

  @TagByteSize(size = BytesSize.UINT32)
  POLICY_RADIUS(0x0C05, "policyRadius"),

  @TagByteSize(size = BytesSize.BYTE)
  APPROVAL_MODE(0x010F, "approvalMode"),

  @TagByteSize(size = BytesSize.UINT32)
  CURRENT_GST(0x0100, "currentGst"),

  @TagByteSize(size = BytesSize.UINT32)
  NEW_GST(0x0101, "newGst"),

  @TagByteSize(size = BytesSize.DATETIME)
  NEW_GST_EFFECTIVE_DATE(0x0102, "newGstEffectiveDate"),

  @TagByteSize(size = BytesSize.BYTE)
  CURRENT_ADMIN_TYPE(0x0103, "currentAdminType"),

  @TagByteSize(size = BytesSize.UINT32)
  CURRENT_ADMIN_VALUE(0x0104, "currentAdminValue"),

  @TagByteSize(size = BytesSize.BYTE)
  CURRENT_GST_INCLUSIVE(0x0105, "currentGstInclusive"),

  @TagByteSize(size = BytesSize.UINT32)
  CURRENT_ADMIN_GST_MSG(0x0106, "currentAdminGstMsg"),

  @TagByteSize(size = BytesSize.UINT32)
  CURRENT_ADMIN_DISCOUNT_VALUE(0x0107, "currentAdminDiscountValue"),

  @TagByteSize(size = BytesSize.DATETIME)
  NEW_ADMIN_EFFECTIVE_DATE(0x0108, "newAdminEffectiveDate"),

  @TagByteSize(size = BytesSize.BYTE)
  NEW_ADMIN_TYPE(0x0109, "newAdminType"),

  @TagByteSize(size = BytesSize.UINT32)
  NEW_ADMIN_VALUE(0x010A, "newAdminValue"),

  @TagByteSize(size = BytesSize.BYTE)
  NEW_GST_INCLUSIVE(0x010B, "newGstInclusive"),

  @TagByteSize(size = BytesSize.UINT32)
  NEW_ADMIN_GST_MSG(0x010C, "newAdminGstMsg"),

  @TagByteSize(size = BytesSize.UINT32)
  NEW_ADMIN_DISCOUNT_VALUE(0x010D, "newAdminDiscountValue"),

  @TagByteSize(size = BytesSize.BOOLEAN)
  RESET_FLAG(0x00E7, "resetFlag"), // CDG-6291 To force password change

  @TagByteSize(size = BytesSize.BOOLEAN)
  FORGOT_PWD_WITH_OTP(0x0111, "forgotPwdWithOtp"),

  @TagByteSize(size = BytesSize.TEXT)
  OTP(0x0112, "otp"),
  /** Platform Fee (0x0113), normally represented as {@link BytesSize#MONEY}. */
  @TagByteSize(size = BytesSize.MONEY)
  PLATFORM_FEE(0x0113, "platformFee"),

  /** Platform Fee isApplicable (0x0114), normally represented as {@link BytesSize}. */
  @TagByteSize(size = BytesSize.CHAR)
  PLATFORM_FEE_ISAPPLICABLE(0x0114, "platformFeeIsApplicable"),
  /** TO DEFINE IT AS PLATFORM FEE LIST, normally represented as {@link BytesSize#}. */
  PLATFORM_FEE_LIST(0x0D00, "platformFeeList"),

  @TagByteSize(size = BytesSize.CHAR)
  ISAPPLICABLE(0x0D01, "isApplicable"),

  @TagByteSize(size = BytesSize.MONEY)
  THRESHOLD_LIMIT(0x0D02, "thresholdLimit"),

  @TagByteSize(size = BytesSize.MONEY)
  FEE_BELOW_THRESHOLD(0x0D03, "fareBelowThreshold"),

  @TagByteSize(size = BytesSize.MONEY)
  FEE_ABOVE_THRESHOLD(0x0D04, "fareAboveThreshold"),

  ADDITIONAL_CHARGES(0x0E00, "additionalCharges"),

  @TagByteSize(size = BytesSize.INT32)
  CHARGE_ID(0x0E01, "chargeId"),

  @TagByteSize(size = BytesSize.TEXT)
  CHARGE_TYPE(0x0E02, "chargeType"),

  @TagByteSize(size = BytesSize.MONEY)
  CHARGE_AMT(0x0E03, "chargeAmt"),

  @TagByteSize(size = BytesSize.MONEY)
  CHARGE_THRESHOLD(0x0E04, "chargeThreshold"),

  @TagByteSize(size = BytesSize.MONEY)
  CHARGE_UPPER_LIMIT(0x0E05, "chargeUpperLimit"),

  @TagByteSize(size = BytesSize.MONEY)
  CHARGE_LOWER_LIMIT(0x0E06, "chargeLowerLimit"),

  @TagByteSize(size = BytesSize.UINT32)
  TRIP_DISTANCE(0x0117, "tripDistance"),

  @TagByteSize(size = BytesSize.UINT32)
  PICKUP_DISTANCE(0x0118, "pickupDistance"),

  @TagByteSize(size = BytesSize.MONEY)
  NETT_FARE(0x0119, "nettFare");

  /**
   * Returns enum constant for a given field tag id.
   *
   * @param id integer value
   * @return ivdFlag
   */
  public static IVDFieldTag getTag(int id) {
    for (IVDFieldTag tag : IVDFieldTag.values()) {
      if (tag.getId() == id) {
        return tag;
      }
    }
    return IVDFieldTag.NONE;
  }

  private final int id;

  private final String name;
}
