package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import lombok.Getter;

/**
 * Enum class to represent different IVD message types. Values from 0 to 127 is for Backend to IVD,
 * while 128 to 255 for IVD to Backend.
 */
@Getter
public enum IVDMessageType {

  /** Acknowledge Convert Street Hail (151) */
  ACKNOWLEDGE_CONVERT_STREET_HAIL(151),

  /** Acknowledge Job Cancellation (153) */
  ACKNOWLEDGE_JOB_CANCELLATION(153),

  /** Acknowledge Job Modification (152) */
  ACKNOWLEDGE_JOB_MODIFICATION(152),

  /** Acknowledge Job Query (175) */
  ACKNOWLEDGE_JOB_QUERY(175),

  /** IVD Event (219) */
  IVD_EVENT(219),
  /** Acknowledge No Show Reject (215) */
  ACKNOWLEDGE_NO_SHOW_REJECT(215),

  /** Advanced Job Remind (169) */
  ADVANCED_JOB_REMIND(169),

  /** Arrival (156) */
  ARRIVAL(156),

  /** Break Update (134) */
  BREAK_UPDATE(134),

  /** Busy Update (135) */
  BUSY_UPDATE(135),

  /** Call Out (158) */
  CALL_OUT(158),

  /** Call Out Result (21) */
  CALL_OUT_RESULT(21),

  /** Change Pin Confirmation (5) */
  CHANGE_PIN_CONFIRMATION(5),

  /** Change Pin Request (150) */
  CHANGE_PIN_REQUEST(150),

  /** Change Shift Update (172) */
  CHANGE_SHIFT_UPDATE(172),

  /** Command Message (109) */
  COMMAND_MESSAGE(109),

  /** Convert Street Hail (017) */
  CONVERT_STREET_HAIL(17),

  /** Corporate Details Request (211) */
  CORPORATE_DETAILS_REQUEST(211),

  /** Crossing Zone (133) */
  CROSSING_ZONE(133),

  /** Driver Performance Request (208) */
  DRIVER_PERFORMANCE_REQUEST(208),

  /** Driver Performance Response (108) */
  DRIVER_PERFORMANCE_RESPONSE(108),

  /** Emergency Closed (34) */
  EMERGENCY_CLOSED(34),

  /** Emergency Initiated (185) */
  EMERGENCY_INITIATED(185),

  /** Emergency Report (164) */
  EMERGENCY_REPORT(164),

  /** False Alarm (186) */
  FALSE_ALARM(186),

  /** Fare Calculation Request (194) */
  FARE_CALCULATION_REQUEST(194),

  /** Fare Calculation Response (39) */
  FARE_CALCULATION_RESPONSE(39),

  // CDG-6549 (Backend) MDT - "forgot password" button on MDT login page - changes starts
  /** Forgot Password Request (226) */
  FORGOT_PASSWORD_REQUEST(226),

  /** Forgot Password Response (29) */
  FORGOT_PASSWORD_RESPONSE(29),
  // CDG-6549 (Backend) MDT - "forgot password" button on MDT login page - changes ends

  /** HEART_BEAT (199) */
  HEART_BEAT(199),

  /** IVD Hardware Info (143) */
  IVD_HARDWARE_INFO(143),

  /** IVD Ping (35) */
  IVD_PING(35),

  /** IVD Ping Response (163) */
  IVD_PING_RESPONSE(163),

  /** Job Accept (154) */
  JOB_ACCEPT(154),

  /** Job Cancellation (14) */
  JOB_CANCELLATION(14),

  /** Job Confirmation (11) */
  JOB_CONFIRMATION(11),

  /** Job Confirmatino Acknowledge (188) */
  JOB_CONFIRMATION_ACKNOWLEDGE(188),

  /** TOC-966 changes starts auto Accept Job Confirmatino Acknowledge (190) */
  AUTO_ACCEPT_JOB_CNF_ACK(190),
  // **TOC-966 changes ends

  /** Job Dispatch (10) */
  JOB_DISPATCH(10),

  /** Job Modification (13) */
  JOB_MODIFICATION(13),

  /** Job Number Block Request (141) */
  JOB_NUMBER_BLOCK_REQUEST(141),

  /** Job Number Block Response (15) */
  JOB_NUMBER_BLOCK_RESPONSE(15),

  /** Job Posting Request (178) */
  JOB_POSTING_REQUEST(178),

  /** Job Posting Response (27) */
  JOB_POSTING_RESPONSE(27),

  /** Job Posting Response Failure (18) */
  JOB_POSTING_RESPONSE_FAILURE(18),

  /** Job Query (25) */
  JOB_QUERY(25),

  /** Job Reject (155) */
  JOB_REJECT(155),

  /** Log Of Result (4) */
  LOG_OFF_RESULT(4),

  /** Logon Request (130) */
  LOGON_REQUEST(130),

  /** Logon Result (003) */
  LOGON_RESULT(3),

  /** Logout Request (131) */
  LOGOUT_REQUEST(131),

