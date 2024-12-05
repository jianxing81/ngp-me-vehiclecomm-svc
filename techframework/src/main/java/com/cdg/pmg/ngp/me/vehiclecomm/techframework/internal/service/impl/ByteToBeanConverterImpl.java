package com.cdg.pmg.ngp.me.vehiclecomm.techframework.internal.service.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.IvdDeviceConfig;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtIvdDeviceConfigApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtLogOffApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtLogOnApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtPowerUpApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal.ByteToBeanConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.Surcharge;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ByteDataRepresentation;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.DiscountedTotal;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.TariffInfo;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesSize;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.helper.AdditionalChargesHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.mapper.MessageDataMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.ByteDataExtractor;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.BytesUtil;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.DataConversion;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDFieldTag;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDListItem;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDMessageHeader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

/** This class converts bytes to Java Bean for all IVD events */
@Service
@RequiredArgsConstructor
@Slf4j
public class ByteToBeanConverterImpl implements ByteToBeanConverter {

  private final MessageDataMapper messageDataMapper;

  /**
   * This is a generic method to convert byte data to a pojo
   *
   * @param hexString hexString
   * @return ByteDataRepresentation jobEventCommand
   */

  // Name of this method to be changed later
  @Override
  public ByteDataRepresentation jobAcceptConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(IVDFieldTag.JOB_NUMBER, IVDFieldTag.SOURCE, IVDFieldTag.REJECT_REASON_CODE);

