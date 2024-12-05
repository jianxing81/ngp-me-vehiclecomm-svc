package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import static java.time.ZoneOffset.UTC;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesSize;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.Channel;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class VehCommUtils {

  private static int nLatestSerialNum = -1;

  private static final ZoneId ASIA_SINGAPORE = ZoneId.of("Asia/Singapore");

  Random random = new SecureRandom();

  /**
   * Method to check if acknowledgment required 11 - Job Confirmation 106 - OTA Completed 109 -
   * Re-start IVD
   *
   * @param msgId m
   * @return i
   */
  public static boolean isAckNeeded(int msgId) {

    return msgId == 11 || msgId == 106 || msgId == 109;
  }

  /**
   * Method to get serial number
   *
   * @return serial number
   */
  public static int getSerialNum() {

    int nCurrSN = nLatestSerialNum;

    nLatestSerialNum = nCurrSN == 127 ? 0 : nCurrSN + 1;
    return nLatestSerialNum;
  }

  /**
   * Method for timestamp
   *
   * @param sourceArray input array
   * @param timestamp timestamp
   * @param timestampStartIndex timestamp start index
   */
  public static void fillTimestamp(byte[] sourceArray, long timestamp, int timestampStartIndex) {
    byte[] binaryTimestamp = getBinaryTimestamp(timestamp);

    IntStream.range(0, binaryTimestamp.length)
        .forEach(
            i ->
                Arrays.fill(
                    sourceArray,
                    timestampStartIndex + i,
                    timestampStartIndex + i + 1,
                    binaryTimestamp[i]));
  }

  /**
   * @param timestamp timestamp
   * @return timestamp as array of bytes
   */
  private static byte[] getBinaryTimestamp(long timestamp) {
    long epoch = timestamp / 1000;
    byte[] bytesTimestamp = BytesUtil.convertToBytes(epoch, BytesSize.UINT32);

    /* Converting to LITTLE_ENDIAN */
    BytesUtil.reverseBytes(bytesTimestamp);
    return bytesTimestamp;
  }

  /**
   * Method to obtain channel type
   *
   * @param channel name
   * @return channel id
   */
  public static int getChannelIntVal(String channel) {
    int val = 99;
    try {
      return Channel.getType(channel).getChannelId();
    } catch (Exception e) {
      log.error("Error on getChannelIntVal", e);
    }
    return val;
  }

  /**
   * @return serialNumber of MDT
   */
  public static int getMdtEventSerialNumber() {
    int min = 0;
    int max = 127;

    return random.nextInt((max - min) + 1) + min;
  }

  /**
   * Set header part for IVD Events
   *
   * @param type Message Type
   * @param ivdNo Ivd No
   * @return IVDMessage
   */
  public static IVDMessage createResponse(IVDMessageType type, int ivdNo) {
    IVDMessage response = new IVDMessage();
    response.getHeader().setType(type);

    response.getHeader().setSerialNum(getMdtEventSerialNumber());
    response.getHeader().setAcknowledgement(true);

    response.getHeader().setMobileId(ivdNo);
    response.getHeader().setTimestamp(new Date());

    return response;
  }

  public static Date localDateTimeToDate(LocalDateTime dateTime) {
    return Optional.ofNullable(dateTime)
        .map(
            str ->
                Date.from(
                    ZonedDateTime.of(dateTime, UTC)
                        .withZoneSameInstant(ASIA_SINGAPORE)
                        .toInstant()))
        .orElse(null);
  }
}