  /** Manual Suspension (24) */
  MANUAL_SUSPENSION(24),

  /** MDT Sync Request */
  MDT_SYNC_REQUEST(176),

  /** Message Acknowledge (251) */
  MESSAGE_ACKNOWLEDGE(251),

  /** Message Acknowledge (125) */
  MESSAGE_AKNOWLEDGE(125),

  /** Meter Off Dispatch Job (161) */
  METER_OFF_DISPATCH_JOB(161),

  /** Meter Off Street Hail Job (140) */
  METER_OFF_STREET_HAIL_JOB(140),

  /** Meter On Dispatch Job (160) */
  METER_ON_DISPATCH_JOB(160),

  /** Meter On Street Hail Job (139) */
  METER_ON_STREET_HAIL_JOB(139),

  /** No Show Confirmation (22) */
  NO_SHOW_CONFIRMATION(22),

  /** No Show Request (159) */
  NO_SHOW_REQUEST(159),

  /** No Message Type Assigned (0) */
  NONE(0),

  /** Notify On Call (184) */
  NOTIFY_ONCALL(184),

  /** Notify Static GPS (157) */
  NOTIFY_STATIC_GPS(157),

  /** On Expressway Update (132) */
  ON_EXPRESSWAY_UPDATE(132),

  /** Pending Job Request (168) */
  PENDING_JOB_REQUEST(168),

  /** Power Up Request (129) */
  POWER_UP(129),

  /** Power Up Failure (19) */
  POWER_UP_FAILURE(19),

  /** Power Up Response (1) */
  POWER_UP_RESPONSE(1),

  /** Real Reject (174) */
  REAL_REJECT(174),

  /** Register Rank Request (162) */
  REGISTER_RANK_REQUEST(162),

  /** Regular Report (136) */
  REGULAR_REPORT(136),

  /** Reject Job Modification (179) */
  REJECT_JOB_MODIFICATION(179),

  /** Report Device Health Status (177) */
  REPORT_DEVICE_HEALTH_STATUS(177),

  /** Report Total Mileage (212) */
  REPORT_TOTAL_MILEAGE(212),

  /** Report Trip Information Normal (221) */
  REPORT_TRIP_INFORMATION_NORMAL(221),

  /** Request Hotstop Areas (144) */
  REQUEST_HOTSPOT_ARES(144),

  /** Reset IVD Acknowledge (209) */
  RESET_IVD_ACKNOWLEDGE(209),

  /** Respond Hotspot Ares (16) */
  RESPOND_HOTSPOT_AREAS(16),

  /** Respond to Simple Message (166) */
  RESPOND_TO_SIMPLE_MESSAGE(166),

  /** Respond To Structure Message (167) */
  RESPOND_TO_STRUCTURE_MESSAGE(167),

  /** Send Message (165) */
  SEND_MESSAGE(165),

  /** Simple Text Message (40) */
  SIMPLE_TEXT_MESSAGE(40),

  /** Start Vehicle Tracking (6) */
  START_VEHICLE_TRACKING(6),

  /** Start Voice Streaming (8) */
  START_VOICE_STREAMING(8),

  /** Stop Vehicle Tracking (7) */
  STOP_VEHICLE_TRACKING(7),

  /** Stop Voice Streaming (9) */
  STOP_VOICE_STREAMING(9),

  /** Store Forward Message (173) */
  STORE_FORWARD_MESSAGE(173),

  /** Structure Message (41) */
  STRUCTURE_MESSAGE(41),

  /** Suspension Acknowledge (170) */
  SUSPENSION_ACKNOWLEDGE(170),

  /** Trip Detail Update (171) */
  TRIP_DETAIL_UPDATE(171),

  /** Update Option */
  UPDATE_OPTION(218),

  /** Update STC (137) */
  UPDATE_STC(137),

  /** Update Stop (213) */
  UPDATE_STOP(213),

  /** Vehicle Tracking Response (138) */
  VEHICLE_TRACKING_RESPONSE(138),

  /** Voie Streaming Message (187) */
  VOICE_STREAMING_MESSAGE(187),

  /** Verify OTP (227) */
  VERIFY_OTP(227),

  /** Levy Update (118) */
  LEVY_UPDATE(118);

  private static final int MAX_RESULT_ID = 127;

  /**
   * Returns enum constant for a given message type id, {@link #NONE} is returned if not found.
   *
   * @param id id
   */
  public static IVDMessageType getType(int id) {
    for (IVDMessageType type : IVDMessageType.values()) {
      if (type.getId() == id) {
        return type;
      }
    }
    return IVDMessageType.NONE;
  }

  /** -- GETTER -- Returns message id. */
  private final int id;

  IVDMessageType(int id) {
    this.id = id;
  }

  /**
   * Returns true if the message type is for requests (IVD to Backend).
   * <!-- -->
   * Otherwise, false (Backend to IVD).
   */
  public boolean isFromIVD() {
    return this.id > IVDMessageType.MAX_RESULT_ID;
  }
}