    List<IVDFieldTag> requiredIntegerTags = List.of(IVDFieldTag.ETA);

    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);

    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, requiredIntegerFields, null, null);
  }

  /**
   * This method will convert the byte data of REPORT_TRIP_INFORMATION_NORMAL(221) event to a Java
   * bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation jobEventCommand
   */
  @Override
  public ByteDataRepresentation reportTripInfoNormalConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(
            IVDFieldTag.JOB_NUMBER,
            IVDFieldTag.SOURCE,
            IVDFieldTag.HIRED_DISTANCE,
            IVDFieldTag.TRIP_NUMBER,
            IVDFieldTag.PLATFORM_FEE_ISAPPLICABLE,
            IVDFieldTag.PAYMENT_METHOD,
            IVDFieldTag.CARD_NUMBER,
            IVDFieldTag.DRIVER_ID,
            IVDFieldTag.TOTAL_DISTANCE,
            IVDFieldTag.TRIP_TYPE,
            IVDFieldTag.PRODUCT_ID,
            IVDFieldTag.GST_INCLUSIVE,
            IVDFieldTag.UPDATE_TYPE,
            IVDFieldTag.ENTRY_MODE,
            IVDFieldTag.PROMO_CODE,
            IVDFieldTag.PARTNER_DISCOUNT_TYPE,
            IVDFieldTag.PARTNER_ORDER_ID,
            IVDFieldTag.IS_FEE_WAIVED,
            IVDFieldTag.CAB_REWARDS_FLAG,
            IVDFieldTag.MAXIMUM_SPEED,
            IVDFieldTag.METER_EDIT_FLAG);

    List<IVDFieldTag> requiredIntegerTags =
        List.of(
            IVDFieldTag.PICKUP_X,
            IVDFieldTag.PICKUP_Y,
            IVDFieldTag.PICKUP_TIME,
            IVDFieldTag.DESTINATION_X,
            IVDFieldTag.DESTINATION_Y,
            IVDFieldTag.METER_FARE,
            IVDFieldTag.ERP_FEE,
            IVDFieldTag.ADMIN_FEE,
            IVDFieldTag.GST,
            IVDFieldTag.FIXED_PRICE,
            IVDFieldTag.AMOUNT_PAID,
            IVDFieldTag.TOTAL_PAID,
            IVDFieldTag.CAB_REWARDS_AMT,
            IVDFieldTag.LEVY,
            IVDFieldTag.BALANCE_DUE,
            IVDFieldTag.VOUCHER_PAYMENT_AMOUNT,
            IVDFieldTag.PROMO_AMOUNT,
            IVDFieldTag.ADMIN_FEE_DISCOUNT,
            IVDFieldTag.PLATFORM_FEE,
            IVDFieldTag.PARTNER_DISCOUNT_AMT,
            IVDFieldTag.PARTNER_DISCOUNT_VALUE,
            IVDFieldTag.HLA_FEE,
            IVDFieldTag.DISCOUNT);

    List<IVDFieldTag> requiredDateTags = List.of(IVDFieldTag.TRIP_END_TIME);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    Map<String, Date> requiredDateFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredDateTags, Date.class);

    IVDListItem[] tariffList = ivdMessage.getListItemArray(IVDFieldTag.TARIFF);
    List<TariffInfo> tariffInfo = getTariffFromIVDListItemArray(tariffList);

    Map<String, Integer> intermediatePointMap =
        getIntermediatePointsFromIVDListItem(ivdMessage.getListItem(IVDFieldTag.MULTI_STOP));
    if (!intermediatePointMap.isEmpty()) {
      requiredIntegerFields.putAll(intermediatePointMap);
    }

    IVDListItem[] surchargeList = ivdMessage.getListItemArray(IVDFieldTag.SURCHARGE);
    Map<String, Integer> surchargeMap = getSurchargesFromIVDListItemArray(surchargeList);
    if (!surchargeMap.isEmpty()) {
      requiredIntegerFields.putAll(surchargeMap);
    }
    // Map the fields to a pojo
    ByteDataRepresentation byteDataRepresentation =
        messageDataMapper.mapToByteDataRepresentation(
            header, requiredStringFields, requiredIntegerFields, requiredDateFields, null);

    if (byteDataRepresentation != null) {
      // Driver Fee requirement ,jira ticket:
      // https://comfortdelgrotaxi.atlassian.net/browse/NGPME-9210
      AdditionalChargesHelper.getAdditionalChargesFromIVDListItemArray(
              ivdMessage.getListItemArray(IVDFieldTag.ADDITIONAL_CHARGES))
          .ifPresent(byteDataRepresentation::setAdditionalCharges);
      byteDataRepresentation.setTariffInfo(tariffInfo);
    }

    return byteDataRepresentation;
  }

  /**
   * This method will convert the byte data of REJECT_JOB_MODIFICATION_EVENT_ID(179) event to a Java
   * bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation rejectJobModificationConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(IVDFieldTag.JOB_NUMBER, IVDFieldTag.SOURCE, IVDFieldTag.REJECT_REASON_CODE);

    List<IVDFieldTag> requiredIntegerTags = List.of(IVDFieldTag.ETA);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    // Map the fields to a pojo
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, requiredIntegerFields, null, null);
  }

  /**
   * This method will convert the byte data of JOB_REJECT_EVENT (155) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation jobRejectConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(IVDFieldTag.SOURCE, IVDFieldTag.JOB_NUMBER, IVDFieldTag.REJECT_REASON_CODE);

    List<IVDFieldTag> requiredIntegerTags = List.of(IVDFieldTag.ETA);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    // Map the fields to a pojo
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, requiredIntegerFields, null, null);
  }

  /**
   * This method will convert the byte data of ACKNOWLEDGE_JOB_CANCELLATION_EVENT(153) event to a
   * Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation acknowledgeJobCancellationConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags = List.of(IVDFieldTag.JOB_NUMBER, IVDFieldTag.SOURCE);

    List<IVDFieldTag> requiredIntegerTags = List.of(IVDFieldTag.ETA);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    // Map the fields to a pojo
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, requiredIntegerFields, null, null);
  }

  /**
   * This method will convert the byte data of JOB_CONFIRMATION_EVENT(14) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation jobConfirmationAcknowledgeConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags = List.of(IVDFieldTag.SOURCE, IVDFieldTag.JOB_NUMBER);

    List<IVDFieldTag> requiredIntegerTags = List.of(IVDFieldTag.ETA);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    // Map the fields to a pojo
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, requiredIntegerFields, null, null);
  }

  /**
   * This method will convert the byte data of JOB_MODIFICATION_EVENT(152) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation jobModificationConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(IVDFieldTag.JOB_NUMBER, IVDFieldTag.SOURCE, IVDFieldTag.JOB_MODIFICATION_RESPONSE);

    List<IVDFieldTag> requiredIntegerTags = List.of(IVDFieldTag.ETA);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    // Map the fields to a pojo
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, requiredIntegerFields, null, null);
  }

  /**
   * This method will convert the byte data of MESSAGE_ACKNOWLEDGE(251) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation messageAcknowledgeConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of ARRIVAL_EVENT(156) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation arrivalEventConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of JOB_NUMBER_BLOCK_REQUEST(141) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation jobNumberBlockRequestEventConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of NO_SHOW_REQUEST_EVENT_ID(159) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation noShowRequestConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of NOTIFY_ON_CALL_EVENT_ID(184) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation notifyOnCallEventConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of UPDATE_STOP_EVENT_ID(213) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation updateStopEventConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of METER_ON_DISPATCH_JOB(160) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation meterOnDispatchConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of AUTO_ACCEPT_JOB_CONF_ACK_EVENT(190) event to a Java
   * bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation autoAcceptJobConfirmationAckEvent(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredIntegerTags = List.of(IVDFieldTag.ETA);
    List<IVDFieldTag> requiredStringTags = List.of(IVDFieldTag.JOB_NUMBER, IVDFieldTag.SOURCE);

    // Extract required fields from byte array
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    // Map the fields to a pojo
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, requiredIntegerFields, null, null);
  }

  /**
   * This method will convert the byte data of CALL_OUT_EVENT(158)event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation callOutEvent(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of METER_OFF_DISPATCH_JOB_EVENT(161) event to a Java
   * bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation meterOffDispatchJobEvent(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of METER_OFF_STREETHAIL_JOB_EVENT(140) event to a Java
   * bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation meterOffStreetHailJobEvent(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of ACKNOWLEDGE_CONVERT_STREET_HAIL_EVENT(151) event to a
   * Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation acknowledgeConvertStreetHailEvent(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of UPDATE_STC(137) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation updateStcConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of BREAK(134) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation breakConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of UPDATE_BUSY(135) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation busyConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of Regular Report(136) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation regularConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of IVD_EVENT(219) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation ivdEventConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(
            IVDFieldTag.JOB_NUMBER,
            IVDFieldTag.DRIVER_ID,
            IVDFieldTag.EVENT_CONTENT,
            IVDFieldTag.MOBILE_NUMBER,
            IVDFieldTag.OTP,
            IVDFieldTag.OLD_PIN,
            IVDFieldTag.NEW_PIN,
            IVDFieldTag.ZONE_ID);

    List<IVDFieldTag> requiredIntegerTags =
        List.of(
            IVDFieldTag.ETA,
            IVDFieldTag.REFERENCE_NUMBER,
            IVDFieldTag.SEQUENCE_NUMBER,
            IVDFieldTag.TOTAL_MILEAGE,
            IVDFieldTag.EVENT_ID);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    // Map the fields to a pojo
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, requiredIntegerFields, null, null);
  }

  /**
   * This method will convert the byte data of VERIFY_OTP(227) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation verifyOtpConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of IVD_PING_RESPONSE(163) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation ivdPingResponseConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of REPORT_TOTAL_MILEAGE(212) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation reportTotalMileageConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of CHANGE_PIN_REQUEST(150) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation changePinRequestConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of DRIVER_PERFORMANCE_REQUEST(108) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation driverPerformanceRequestConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of CROSSING_ZONE(133) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation crossingZoneConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of CHANGE_SHIFT_UPDATE(172) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation changeShiftUpdateConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of DESTINATION_UPDATE(171) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation destinationUpdateConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of EXPRESSWAY_STATUS_UPDATE(132) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation expresswayStatusUpdateConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method will convert the byte data of NOTIFY_STATIC_GPS(157) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation notifyStaticGpsConverter(String hexString) {
    return commonByteToBeanConverter(hexString);
  }

  /**
   * This method is used for converting similar fields for events. It extracts the job number,
   * driver ID, and ETA from the byte array. It is designed to handle events that require only these
   * three fields, redirecting them to this method for processing.
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  private ByteDataRepresentation commonByteToBeanConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(
            IVDFieldTag.JOB_NUMBER,
            IVDFieldTag.DRIVER_ID,
            IVDFieldTag.EVENT_CONTENT,
            IVDFieldTag.MOBILE_NUMBER,
            IVDFieldTag.OTP,
            IVDFieldTag.OLD_PIN,
            IVDFieldTag.NEW_PIN,
            IVDFieldTag.ZONE_ID,
            IVDFieldTag.RECEIVED_MESSAGE_ID,
            IVDFieldTag.RECEIVED_MESSAGE_SN);

    List<IVDFieldTag> requiredIntegerTags =
        List.of(
            IVDFieldTag.ETA,
            IVDFieldTag.REFERENCE_NUMBER,
            IVDFieldTag.SEQUENCE_NUMBER,
            IVDFieldTag.TOTAL_MILEAGE,
            IVDFieldTag.NO_SHOW_EVENT_TYPE);

    List<IVDFieldTag> requiredBooleanTags = List.of(IVDFieldTag.VALID_LOCATION_FLAG);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    Map<String, Boolean> requiredBooleanFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredBooleanTags, Boolean.class);
    // Map the fields to a pojo
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, requiredIntegerFields, null, requiredBooleanFields);
  }

  @Override
  public String extractMdtIpAddress(String hexString) {

    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    /* retrieve the mdtIp */
    if (bytesMsg.length > 15) {
      return BytesUtil.toDecimal(
              ArrayUtils.subarray(bytesMsg, bytesMsg.length - 15, bytesMsg.length), BytesSize.TEXT)
          .toString()
          .trim();
    }
    return "";
  }

  /**
   * This method will convert the byte data of MDT_SYNC_REQUEST(176) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation mdtSyncRequestConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();
    return messageDataMapper.mapToByteDataRepresentation(header, null, null, null, null);
  }

  /**
   * This method will convert the byte data of METER_ON_STREETHAIL_JOB_EVENT(139) event to a Java
   * bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation meterOnStreetHailJobEvent(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    List<IVDFieldTag> requiredIntegerTags = List.of(IVDFieldTag.ETA);
    List<IVDFieldTag> requiredStringTags = List.of(IVDFieldTag.JOB_NUMBER);

    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    // Map the fields to a pojo
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, requiredIntegerFields, null, null);
  }

  /**
   * This method will convert the byte data of VOICE_STREAMING_MESSAGE(187) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation voiceStreamConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();
    byte[] voiceStream =
        extractVoiceStreamFromBytesMsg(
            bytesMsg,
            VehicleCommAppConstant.BYTES_START_LENGTH_FOR_VOICE_STREAM,
            VehicleCommAppConstant.BYTES_END_LENGTH_FOR_VOICE_STREAM);
    return messageDataMapper.mapTByteDataRepresentation(header, Arrays.toString(voiceStream));
  }

  /**
   * This method will convert the byte data of RESPONSE_TO_SIMPLE_MESSAGE(166) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation responseToSimpleMessageConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags = List.of(IVDFieldTag.CAN_MESSAGE_ID);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, null, null, null);
  }

  /**
   * Method to convert for send message
   *
   * @param hexString hexString
   * @return ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation sendMessageConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();
    List<IVDFieldTag> requiredStringTags = List.of(IVDFieldTag.MESSAGE_CONTENT);
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    // Map the fields to a pojo
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, null, null, null);
  }

  private byte[] extractVoiceStreamFromBytesMsg(byte[] bytesMsg, int startLength, int endLength) {
    byte[] voiceStream = null;
    if (bytesMsg.length > endLength) {
      voiceStream = ArrayUtils.subarray(bytesMsg, startLength, bytesMsg.length - endLength);
    } else {
      voiceStream = ArrayUtils.subarray(bytesMsg, startLength, bytesMsg.length);
    }
    return voiceStream;
  }

  @Override
  public ByteDataRepresentation fareCalculationRequestConverter(final String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(
            IVDFieldTag.CORPORATE_CARD_NUMBER,
            IVDFieldTag.JOB_NUMBER,
            IVDFieldTag.PRODUCT_ID,
            IVDFieldTag.PAYMENT_METHOD,
            IVDFieldTag.ACCOUNT_ID,
            IVDFieldTag.TRIP_NUMBER,
            IVDFieldTag.REQUEST_ID);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);

    IVDListItem[] tariffList = ivdMessage.getListItemArray(IVDFieldTag.TARIFF);
    List<TariffInfo> tariffInfo = getTariffFromIVDListItemArray(tariffList);

    // Map the fields to a pojo
    ByteDataRepresentation byteDataRepresentation =
        messageDataMapper.mapToByteDataRepresentation(
            header, requiredStringFields, null, null, null);
    byteDataRepresentation.setTariffInfo(tariffInfo);
    return byteDataRepresentation;
  }

  @Override
  public ByteDataRepresentation advanceJobRemindEventConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredIntegerTags = List.of(IVDFieldTag.ACKNOWLEDGEMENT);
    List<IVDFieldTag> requiredStringTags = List.of(IVDFieldTag.JOB_NUMBER);
    // Extract required fields from byte array
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, requiredIntegerFields, null, null);
  }

  private Map<String, Integer> getIntermediatePointsFromIVDListItem(IVDListItem ivdListItem) {
    Map<String, Integer> intermediatePointMap = new HashMap<>();
    if (Objects.isNull(ivdListItem)) {
      return intermediatePointMap;
    }
    int intermediateLat = ivdListItem.getCoord(IVDFieldTag.MULTI_STOP_X);
    int intermediateLng = ivdListItem.getCoord(IVDFieldTag.MULTI_STOP_Y);
    intermediatePointMap.put(IVDFieldTag.MULTI_STOP_X.getName(), intermediateLat);
    intermediatePointMap.put(IVDFieldTag.MULTI_STOP_Y.getName(), intermediateLng);
    return intermediatePointMap;
  }

  private Map<String, Integer> getSurchargesFromIVDListItemArray(IVDListItem[] ivdListItems) {
    return Arrays.stream(ivdListItems)
        .filter(
            item ->
                Objects.nonNull(item) && Objects.nonNull(item.getText(IVDFieldTag.SURCHARGE_ID)))
        .collect(
            Collectors.toMap(
                item -> {
                  String surchargeId = item.getText(IVDFieldTag.SURCHARGE_ID);
                  if (surchargeId.startsWith(Surcharge.CBD_AREA.getId())) {
                    return Surcharge.CBD_AREA.getName();
                  } else {
                    return Surcharge.findNameById(surchargeId);
                  }
                },
                item -> item.getMoney(IVDFieldTag.SURCHARGE_AMOUNT),
                (existingValue, newValue) -> newValue,
                HashMap::new));
  }

  private List<TariffInfo> getTariffFromIVDListItemArray(IVDListItem[] ivdListItems) {
    return Arrays.stream(ivdListItems)
        .map(
            item -> {
              var tariffTypeCode = item.getText(IVDFieldTag.TARIFF_CODE);
              var tariffUnitAmount =
                  DataConversion.toBackendMoneyType(item.getMoney(IVDFieldTag.TARIFF_AMOUNT));
              var tariffUnit = item.getInt32(IVDFieldTag.TARIFF_QUANTITY);

              var discountedTotal = new DiscountedTotal();
              discountedTotal.setFare(tariffUnitAmount);

              var tariffInfo = new TariffInfo();
              tariffInfo.setTariffTypeCode(tariffTypeCode);
              tariffInfo.setTariffUnit(tariffUnit);
              tariffInfo.setDiscountedTotal(discountedTotal);
              return tariffInfo;
            })
        .toList();
  }

  @Override
  public ByteDataRepresentation forgotPasswordRequestConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(IVDFieldTag.MOBILE_NUMBER, IVDFieldTag.VEHICLE_PLATE_NUMBER);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);

    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, null, null, null);
  }

  /**
   * This method will convert the byte data of Rcsa False alarm("185,186") events to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation
   */
  public ByteDataRepresentation emergencyRequestConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(IVDFieldTag.EMERGENCY_ID, IVDFieldTag.JOB_NUMBER);
    List<IVDFieldTag> requiredIntegerTags = List.of(IVDFieldTag.ETA);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    // Map the fields to a pojo
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, requiredIntegerFields, null, null);
  }

  /**
   * This method will convert the byte data of Rcsa Respond To Structrue("167") event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation respondToStructureConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags = List.of(IVDFieldTag.CAN_MESSAGE_ID);

    List<IVDFieldTag> requiredIntegerTags = List.of(IVDFieldTag.MESSAGE_SELECTION);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    // Map the fields to a pojo
    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, requiredIntegerFields, null, null);
  }

  /**
   * This method will convert the byte data of EMERGENCY_REPORT(164) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  @Override
  public ByteDataRepresentation emergencyReportConverter(String hexString) {
    byte[] bytesMsg = BytesUtil.toBytes(hexString);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();

    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(IVDFieldTag.JOB_NUMBER, IVDFieldTag.EMERGENCY_ID);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);

    return messageDataMapper.mapToByteDataRepresentation(
        header, requiredStringFields, null, null, null);
  }

  @Override
  public MdtLogOffApiRequest convertLogOffByteToBeanConverter(String byteMessage, byte[] ipAddArr) {

    byte[] bytesMsg = BytesUtil.toBytes(byteMessage);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();
    String mdtIpAddress = BytesUtil.toDecimal(ipAddArr, BytesSize.TEXT).toString().trim();
    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(IVDFieldTag.MOBILE_NUMBER, IVDFieldTag.DRIVER_ID, IVDFieldTag.LOG_OFF_STATUS);
    List<IVDFieldTag> requiredIntegerTags = List.of(IVDFieldTag.TOTAL_MILEAGE);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    return messageDataMapper.mapToMdtLogOffRequest(
        header, requiredStringFields, requiredIntegerFields, mdtIpAddress, null);
  }

  @Override
  public MdtLogOnApiRequest convertLogOnByteToBeanConverter(String byteMessage, byte[] ipAddArr) {
    byte[] bytesMsg = BytesUtil.toBytes(byteMessage);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();
    String mdtIpAddress = BytesUtil.toDecimal(ipAddArr, BytesSize.TEXT).toString().trim();
    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(
            IVDFieldTag.MOBILE_NUMBER,
            IVDFieldTag.DRIVER_PIN,
            IVDFieldTag.PENDING_ONCALL_JOBNO,
            IVDFieldTag.DRIVER_ID);
    List<IVDFieldTag> requiredIntegerTags = List.of(IVDFieldTag.TOTAL_MILEAGE);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    Map<String, Integer> requiredIntegerFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredIntegerTags, Integer.class);
    return messageDataMapper.mapToMdtLogOnRequest(
        header, requiredStringFields, requiredIntegerFields, mdtIpAddress, null);
  }

  @Override
  public MdtIvdDeviceConfigApiRequest convertIVDDeviceConfigByteToBeanConverter(
      String byteMessage, byte[] ipAddArr) {
    byte[] bytesMsg = BytesUtil.toBytes(byteMessage);
    IVDMessage ivdMessage = new IVDMessage(bytesMsg);
    IVDMessageHeader header = ivdMessage.getHeader();
    String mdtIpAddress = BytesUtil.toDecimal(ipAddArr, BytesSize.TEXT).toString().trim();
    IVDListItem[] ivdListItem = ivdMessage.getListItemArray(IVDFieldTag.DEVICE_CONFIG_LIST);
    MdtIvdDeviceConfigApiRequest ivdDeviceConfigAPIRequest = new MdtIvdDeviceConfigApiRequest();
    List<IvdDeviceConfig> deviceConfigsList = new ArrayList<>();
    for (IVDListItem ivdDeviceConfigItem : ivdListItem) {
      IvdDeviceConfig ivdConfig = new IvdDeviceConfig();
      ivdConfig.setDeviceAttributeValue(
          ivdDeviceConfigItem.getText(IVDFieldTag.DEVICE_ATTRIBUTE_VALUE));
      ivdConfig.setDeviceAttributeId(ivdDeviceConfigItem.getInt32(IVDFieldTag.DEVICE_ATTRIBUTE_ID));
      ivdConfig.setFacilityComponentID(
          ivdDeviceConfigItem.getInt32(IVDFieldTag.DEVICE_FACILITY_COMP_ID));
      ivdConfig.setDeviceComponentID(ivdDeviceConfigItem.getInt32(IVDFieldTag.DEVICE_COMPONENT_ID));
      ivdConfig.setDeviceInfoType(ivdDeviceConfigItem.getInt32(IVDFieldTag.DEVICE_INFO_TYPE));
      deviceConfigsList.add(ivdConfig);
    }
    ivdDeviceConfigAPIRequest.setMessageId(String.valueOf(header.getType().getId()));
    ivdDeviceConfigAPIRequest.setIvdDeviceConfig(deviceConfigsList);
    ivdDeviceConfigAPIRequest.setIvdNo(header.getMobileId());
    ivdDeviceConfigAPIRequest.setIpAddress(mdtIpAddress);
    ivdDeviceConfigAPIRequest.setSerialNo(String.valueOf(header.getSerialNum()));
    ivdDeviceConfigAPIRequest.setIsAckRequired(header.isAcknowledgement());
    ivdDeviceConfigAPIRequest.setTimeStamp(messageDataMapper.getInstantDate());
    return ivdDeviceConfigAPIRequest;
  }

  @Override
  public MdtPowerUpApiRequest convertPowerUpByteToBeanConverter(
      String byteMessage, byte[] ipAddArr) {
    byte[] bytesMsg = BytesUtil.toBytes(byteMessage);
    byte[] requestBytesWithoutIP = ArrayUtils.subarray(bytesMsg, 0, bytesMsg.length - 15);
    IVDMessage ivdMessage = new IVDMessage(requestBytesWithoutIP);
    IVDMessageHeader header = ivdMessage.getHeader();
    String mdtIp = BytesUtil.toDecimal(ipAddArr, BytesSize.TEXT).toString().trim();
    ivdMessage.putText(IVDFieldTag.IP_ADDRESS, mdtIp);
    // Prepare list of required tags
    List<IVDFieldTag> requiredStringTags =
        List.of(
            IVDFieldTag.MOBILE_NUMBER,
            IVDFieldTag.IP_ADDRESS,
            IVDFieldTag.IVD_FIRMWARE_VERSION,
            IVDFieldTag.IVD_INFO_MODEL,
            IVDFieldTag.FILE_LIST,
            IVDFieldTag.SELF_TEST_RESULT,
            IVDFieldTag.IMSI,
            IVDFieldTag.VEHICLE_PLATE_NUMBER,
            IVDFieldTag.TELCO_ID,
            IVDFieldTag.METER_VERSION,
            IVDFieldTag.TARIFF_CHECKSUM,
            IVDFieldTag.FIRMWARE_CHECKSUM,
            IVDFieldTag.MAPDATA_VERSION,
            IVDFieldTag.GIS_VERSION,
            IVDFieldTag.SCREEN_SAVER_VER,
            IVDFieldTag.JOB_NUMBER_BLOCK_START,
            IVDFieldTag.JOB_NUMBER_BLOCK_END);

    // Extract required fields from byte array
    Map<String, String> requiredStringFields =
        ByteDataExtractor.extractMultipleFieldsFromByteArray(
            ivdMessage, requiredStringTags, String.class);
    MdtPowerUpApiRequest mdtPowerUpAPIRequest =
        messageDataMapper.mapToMdtPowerUpRequest(header, requiredStringFields, mdtIp, null);
    List<MdtPowerUpApiRequest.FileDetails> fileDetailsList = new ArrayList<>();
    IVDListItem[] fileListItem = ivdMessage.getListItemArray(IVDFieldTag.FILE_LIST);
    for (IVDListItem fileList : fileListItem) {
      MdtPowerUpApiRequest.FileDetails file = new MdtPowerUpApiRequest.FileDetails();
      file.setFileName(fileList.getText(IVDFieldTag.FILE_FILENAME));
      file.setFileCheckSum(fileList.getText(IVDFieldTag.FILE_CHECKSUM));
      fileDetailsList.add(file);
    }

    mdtPowerUpAPIRequest.setFileList(fileDetailsList);
    return mdtPowerUpAPIRequest;
  }
}
