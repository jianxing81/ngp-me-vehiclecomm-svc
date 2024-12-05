package com.cdg.pmg.ngp.me.vehiclecomm.techframework.internal.service.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.BookingProductData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ChangePinResult;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.CmsConfiguration;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.FareTariffResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ForgotPasswordResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobNoBlockResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtApiResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtResponseCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.Product;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RooftopMessages;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VerifyOtpResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal.BeanToByteConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.web.ConfigurationService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.RcsaMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.EmergencyAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.MdtAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.MessageTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.PlatformFeeApplicabilityEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.BadRequestException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.IvdConversionException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.CabchargePolicy;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.EmergencyClose;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ExtraStopsInfo;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingMessageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.JobDispatchEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.MultiStop;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.PlatformFeeItem;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.PolicyDetails;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.TariffInfo;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleTrackCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VoiceEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.ByteData;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesSize;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.helper.AdditionalChargesHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.BytesUtil;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.DataConversion;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.EscUtilities;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDFieldTag;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDListItem;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDMessageContent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDMessageType;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JobDispatchUtil;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.VehCommUtils;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.VehicleCommFrameworkConstants;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/** To Handle JobDispatch Event */
@Service
@Slf4j
@RequiredArgsConstructor
public class BeanToByteConverterImpl implements BeanToByteConverter {
  public static final int HEADER_ARRAY_LEN = 9;
  public static final int ARRAY_FILL_INDEX_FROM = 8;
  public static final int ARRAY_FILL_INDEX_TO = 9;
  public static final int TIMESTAMP_START_INDEX = 4;

  private final VehicleCommCacheService vehicleCommCacheService;
  private final ConfigurationService configurationService;

  /**
   * Method to convert the request to byte array for job modification
   *
   * @param jobDispatchEventRequest request
   * @return JobDispatchEventCommand
   */
  public GenericEventCommand convertToJobModification(
      JobDispatchEventRequest jobDispatchEventRequest) {

    byte[] bMsgToIVD =
        generateHeader(
            Integer.parseInt(jobDispatchEventRequest.getIvdNo()),
            IVDMessageType.JOB_MODIFICATION.getId());
    GenericEventCommand genericEventCommand =
        convertJobModification(bMsgToIVD, jobDispatchEventRequest);
    String strIPAddress = jobDispatchEventRequest.getIpAddress();
    return generatedByte(genericEventCommand, strIPAddress);
  }

  public String sendMessageAcknowledgement(
      Integer ivdNo, String serialNo, String msgId, String ipAddress) {

    int nMsgId = IVDMessageType.MESSAGE_AKNOWLEDGE.getId();
    byte[] bMsgToIVD = generateHeader(ivdNo, nMsgId);

    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);
    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.RECEIVED_MESSAGE_ID, msgId);
    valuesMap.put(IVDFieldTag.RECEIVED_MESSAGE_SN, serialNo);

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);
    GenericEventCommand genericEventCommand =
        getGenericEventCommandForMessageAck(message, header, bMsgToIVD, ipAddress);
    return BytesUtil.toString(genericEventCommand.getByteArray());
  }

  public GenericEventCommand convertToStreetHail(JobDispatchEventRequest jobDispatchEventRequest) {
    if (jobDispatchEventRequest == null) {
      log.info("[convertToStreetHail] Error occurred while parsing bean to byte");
      throw new IvdConversionException(ErrorCode.PARSING_ERROR.getCode());
    }
    byte[] bMsgToIVD =
        generateHeader(
            Integer.parseInt(jobDispatchEventRequest.getIvdNo()),
            IVDMessageType.CONVERT_STREET_HAIL.getId());
    GenericEventCommand genericEventCommand = convertStreetHail(bMsgToIVD, jobDispatchEventRequest);
    String strIPAddress = jobDispatchEventRequest.getIpAddress();
    return generatedByte(genericEventCommand, strIPAddress);
  }

  /**
   * Method to convert the request to byte array for verify Otp event
   *
   * @param verifyOtpResponse values received from verify Otp API
   * @param esbVehicle esbVehicle
   * @return GenericEventCommand
   */
  public GenericEventCommand convertToByteVerifyOtp(
      VerifyOtpResponse verifyOtpResponse, EsbVehicle esbVehicle) {

    byte[] bMsgToIVD =
        generateHeader(esbVehicle.getByteData().getIvdNo(), esbVehicle.getResponseMessageId());
    GenericEventCommand genericEventCommand = convertVerifyOtp(bMsgToIVD, verifyOtpResponse);
    String strIPAddress = verifyOtpResponse.getIvdInfo().getIpAddress();

    return generatedByte(genericEventCommand, strIPAddress);
  }

  /**
   * Method to convert the request to byte array for rcsa message event
   *
   * @param rcsaMessageRequestCommand message request
   * @return GenericEventCommand
   */
  public GenericEventCommand convertToByteRcsaMessage(RcsaMessage rcsaMessageRequestCommand) {
    byte[] bMsgToIVD =
        generateHeader(
            rcsaMessageRequestCommand.getRcsaMessageEventRequest().getIvdNo(),
            rcsaMessageRequestCommand.getRcsaMessageEventRequest().getMsgId());
    GenericEventCommand genericEventCommand;
    switch (rcsaMessageRequestCommand.getMessageTypeEnum()) {
      case COMMAND_TYPE_1,
          COMMAND_TYPE_2,
          COMMAND_TYPE_3,
          COMMAND_TYPE_4,
          COMMAND_TYPE_5,
          COMMAND_TYPE_6 -> genericEventCommand =
          convertRcsaMessage(bMsgToIVD, rcsaMessageRequestCommand);
      case SIMPLE -> genericEventCommand =
          convertRcsaMessageSimple(bMsgToIVD, rcsaMessageRequestCommand);
      case STRUCTURE -> genericEventCommand =
          convertRcsaMessageStructure(bMsgToIVD, rcsaMessageRequestCommand);
      default -> throw new BadRequestException(ErrorCode.INVALID_COMMAND_TYPE.getCode());
    }
    String strIPAddress = rcsaMessageRequestCommand.getRcsaMessageEventRequest().getIpAddr();
    return generatedByte(genericEventCommand, strIPAddress);
  }

  /**
   * @param bMsgToIVD byte array
   * @param rcsaMessageRequestCommand request
   * @return GenericEventCommand
   */
  private GenericEventCommand convertRcsaMessageStructure(
      byte[] bMsgToIVD, RcsaMessage rcsaMessageRequestCommand) {
    IVDMessageContent message = new IVDMessageContent();
    byte[] header = Arrays.copyOf(bMsgToIVD, 9);
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), 4);

    Arrays.fill(header, 8, 9, (byte) 0);
    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(
        IVDFieldTag.REQUEST_SERVICE_TYPE,
        rcsaMessageRequestCommand.getRcsaMessageEventRequest().getRequestServiceType().getValue());
    valuesMap.put(
        IVDFieldTag.MESSAGE_SERIAL_NO,
        rcsaMessageRequestCommand.getRcsaMessageEventRequest().getMessageSerialNo());
    valuesMap.put(
        IVDFieldTag.CAN_MESSAGE_ID,
        rcsaMessageRequestCommand.getRcsaMessageEventRequest().getCanMessageId());
    valuesMap.put(
        IVDFieldTag.MESSAGE_CONTENT,
        rcsaMessageRequestCommand.getRcsaMessageEventRequest().getMsgContent());

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * @param bMsgToIVD byte array
   * @param rcsaMessageRequestCommand request
   * @return GenericEventCommand
   */
  private GenericEventCommand convertRcsaMessageSimple(
      byte[] bMsgToIVD, RcsaMessage rcsaMessageRequestCommand) {
    byte[] header = Arrays.copyOf(bMsgToIVD, 9);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), 4);

    Arrays.fill(header, 8, 9, (byte) 0);
    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(
        IVDFieldTag.MESSAGE_SERIAL_NO,
        rcsaMessageRequestCommand.getRcsaMessageEventRequest().getMessageSerialNo());
    valuesMap.put(
        IVDFieldTag.REQUEST_SERVICE_TYPE,
        rcsaMessageRequestCommand.getRcsaMessageEventRequest().getRequestServiceType().getValue());
    valuesMap.put(
        IVDFieldTag.MESSAGE_CONTENT,
        rcsaMessageRequestCommand.getRcsaMessageEventRequest().getMsgContent());
    valuesMap.put(
        IVDFieldTag.CAN_MESSAGE_ID,
        rcsaMessageRequestCommand.getRcsaMessageEventRequest().getCanMessageId());

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * @param bMsgToIVD byte array
   * @param rcsaMessageRequestCommand request
   * @return GenericEventCommand
   */
  private GenericEventCommand convertRcsaMessage(
      byte[] bMsgToIVD, RcsaMessage rcsaMessageRequestCommand) {

    byte[] header = Arrays.copyOf(bMsgToIVD, 9);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), 4);

    Arrays.fill(header, 8, 9, (byte) 0);
    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);

    String cmdType = rcsaMessageRequestCommand.getMessageTypeEnum().getValue();

    valuesMap.put(IVDFieldTag.COMMAND_TYPE, cmdType);

    if (Objects.requireNonNull(rcsaMessageRequestCommand.getMessageTypeEnum())
            .equals(MessageTypeEnum.COMMAND_TYPE_1)
        || rcsaMessageRequestCommand.getMessageTypeEnum().equals(MessageTypeEnum.COMMAND_TYPE_2)
        || rcsaMessageRequestCommand.getMessageTypeEnum().equals(MessageTypeEnum.COMMAND_TYPE_3)
        || rcsaMessageRequestCommand.getMessageTypeEnum().equals(MessageTypeEnum.COMMAND_TYPE_4)
        || rcsaMessageRequestCommand.getMessageTypeEnum().equals(MessageTypeEnum.COMMAND_TYPE_5)
        || rcsaMessageRequestCommand.getMessageTypeEnum().equals(MessageTypeEnum.COMMAND_TYPE_6)) {
      valuesMap.put(
          IVDFieldTag.COMMAND_VARIABLE,
          rcsaMessageRequestCommand.getRcsaMessageEventRequest().getCommandVariable());
    }

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * @param bMsgToIVD byte array
   * @param vehicleEventRequest request
   * @return GenericEventCommand
   */
  private GenericEventCommand convertSyncronizedAutoBidMessage(
      byte[] bMsgToIVD, VehicleEventRequest vehicleEventRequest) {

    byte[] header = Arrays.copyOf(bMsgToIVD, 9);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), 4);
    Arrays.fill(header, 8, 9, (byte) 0);
    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.COMMAND_TYPE, VehicleCommAppConstant.AUTO_BID_COMMAND_TYPE);
    valuesMap.put(IVDFieldTag.COMMAND_VARIABLE, vehicleEventRequest.getEvent().getValue());
    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);
    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * Method to convert the request to byte array for send ping message
   *
   * @param messageId messageId
   * @param ivdPingMessageRequest values received from send ping message API
   * @return GenericEventCommand
   */
  @Override
  public GenericEventCommand convertToBytePingMessage(
      Integer messageId, IvdPingMessageRequest ivdPingMessageRequest) {
    if (ivdPingMessageRequest == null) {
      log.info("[convertToBytePingMessage] Error occurred while parsing bean to byte");
      throw new IvdConversionException(ErrorCode.PARSING_ERROR.getCode());
    }
    byte[] bMsgToIVD = generateHeader(ivdPingMessageRequest.getIvdNo(), messageId);
    GenericEventCommand genericEventCommand = convertPingMessage(bMsgToIVD, ivdPingMessageRequest);
    String strIPAddress = ivdPingMessageRequest.getIpAddress();

    return generatedByte(genericEventCommand, strIPAddress);
  }

  /**
   * Method to convert the request to byte array for send ping message
   *
   * @param changePinResult ChangePinResult values received from change pin API
   * @param msgId msgId
   * @param esbVehicle esbVehicle
   * @return GenericEventCommand
   */
  @Override
  public GenericEventCommand convertToByteChangePin(
      ChangePinResult changePinResult, int msgId, EsbVehicle esbVehicle) {
    byte[] bMsgToIVD = generateHeader(esbVehicle.getByteData().getIvdNo(), msgId);
    GenericEventCommand genericEventCommand =
        convertChangePin(
            bMsgToIVD,
            esbVehicle.getByteData().getDriverId(),
            esbVehicle.getByteData().getNewPin(),
            changePinResult.isChangePinResult());
    String strIPAddress = changePinResult.getIpAddress();
    return generatedByte(genericEventCommand, strIPAddress);
  }

  /**
   * Method to convert the request to byte array for job no block
   *
   * @param esbJob esbJob
   * @param jobNoBlockResponse jobNoBlockResponse
   * @return GenericEventCommand
   */
  public GenericEventCommand convertToJobNumberBlock(
      EsbJob esbJob, JobNoBlockResponse jobNoBlockResponse) {
    if (jobNoBlockResponse == null || esbJob.getVehicleDetails().getIpAddress() == null) {
      log.info("[convertToJobNumberBlock] Error occurred while parsing bean to byte");
      throw new IvdConversionException(ErrorCode.PARSING_ERROR.getCode());
    }

    byte[] bMsgToIVD =
        generateHeader(esbJob.getByteData().getIvdNo(), esbJob.getResponseMessageId());
    GenericEventCommand genericEventCommand = convertJobNoBlock(bMsgToIVD, jobNoBlockResponse);
    String strIPAddress = esbJob.getVehicleDetails().getIpAddress();

    return generatedByte(genericEventCommand, strIPAddress);
  }

  @Override
  public GenericEventCommand convertToJobCancelInIvdResponse(
      JobDispatchEventRequest jobDispatchEventRequest) {

    log.debug("Start of convertToJobCancelInIvdResponse: {} ", jobDispatchEventRequest);
    byte[] bMsgToIVD =
        generateHeader(
            Integer.parseInt(jobDispatchEventRequest.getIvdNo()),
            IVDMessageType.JOB_CANCELLATION.getId());
    GenericEventCommand genericEventCommand =
        convertJobCancelForIvdResponse(bMsgToIVD, jobDispatchEventRequest);
    String strIPAddress = jobDispatchEventRequest.getIpAddress();
    return generatedByte(genericEventCommand, strIPAddress);
  }

  @Override
  public GenericEventCommand convertToJobConfirmation(
      JobDispatchEventRequest jobDispatchEventRequest) {
    byte[] bMsgToIVD =
        generateHeader(
            Integer.parseInt(jobDispatchEventRequest.getIvdNo()),
            IVDMessageType.JOB_CONFIRMATION.getId());
    String strIPAddress = jobDispatchEventRequest.getIpAddress();
    GenericEventCommand genericEventCommand;
    if ((jobDispatchEventRequest.getAutoAcceptFlag()
            == VehicleCommAppConstant.JOB_CONFIRM_AUTO_ACCEPT_FLAG
        && !(jobDispatchEventRequest.getAutoAcceptSuspend()))) {
      bMsgToIVD[1] =
          (byte) ((EscUtilities.toBoolOneOrZero(true) << 7) + VehCommUtils.getSerialNum());
      genericEventCommand = convertJobOffer(bMsgToIVD, jobDispatchEventRequest);
    } else {
      genericEventCommand = convertJobConfirmation(bMsgToIVD, jobDispatchEventRequest);
    }

    return generatedByte(genericEventCommand, strIPAddress);
  }

  private GenericEventCommand convertJobOffer(
      byte[] bMsgToIVD, JobDispatchEventRequest jobDispatchEventRequest) {
    byte[] header = Arrays.copyOf(bMsgToIVD, 9);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), 4);
    Arrays.fill(header, 8, 9, (byte) 0);
    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.SOURCE, jobDispatchEventRequest.getMessageSource());
    valuesMap.put(IVDFieldTag.JOB_NUMBER, jobDispatchEventRequest.getJobNo());
    String jobTypeInNumber =
        VehicleCommFrameworkConstants.ADVANCE_JOB_TYPE.equalsIgnoreCase(
                jobDispatchEventRequest.getJobType())
            ? "1"
            : "0";
    valuesMap.put(IVDFieldTag.JOB_TYPE, jobTypeInNumber);
    valuesMap.put(
        IVDFieldTag.PAYMENT_METHOD,
        vehicleCommCacheService
            .getPaymentMethodFromCacheAsName(jobDispatchEventRequest.getPaymentMethod())
            .getPaymentMode());
    valuesMap.put(
        IVDFieldTag.PICKUP_Y, DataConversion.toCoordData(jobDispatchEventRequest.getPickupLat()));
    valuesMap.put(IVDFieldTag.DISPATCH_METHOD, jobDispatchEventRequest.getDispatchMethod());
    valuesMap.put(
        IVDFieldTag.CUSTOMER_PRIORITY,
        Boolean.TRUE.equals(jobDispatchEventRequest.getPriorityCustomer()) ? 1 : 0);
    valuesMap.put(
        IVDFieldTag.PICKUP_TIME,
        VehCommUtils.localDateTimeToDate(jobDispatchEventRequest.getPickupTime()));
    valuesMap.put(
        IVDFieldTag.PICKUP_X, DataConversion.toCoordData(jobDispatchEventRequest.getPickupLng()));

    valuesMap.put(IVDFieldTag.PICKUP_ADDRESS, jobDispatchEventRequest.getPickupAddr());
    valuesMap.put(IVDFieldTag.ARRIVAL_REQUIRED, jobDispatchEventRequest.getArrivalRequired());
    Optional.ofNullable(jobDispatchEventRequest.getCompanyName())
        .ifPresent(companyName -> valuesMap.put(IVDFieldTag.ACCOUNT_COMPANY_NAME, companyName));
    Optional.ofNullable(jobDispatchEventRequest.getBookingChannel())
        .filter(StringUtils::isNotEmpty)
        .map(
            channel ->
                Map.entry(IVDFieldTag.BOOKING_CHANNEL, VehCommUtils.getChannelIntVal(channel)))
        .ifPresent(entry -> valuesMap.put(entry.getKey(), entry.getValue()));
    double bookingFee = 0.0;
    Optional.ofNullable(jobDispatchEventRequest.getBookingFee())
        .ifPresent(
            (fee -> valuesMap.put(IVDFieldTag.BOOKING_FEE, DataConversion.toIVDMoneyType(fee))));
    Optional.ofNullable(jobDispatchEventRequest.getFareType())
        .filter(fareType -> fareType != 0)
        .ifPresent(fareType -> valuesMap.put(IVDFieldTag.FARE_TYPE, fareType));
    Optional.ofNullable(jobDispatchEventRequest.getDeposit())
        .ifPresent(
            deposit -> valuesMap.put(IVDFieldTag.DEPOSIT, DataConversion.toIVDMoneyType(deposit)));
    Optional.ofNullable(jobDispatchEventRequest.getLevy())
        .map(levy -> Map.entry(IVDFieldTag.LEVY, DataConversion.toIVDMoneyType(levy)))
        .ifPresent(entry -> valuesMap.put(entry.getKey(), entry.getValue()));
    Optional.ofNullable(jobDispatchEventRequest.getJobMeritPoint())
        .ifPresent(meritPoint -> valuesMap.put(IVDFieldTag.JOB_MERIT_POINTS, meritPoint));
    Optional.ofNullable(jobDispatchEventRequest.getPassengerName())
        .ifPresent(passengerName -> valuesMap.put(IVDFieldTag.PASSENGER_NAME, passengerName));
    Optional.ofNullable(jobDispatchEventRequest.getNoShowTiming())
        .filter(noShowTiming -> noShowTiming >= 0)
        .ifPresent(noShowTiming -> valuesMap.put(IVDFieldTag.NO_SHOW_TIMEOUT, noShowTiming));
    Optional.ofNullable(jobDispatchEventRequest.getAccountId())
        .ifPresent(accountId -> valuesMap.put(IVDFieldTag.ACCOUNT_ID, accountId));
    Optional.ofNullable(jobDispatchEventRequest.getPassengerContactNumber())
        .ifPresent(
            contactNumber -> valuesMap.put(IVDFieldTag.PASSENGER_CONTACT_NUMBER, contactNumber));
    setDestinationDetails(jobDispatchEventRequest, valuesMap);
    Optional.ofNullable(jobDispatchEventRequest.getProductId())
        .filter(StringUtils::isNotEmpty)
        .map(vehicleCommCacheService::getBookingProductDetailsFromCache)
        .map(BookingProductData::getInVehicleDeviceCode)
        .map(String::valueOf)
        .ifPresentOrElse(
            productIdVehicleCode -> valuesMap.put(IVDFieldTag.PRODUCT_ID, productIdVehicleCode),
            () -> valuesMap.put(IVDFieldTag.PRODUCT_ID, "1"));
    Optional.ofNullable(jobDispatchEventRequest.getNotes())
        .ifPresent(notes -> valuesMap.put(IVDFieldTag.JOB_NOTES, notes));
    Optional.ofNullable(jobDispatchEventRequest.getWaitingPoint())
        .ifPresent(waitingPoint -> valuesMap.put(IVDFieldTag.WAITING_POINT, waitingPoint));
    Optional.ofNullable(jobDispatchEventRequest.getRemark())
        .ifPresent(remark -> valuesMap.put(IVDFieldTag.ADDRESS_INFO, remark));
    Optional.ofNullable(jobDispatchEventRequest.getTariffInfo())
        .ifPresent(tariffInfo -> setTariffs(tariffInfo, message));
    Optional.ofNullable(jobDispatchEventRequest.getExtraStopsInfo())
        .ifPresent(extraStopsInfo -> setExtraStops(extraStopsInfo, message));
    valuesMap.put(IVDFieldTag.SHOW_OFFER_SCREEN, jobDispatchEventRequest.getSosFlag());
    CmsConfiguration cmsConfiguration = configurationService.getCmsConfiguration();
    Optional.ofNullable(jobDispatchEventRequest.getPromoCode())
        .filter(StringUtils::isNotEmpty)
        .ifPresent(promoCode -> valuesMap.put(IVDFieldTag.PROMO_CODE, promoCode));
    int jobDispInclMpInfoFlag =
        cmsConfiguration.getJobDispInclMpInfoFlag() == null
            ? VehicleCommFrameworkConstants.JOB_DISP_INCL_MP_INFO_FLAG
            : cmsConfiguration.getJobDispInclMpInfoFlag();
    if (jobDispInclMpInfoFlag == 1) {
      Optional.ofNullable(jobDispatchEventRequest.getCcNumber())
          .filter(StringUtils::isNotEmpty)
          .ifPresent(ccNumber -> valuesMap.put(IVDFieldTag.CC_NUMBER, ccNumber));
      Optional.ofNullable(jobDispatchEventRequest.getCcExpiry())
          .filter(StringUtils::isNotEmpty)
          .ifPresent(ccExpiry -> valuesMap.put(IVDFieldTag.CC_EXPIRY_DATE, ccExpiry));
    }
    String waiveBookingFee = jobDispatchEventRequest.getPromoWaiveBookingFee();
    Double promoAmtVal = jobDispatchEventRequest.getPromoAmount();
    if (VehicleCommFrameworkConstants.Y.equalsIgnoreCase(waiveBookingFee)) {
      promoAmtVal = bookingFee;
    }
    valuesMap.put(IVDFieldTag.DISP_CALL_OUT_BTN, jobDispatchEventRequest.getEnableCalloutButton());
    Optional.ofNullable(promoAmtVal)
        .ifPresent(
            value -> valuesMap.put(IVDFieldTag.PROMO_AMOUNT, DataConversion.toIVDMoneyType(value)));
    valuesMap.put(
        IVDFieldTag.ENFORCE_MODIFICATION,
        BooleanUtils.negate(jobDispatchEventRequest.getCanReject()));
    valuesMap.put(IVDFieldTag.PAYMENT_INFO, jobDispatchEventRequest.getPaymentPlus());
    valuesMap.put(IVDFieldTag.AUTO_ASSIGN_FLAG, jobDispatchEventRequest.getAutoAssignFlag());
    valuesMap.put(IVDFieldTag.COLLECT_FARE, jobDispatchEventRequest.getCollectFare());
    Optional.ofNullable(jobDispatchEventRequest.getAutoAcceptFlag())
        .ifPresent(autoAcceptFlag -> valuesMap.put(IVDFieldTag.AUTO_BID_FLAG, autoAcceptFlag));

    if (jobDispatchEventRequest.getMultiStop() != null) {
      setMultiStops(jobDispatchEventRequest.getMultiStop(), message);
    }
    setNewGstDetails(jobDispatchEventRequest, valuesMap);
    if (jobDispatchEventRequest.getCurrentGst() != null) {
      valuesMap.put(IVDFieldTag.CURRENT_GST, jobDispatchEventRequest.getCurrentGst());
      valuesMap.put(IVDFieldTag.CURRENT_GST_INCLUSIVE, jobDispatchEventRequest.getCurrentGstIncl());
    }
    valuesMap.put(IVDFieldTag.CAB_REWARDS_FLAG, jobDispatchEventRequest.getLoyaltyEnable());
    Optional.ofNullable(jobDispatchEventRequest.getLoyaltyAmount())
        .ifPresent(
            loyaltyAmount ->
                valuesMap.put(
                    IVDFieldTag.CAB_REWARDS_AMT, DataConversion.toIVDMoneyType(loyaltyAmount)));
    valuesMap.put(
        IVDFieldTag.CBD_SURCHARGE_FLG, Boolean.TRUE.equals(jobDispatchEventRequest.getCbdFlag()));
    Optional.ofNullable(jobDispatchEventRequest.getCbdSurchargeAmount())
        .ifPresent(
            cbdSurchargeAmt ->
                valuesMap.put(
                    IVDFieldTag.CBD_SURCHARGE_AMT, DataConversion.toIVDMoneyType(cbdSurchargeAmt)));
    valuesMap.put(IVDFieldTag.DYNA_PRC_INDICATOR, jobDispatchEventRequest.getJobSurgeSigns());

    if (StringUtils.isNotEmpty(jobDispatchEventRequest.getPrivateField())) {
      valuesMap.put(IVDFieldTag.PRIVATE_FIELD, jobDispatchEventRequest.getPrivateField());
    }

    Optional.ofNullable(jobDispatchEventRequest.getEta())
        .ifPresent(eta -> valuesMap.put(IVDFieldTag.ETA, eta));

    if (jobDispatchEventRequest.getCurrentAdminVal() != null
        && jobDispatchEventRequest.getCurrentAdminDiscountVal() != null) {
      sendCurrAdminValues(jobDispatchEventRequest, valuesMap);
    }
    if (jobDispatchEventRequest.getNewAdminValue() != null
        && jobDispatchEventRequest.getNewAdminDiscountVal() != null
        && jobDispatchEventRequest.getNewAdminEffectiveDate() != null) {
      sendNewAdminValues(jobDispatchEventRequest, valuesMap);
    }
    Optional.ofNullable(jobDispatchEventRequest.getCabchargePolicy())
        .ifPresent(cabchargePolicy -> setCabChargePolicy(cabchargePolicy, valuesMap));
    Optional.ofNullable(jobDispatchEventRequest.getPlatformFeeItem())
        .ifPresent(platformFeeItems -> getPlatformFeeListItem(platformFeeItems, message));

    /* ME-3536 [NettFareCalculation] Add tripDistance and pickupDistance (both meter) */
    Optional.ofNullable(jobDispatchEventRequest.getTripDistance())
        .filter(tripDistance -> tripDistance > 0)
        .ifPresent(
            tripDistance -> valuesMap.put(IVDFieldTag.TRIP_DISTANCE, tripDistance.longValue()));
    Optional.ofNullable(jobDispatchEventRequest.getPickupDistance())
        .filter(pickupDistance -> pickupDistance > 0)
        .ifPresent(
            pickupDistance ->
                valuesMap.put(IVDFieldTag.PICKUP_DISTANCE, pickupDistance.longValue()));
    Optional.ofNullable(jobDispatchEventRequest.getNettFare())
        .filter(netFare -> netFare > 0)
        .ifPresent(
            nettFare ->
                valuesMap.put(IVDFieldTag.NETT_FARE, DataConversion.toIVDMoneyType(nettFare)));

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    // Driver Fee requirement ,jira ticket:
    // https://comfortdelgrotaxi.atlassian.net/browse/NGPME-9210
    AdditionalChargesHelper.convertToIvdAdditionalCharges(
            jobDispatchEventRequest.getAdditionalCharges())
        .ifPresent(
            additionalChargeIvdListItems ->
                setAdditionalCharges(additionalChargeIvdListItems, message));

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  private static void setAdditionalCharges(
      List<IVDListItem> additionalChargeIvdListItems, IVDMessageContent message) {
    /*
    Due to the caller method "convertJobOffer" have to much void return method invoke,
    such as "getPlatformFeeListItem" ,
    recommend to refine the "convertJobOffer" method in the future.
     */
    try {
      additionalChargeIvdListItems.forEach(
          additionalCharge ->
              message.putListItem(IVDFieldTag.ADDITIONAL_CHARGES, additionalCharge, false));
    } catch (Exception e) {
      log.error("Set additional charges to IVDMessageContent failed.", e);
    }
  }

  /**
   * Method to set byte size and array to GenericEventCommand
   *
   * @param bMsgToIVD byte array
   * @param jobNoBlockResponse jobNoBlockResponse
   * @return GenericEventCommand
   */
  private GenericEventCommand convertJobNoBlock(
      byte[] bMsgToIVD, JobNoBlockResponse jobNoBlockResponse) {

    byte[] header = Arrays.copyOf(bMsgToIVD, 9);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), 4);

    header[8] = (byte) 0;

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.JOB_NUMBER_BLOCK_START, jobNoBlockResponse.getJobNoBlockStart());
    valuesMap.put(IVDFieldTag.JOB_NUMBER_BLOCK_END, jobNoBlockResponse.getJobNoBlockEnd());

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * Method to set byte size and array to GenericEventCommand
   *
   * @param bMsgToIVD byte array
   * @param ivdPingMessageRequest ivdPingMessageRequest
   * @return GenericEventCommand
   */
  private GenericEventCommand convertPingMessage(
      byte[] bMsgToIVD, IvdPingMessageRequest ivdPingMessageRequest) {
    if (bMsgToIVD == null || bMsgToIVD.length < 9) {
      log.info("[convertPingMessage] Error occurred while parsing bean to byte");
      throw new IvdConversionException(ErrorCode.PARSING_ERROR.getCode());
    }
    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);

    header[8] = (byte) 0;

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.REFERENCE_NUMBER, ivdPingMessageRequest.getRefNo());
    valuesMap.put(IVDFieldTag.SEQUENCE_NUMBER, ivdPingMessageRequest.getSeqNo());

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * Method to convert emergency close request to byte array
   *
   * @param event CLOSE
   * @param emergencyClose request object
   * @return GenericEventCommand
   */
  public GenericEventCommand convertToByteEmergencyCloseRequest(
      EmergencyAction event, EmergencyClose emergencyClose) {
    log.debug("convertToByteEmergencyCloseRequest{}", emergencyClose);
    byte[] bMsgToIVD = generateHeader(emergencyClose.getIvdNo(), emergencyClose.getId());
    GenericEventCommand genericEventCommand =
        convertEmergencyCloseRequest(bMsgToIVD, emergencyClose);
    String strIPAddress = emergencyClose.getIpAddress();
    return generatedByte(genericEventCommand, strIPAddress);
  }

  @Override
  public GenericEventCommand convertToByteFareCalculation(
      final ByteData byteData,
      final String ipAddress,
      final FareTariffResponse fareTariffResponse) {
    byte[] bMsgToIVD =
        generateHeader(byteData.getIvdNo(), IVDMessageType.FARE_CALCULATION_RESPONSE.getId());

    GenericEventCommand genericEventCommand =
        convertFareCalculation(
            bMsgToIVD,
            byteData.getProductId(),
            byteData.getRequestId(),
            byteData.getTripId(),
            fareTariffResponse);
    return generatedByte(genericEventCommand, ipAddress);
  }

  /**
   * Method to set byte size and array to GenericEventCommand
   *
   * @param bMsgToIVD byte array
   * @param productId productId
   * @param requestId requestId
   * @param tripId tripId
   * @param fareTariffResponse fare tariff values
   * @return GenericEventCommand
   */
  private GenericEventCommand convertFareCalculation(
      byte[] bMsgToIVD,
      String productId,
      String requestId,
      String tripId,
      FareTariffResponse fareTariffResponse) {

    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);

    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);

    FareTariffResponse.DataDTO data = fareTariffResponse.getData();

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.DEPOSIT, 0);
    valuesMap.put(IVDFieldTag.REFUND, 0);
    valuesMap.put(IVDFieldTag.ADMIN_FEE, 0);
    Optional.ofNullable(data.getLevy())
        .ifPresent(levy -> valuesMap.put(IVDFieldTag.LEVY, DataConversion.toIVDMoneyType(levy)));
    Optional.ofNullable(data.getGstAmount())
        .ifPresent(
            gstAmount ->
                valuesMap.put(IVDFieldTag.GST, DataConversion.toIVDMoneyType(data.getGstAmount())));
    valuesMap.put(IVDFieldTag.GST_INCLUSIVE, data.getGstInclusive());
    valuesMap.put(IVDFieldTag.REQUEST_ID, requestId);
    valuesMap.put(IVDFieldTag.TRIP_NUMBER, tripId);
    Optional.ofNullable(data.getBookingFee())
        .ifPresent(
            bookingFee ->
                valuesMap.put(IVDFieldTag.TOTAL_AMOUNT, DataConversion.toIVDMoneyType(bookingFee)));
    valuesMap.put(IVDFieldTag.PRODUCT_ID, productId);
    Optional.ofNullable(data.getTariffList())
        .filter(tariffList -> !tariffList.isEmpty())
        .ifPresent(
            tariffList ->
                tariffList.forEach(
                    tariff -> {
                      IVDListItem tariffItem = new IVDListItem();
                      tariffItem.putText(IVDFieldTag.TARIFF_CODE, tariff.getTariffTypeCode());
                      tariffItem.putMoney(
                          IVDFieldTag.TARIFF_AMOUNT,
                          DataConversion.toIVDMoneyType(tariff.getFare().doubleValue()));
                      tariffItem.putByte(
                          IVDFieldTag.TARIFF_QUANTITY, (byte) tariff.getTariffUnit().intValue());
                      message.putListItem(IVDFieldTag.TARIFF, tariffItem, false);
                    }));
    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  @Override
  public GenericEventCommand convertToByteForgotPassword(
      ForgotPasswordResponse forgotPasswordResponse, Integer ivdNo) {

    byte[] bMsgToIVD = generateHeader(ivdNo, IVDMessageType.FORGOT_PASSWORD_RESPONSE.getId());
    GenericEventCommand genericEventCommand =
        convertForgotPassword(bMsgToIVD, forgotPasswordResponse);
    String strIPAddress = forgotPasswordResponse.getIvdInfo().getIpAddress();

    return generatedByte(genericEventCommand, strIPAddress);
  }

  private GenericEventCommand convertForgotPassword(
      byte[] bMsgToIVD, ForgotPasswordResponse forgotPasswordResponse) {
    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);

    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.FORGOT_PWD_RESULT, forgotPasswordResponse.getPassApproval());

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  @Override
  public GenericEventCommand convertToJobOffer(JobDispatchEventRequest jobDispatchEventRequest) {
    int ivdNo = Integer.parseInt(jobDispatchEventRequest.getIvdNo());
    // If ackFlag available set it, else set it as false
    boolean ackFlag =
        Objects.nonNull(jobDispatchEventRequest.getAckFlag())
            && jobDispatchEventRequest.getAckFlag();
    byte[] bMsgToIVD = new byte[VehicleCommFrameworkConstants.BUF_SIZE];
    bMsgToIVD[0] = (byte) IVDMessageType.JOB_DISPATCH.getId();
    bMsgToIVD[1] =
        (byte) (EscUtilities.toBoolOneOrZero(ackFlag) << 7 + VehCommUtils.getSerialNum());
    bMsgToIVD[2] = (byte) ivdNo;
    bMsgToIVD[3] = (byte) ((long) ivdNo >> 8);
    GenericEventCommand genericEventCommand = convertJobOffer(bMsgToIVD, jobDispatchEventRequest);
    String strIPAddress = jobDispatchEventRequest.getIpAddress();
    return generatedByte(genericEventCommand, strIPAddress);
  }

  /**
   * Method to convert to byte for call-out event
   *
   * @param jobDispatchEventRequest job dispatch event request data
   * @return GenericEventCommand
   */
  public GenericEventCommand convertToByteCallOut(JobDispatchEventRequest jobDispatchEventRequest) {
    byte[] bMsgToIVD =
        generateHeader(
            Integer.parseInt(jobDispatchEventRequest.getIvdNo()),
            IVDMessageType.CALL_OUT_RESULT.getId());

    GenericEventCommand genericEventCommand = convertCallOut(bMsgToIVD, jobDispatchEventRequest);
    String strIPAddress = jobDispatchEventRequest.getIpAddress();
    return generatedByte(genericEventCommand, strIPAddress);
  }

  /**
   * convert to byte for the event Syncronized autobid status
   *
   * @param vehicleEventRequest vehicle event request data
   * @return GenericEventCommand
   */
  public GenericEventCommand convertToByteSyncronizedAutoBid(
      VehicleEventRequest vehicleEventRequest, VehicleDetailsResponse vehicleDetails) {
    byte[] bMsgToIVD =
        generateHeader(vehicleEventRequest.getIvdNo(), IVDMessageType.COMMAND_MESSAGE.getId());

    GenericEventCommand genericEventCommand =
        convertSyncronizedAutoBidMessage(bMsgToIVD, vehicleEventRequest);
    String strIPAddress = vehicleDetails.getIpAddress();
    return generatedByte(genericEventCommand, strIPAddress);
  }

  /**
   * convert to byte for the event levy update
   *
   * @param jobDispatchEventRequest job dispatch event request data
   * @return GenericEventCommand
   */
  public GenericEventCommand convertToByteLevyUpdate(
      JobDispatchEventRequest jobDispatchEventRequest) {

    if (null == jobDispatchEventRequest.getIvdNo()) {
      throw new IvdConversionException(ErrorCode.INVALID_IVD_NUMBER.getCode());
    }
    byte[] bMsgToIVD =
        generateHeader(
            Integer.parseInt(jobDispatchEventRequest.getIvdNo()),
            IVDMessageType.LEVY_UPDATE.getId());
    GenericEventCommand genericEventCommand = convertLevyUpdate(bMsgToIVD, jobDispatchEventRequest);
    String strIPAddress = jobDispatchEventRequest.getIpAddress();
    return generatedByte(genericEventCommand, strIPAddress);
  }

  /**
   * Method to set byte size and array to GenericEventCommand
   *
   * @param bMsgToIVD byte array
   * @param jobDispatchEventRequest request data
   * @return GenericEventCommand
   */
  private GenericEventCommand convertLevyUpdate(
      byte[] bMsgToIVD, JobDispatchEventRequest jobDispatchEventRequest) {
    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);
    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    Optional.ofNullable(jobDispatchEventRequest.getLevy())
        .map(levy -> Map.entry(IVDFieldTag.LEVY, DataConversion.toIVDMoneyType(levy)))
        .ifPresent(entry -> valuesMap.put(entry.getKey(), entry.getValue()));

    Optional.ofNullable(jobDispatchEventRequest.getLevyWaiver())
        .map(
            levyWaiver ->
                Map.entry(IVDFieldTag.WAIVED_LEVY_AMT, DataConversion.toIVDMoneyType(levyWaiver)))
        .ifPresent(entry -> valuesMap.put(entry.getKey(), entry.getValue()));

    Optional.ofNullable(jobDispatchEventRequest.getJobNo())
        .ifPresent(jobNo -> valuesMap.put(IVDFieldTag.JOB_NUMBER, jobNo));

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * Method to map the value and tag to custom map and add to byte array
   *
   * @param bMsgToIVD byte array
   * @param jobDispatchEventRequest request
   * @return GenericEventCommand
   */
  private GenericEventCommand convertJobConfirmation(
      byte[] bMsgToIVD, JobDispatchEventRequest jobDispatchEventRequest) {
    byte[] header = Arrays.copyOf(bMsgToIVD, 9);
    IVDMessageContent message = new IVDMessageContent();

    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), 4);

    Arrays.fill(header, 8, 9, (byte) 0);
    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.SOURCE, jobDispatchEventRequest.getMessageSource());
    valuesMap.put(IVDFieldTag.JOB_NUMBER, jobDispatchEventRequest.getJobNo());
    valuesMap.put(IVDFieldTag.IP_ADDRESS, jobDispatchEventRequest.getIpAddress());
    Optional.ofNullable(jobDispatchEventRequest.getPlatformFeeItem())
        .ifPresent(platformFeeItems -> getPlatformFeeListItem(platformFeeItems, message));
    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * converting to bean to byte for Voice Streaming
   *
   * @param voiceEventRequest voiceEventRequest
   * @param isStart isStart - if true then its START else STOP
   * @return genericEventCommand
   */
  @Override
  public GenericEventCommand convertToByteVoiceStreaming(
      VoiceEventRequest voiceEventRequest, boolean isStart) {
    byte[] bMsgToIVD = generateHeader(voiceEventRequest.getIvdNo(), voiceEventRequest.getId());
    GenericEventCommand genericEventCommand;
    if (isStart) {
      genericEventCommand = convertVoiceStreamingForStart(bMsgToIVD, voiceEventRequest);
    } else {
      genericEventCommand = convertVoiceStreamingForStop(bMsgToIVD, voiceEventRequest);
    }
    String strIPAddress = voiceEventRequest.getIpAddress();
    return generatedByte(genericEventCommand, strIPAddress);
  }

  /**
   * converting to bean to byte for START event of Voice Streaming
   *
   * @param voiceEventRequest voiceEventRequest
   * @param bMsgToIVD bMsgToIVD
   * @return genericEventCommand
   */
  private GenericEventCommand convertVoiceStreamingForStart(
      byte[] bMsgToIVD, VoiceEventRequest voiceEventRequest) {
    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);

    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.DURATION, voiceEventRequest.getDuration());
    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);
    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * converting to bean to byte for STOP event of Voice Streaming
   *
   * @param voiceEventRequest voiceEventRequest
   * @param bMsgToIVD bMsgToIVD
   * @return genericEventCommand
   */
  private GenericEventCommand convertVoiceStreamingForStop(
      byte[] bMsgToIVD, VoiceEventRequest voiceEventRequest) {
    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);

    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);
    int msgLength = header.length;

    System.arraycopy(header, 0, bMsgToIVD, 0, msgLength);
    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.DURATION, voiceEventRequest.getDuration());
    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);
    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * Method to convert the request to byte array for driver performance event
   *
   * @param driverPerformance driverPerformance
   * @param esbVehicle esbVehicle
   * @return GenericEventCommand
   */
  @Override
  public GenericEventCommand convertToByteDriverPerformance(
      String driverPerformance, EsbVehicle esbVehicle) {

    byte[] bMsgToIVD =
        generateHeader(esbVehicle.getByteData().getIvdNo(), esbVehicle.getResponseMessageId());
    GenericEventCommand genericEventCommand =
        convertDriverPerformance(bMsgToIVD, driverPerformance);

    return generatedByte(genericEventCommand, esbVehicle.getVehicleDetails().getIpAddress());
  }

  /**
   * Method to convert to byte for driverPerformance response
   *
   * @param bMsgToIVD byte array
   * @param driverPerformance driverPerformance string
   * @return GenericEventCommand
   */
  public GenericEventCommand convertDriverPerformance(byte[] bMsgToIVD, String driverPerformance) {
    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);

    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);

    setDriverPerformanceResMsg(driverPerformance, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  private static void setDriverPerformanceResMsg(String responseMsg, IVDMessageContent message) {
    if (responseMsg == null) {
      IVDListItem item = new IVDListItem();
      item.putText(IVDFieldTag.SINGLE_LINE_TEXT, "No Performance data found");
      message.putListItem(IVDFieldTag.MULTI_LINE_TEXT, item, false);
    } else {
      String[] resArray = responseMsg.split("\n");
      if (resArray.length > 0) {
        IVDListItem item = new IVDListItem();
        for (String s : resArray) {
          item.putText(IVDFieldTag.SINGLE_LINE_TEXT, s);
          message.putListItem(IVDFieldTag.MULTI_LINE_TEXT, item, false);
        }
      }
    }
  }

  /**
   * Method to set byte size and array to GenericEventCommand
   *
   * @param bMsgToIVD byte array
   * @param verifyOtpResponse verify otp values
   * @return GenericEventCommand
   */
  private GenericEventCommand convertVerifyOtp(
      byte[] bMsgToIVD, VerifyOtpResponse verifyOtpResponse) {

    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);

    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.FORGOT_PWD_RESULT, verifyOtpResponse.getPassApproval());

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * Method to set byte size and array to GenericEventCommand
   *
   * @param bMsgToIVD byte array
   * @param driverId, newPin, changePinResult
   * @return GenericEventCommand
   */
  private GenericEventCommand convertChangePin(
      byte[] bMsgToIVD, String driverId, String newPin, Boolean changePinResult) {

    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);

    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.DRIVER_ID, driverId);
    valuesMap.put(IVDFieldTag.NEW_PIN, newPin);
    valuesMap.put(IVDFieldTag.CHANGE_PIN_RESULT, changePinResult);

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * Method to generate message Please do not alter this method's implementation without thorough
   * review of the potential impact
   *
   * @param message generated message
   * @param header generated header
   * @param bMsgToIVD converted byte
   * @return GenericEventCommand
   */
  @NotNull
  private static GenericEventCommand getGenericEventCommand(
      IVDMessageContent message, byte[] header, byte[] bMsgToIVD) {

    byte[] completeMsg;
    byte[] content = message.toHexBytes();
    if (content.length == 0) {
      log.debug("Plain header, no content message");
      completeMsg = header;
    } else {
      completeMsg = BytesUtil.joinBytes(header, content);
    }
    int msgLength = completeMsg.length;

    System.arraycopy(completeMsg, 0, bMsgToIVD, 0, msgLength);

    GenericEventCommand genericEventCommand = new GenericEventCommand();
    genericEventCommand.setByteArray(bMsgToIVD);
    genericEventCommand.setByteArraySize(msgLength);
    return genericEventCommand;
  }

  @NotNull
  private static GenericEventCommand getGenericEventCommandForMessageAck(
      IVDMessageContent message, byte[] header, byte[] bMsgToIVD, String ipAddress) {

    byte[] completeMsg;
    byte[] content = message.toHexBytes();
    if (content.length == 0) {
      log.debug("Plain header, no content message");
      completeMsg = header;
    } else {
      completeMsg = BytesUtil.joinBytes(header, content);
    }
    int msgLength = completeMsg.length;

    System.arraycopy(completeMsg, 0, bMsgToIVD, 0, msgLength);

    // append ip
    int totalLength = msgLength + VehicleCommFrameworkConstants.IPADDR_LEN;
    int i;
    for (i = msgLength; i < (msgLength + ipAddress.length()); i++) {
      bMsgToIVD[i] = (byte) ipAddress.charAt(i - msgLength);
    }
    for (i = (msgLength + ipAddress.length()); i < (totalLength); i++) {
      bMsgToIVD[i] = ' ';
    }

    byte[] bToIVD = new byte[totalLength];
    System.arraycopy(bMsgToIVD, 0, bToIVD, 0, totalLength);

    GenericEventCommand genericEventCommand = new GenericEventCommand();
    genericEventCommand.setByteArray(bToIVD);
    genericEventCommand.setByteArraySize(msgLength);
    return genericEventCommand;
  }

  /**
   * Method to set byte size and array to GenericEventCommand
   *
   * @param bMsgToIVD byte array
   * @param jobDispatchEventRequest request data
   * @return GenericEventCommand
   */
  private GenericEventCommand convertCallOut(
      byte[] bMsgToIVD, JobDispatchEventRequest jobDispatchEventRequest) {

    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);

    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.JOB_NUMBER, jobDispatchEventRequest.getJobNo());
    valuesMap.put(IVDFieldTag.CALL_OUT_STATUS, jobDispatchEventRequest.getCalloutStatus());

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * Method to set byte size and array to jobDispatchEventCommand
   *
   * @param genericEventCommand Generic event command
   * @param strIPAddress IP ADDRESS
   * @return JobDispatchEventCommand
   */
  private GenericEventCommand generatedByte(
      GenericEventCommand genericEventCommand, String strIPAddress) {
    if (strIPAddress == null) {
      log.info("[generatedByte] Error occurred in generatedByte method");
      throw new IvdConversionException(ErrorCode.INVALID_IPADDRESS.getCode());
    }
    int i;
    int nMsgLen = genericEventCommand.getByteArraySize();

    for (i = nMsgLen; i < (nMsgLen + strIPAddress.length()); i++) {
      genericEventCommand.getByteArray()[i] = (byte) strIPAddress.charAt(i - nMsgLen);
    }

    for (i = nMsgLen + strIPAddress.length();
        i < (nMsgLen + VehicleCommFrameworkConstants.IPADDR_LEN);
        i++) {
      genericEventCommand.getByteArray()[i] = ' ';
    }
    genericEventCommand.setByteArraySize(nMsgLen + VehicleCommFrameworkConstants.IPADDR_LEN);

    /* Copy bMsgToIVD to a smaller array for sending */
    byte[] bToIVD = new byte[genericEventCommand.getByteArraySize()];
    System.arraycopy(
        genericEventCommand.getByteArray(), 0, bToIVD, 0, genericEventCommand.getByteArraySize());

    genericEventCommand.setByteArray(bToIVD);
    genericEventCommand.setByteArrayMessage(BytesUtil.toString(bToIVD));
    return genericEventCommand;
  }

  /**
   * Method to generate header
   *
   * @return byte array
   */
  private byte[] generateHeader(int ivdNo, int msgId) {
    byte[] bMsgToIVD = new byte[VehicleCommFrameworkConstants.BUF_SIZE];

    bMsgToIVD[0] = (byte) msgId;
    bMsgToIVD[1] =
        (byte)
            ((EscUtilities.toBoolOneOrZero(VehCommUtils.isAckNeeded(msgId)) << 7)
                + VehCommUtils.getSerialNum());
    bMsgToIVD[2] = (byte) ivdNo;
    bMsgToIVD[3] = (byte) ((long) ivdNo >> 8);

    return bMsgToIVD;
  }

  /**
   * Method to map the value and tag to custom map and add to byte array
   *
   * @param bMsgToIVD byte array
   * @param jobDispatchEventRequest request
   * @return JobDispatchEventCommand
   */
  private GenericEventCommand convertJobModification(
      byte[] bMsgToIVD, JobDispatchEventRequest jobDispatchEventRequest) {

    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);

    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.SOURCE, jobDispatchEventRequest.getMessageSource());
    valuesMap.put(IVDFieldTag.JOB_NUMBER, jobDispatchEventRequest.getJobNo());

    String jobTypeInNumber =
        VehicleCommFrameworkConstants.ADVANCE_JOB_TYPE.equalsIgnoreCase(
                jobDispatchEventRequest.getJobType())
            ? "1"
            : "0";

    valuesMap.put(IVDFieldTag.JOB_TYPE, jobTypeInNumber);
    valuesMap.put(
        IVDFieldTag.PAYMENT_METHOD,
        vehicleCommCacheService
            .getPaymentMethodFromCacheAsName(jobDispatchEventRequest.getPaymentMethod())
            .getPaymentMode());
    valuesMap.put(IVDFieldTag.DISPATCH_METHOD, jobDispatchEventRequest.getDispatchMethod());
    valuesMap.put(
        IVDFieldTag.CUSTOMER_PRIORITY,
        Boolean.TRUE.equals(jobDispatchEventRequest.getPriorityCustomer()) ? 1 : 0);
    valuesMap.put(
        IVDFieldTag.PICKUP_TIME,
        VehCommUtils.localDateTimeToDate(jobDispatchEventRequest.getPickupTime()));
    valuesMap.put(
        IVDFieldTag.PICKUP_X, DataConversion.toCoordData(jobDispatchEventRequest.getPickupLng()));
    valuesMap.put(
        IVDFieldTag.PICKUP_Y, DataConversion.toCoordData(jobDispatchEventRequest.getPickupLat()));
    valuesMap.put(IVDFieldTag.PICKUP_ADDRESS, jobDispatchEventRequest.getPickupAddr());
    valuesMap.put(IVDFieldTag.ARRIVAL_REQUIRED, jobDispatchEventRequest.getArrivalRequired());

    Optional.ofNullable(jobDispatchEventRequest.getBookingChannel())
        .filter(StringUtils::isNotEmpty)
        .map(
            channel ->
                Map.entry(IVDFieldTag.BOOKING_CHANNEL, VehCommUtils.getChannelIntVal(channel)))
        .ifPresent(entry -> valuesMap.put(entry.getKey(), entry.getValue()));

    double bookingFee = 0.0;
    Optional.ofNullable(jobDispatchEventRequest.getBookingFee())
        .ifPresent(
            (fee -> valuesMap.put(IVDFieldTag.BOOKING_FEE, DataConversion.toIVDMoneyType(fee))));

    Optional.ofNullable(jobDispatchEventRequest.getFareType())
        .filter(fareType -> fareType != 0)
        .ifPresent(fareType -> valuesMap.put(IVDFieldTag.FARE_TYPE, fareType));

    Optional.ofNullable(jobDispatchEventRequest.getDeposit())
        .ifPresent(
            deposit -> valuesMap.put(IVDFieldTag.DEPOSIT, DataConversion.toIVDMoneyType(deposit)));

    Optional.ofNullable(jobDispatchEventRequest.getLevy())
        .map(levy -> Map.entry(IVDFieldTag.LEVY, DataConversion.toIVDMoneyType(levy)))
        .ifPresent(entry -> valuesMap.put(entry.getKey(), entry.getValue()));

    Optional.ofNullable(jobDispatchEventRequest.getJobMeritPoint())
        .ifPresent(meritPoint -> valuesMap.put(IVDFieldTag.JOB_MERIT_POINTS, meritPoint));

    Optional.ofNullable(jobDispatchEventRequest.getNoShowTiming())
        .filter(noShowTiming -> noShowTiming >= 0)
        .ifPresent(noShowTiming -> valuesMap.put(IVDFieldTag.NO_SHOW_TIMEOUT, noShowTiming));

    Optional.ofNullable(jobDispatchEventRequest.getAccountId())
        .ifPresent(accountId -> valuesMap.put(IVDFieldTag.ACCOUNT_ID, accountId));

    Optional.ofNullable(jobDispatchEventRequest.getCompanyName())
        .ifPresent(companyName -> valuesMap.put(IVDFieldTag.ACCOUNT_COMPANY_NAME, companyName));

    Optional.ofNullable(jobDispatchEventRequest.getPassengerName())
        .ifPresent(passengerName -> valuesMap.put(IVDFieldTag.PASSENGER_NAME, passengerName));

    Optional.ofNullable(jobDispatchEventRequest.getPassengerContactNumber())
        .ifPresent(
            contactNumber -> valuesMap.put(IVDFieldTag.PASSENGER_CONTACT_NUMBER, contactNumber));

    setDestinationDetails(jobDispatchEventRequest, valuesMap);

    Optional.ofNullable(jobDispatchEventRequest.getNotes())
        .ifPresent(notes -> valuesMap.put(IVDFieldTag.JOB_NOTES, notes));

    Optional.ofNullable(jobDispatchEventRequest.getWaitingPoint())
        .ifPresent(waitingPoint -> valuesMap.put(IVDFieldTag.WAITING_POINT, waitingPoint));

    Optional.ofNullable(jobDispatchEventRequest.getRemark())
        .ifPresent(remark -> valuesMap.put(IVDFieldTag.ADDRESS_INFO, remark));

    Optional.ofNullable(jobDispatchEventRequest.getProductId())
        .filter(StringUtils::isNotEmpty)
        .map(vehicleCommCacheService::getBookingProductDetailsFromCache)
        .map(BookingProductData::getInVehicleDeviceCode)
        .map(String::valueOf)
        .ifPresentOrElse(
            productIdVehicleCode -> valuesMap.put(IVDFieldTag.PRODUCT_ID, productIdVehicleCode),
            () -> valuesMap.put(IVDFieldTag.PRODUCT_ID, "1"));

    Optional.ofNullable(jobDispatchEventRequest.getTariffInfo())
        .ifPresent(tariffInfo -> setTariffs(tariffInfo, message));

    Optional.ofNullable(jobDispatchEventRequest.getExtraStopsInfo())
        .ifPresent(extraStopsInfo -> setExtraStops(extraStopsInfo, message));

    valuesMap.put(IVDFieldTag.SHOW_OFFER_SCREEN, jobDispatchEventRequest.getSosFlag());

    CmsConfiguration cmsConfiguration = configurationService.getCmsConfiguration();
    int jobDispInclMpInfoFlag =
        cmsConfiguration.getJobDispInclMpInfoFlag() == null
            ? VehicleCommFrameworkConstants.JOB_DISP_INCL_MP_INFO_FLAG
            : cmsConfiguration.getJobDispInclMpInfoFlag();

    if (jobDispInclMpInfoFlag == 1) {

      Optional.ofNullable(jobDispatchEventRequest.getCcNumber())
          .filter(StringUtils::isNotEmpty)
          .ifPresent(ccNumber -> valuesMap.put(IVDFieldTag.CC_NUMBER, ccNumber));

      Optional.ofNullable(jobDispatchEventRequest.getCcExpiry())
          .filter(StringUtils::isNotEmpty)
          .ifPresent(ccExpiry -> valuesMap.put(IVDFieldTag.CC_EXPIRY_DATE, ccExpiry));
    }

    Optional.ofNullable(jobDispatchEventRequest.getPromoCode())
        .filter(StringUtils::isNotEmpty)
        .ifPresent(promoCode -> valuesMap.put(IVDFieldTag.PROMO_CODE, promoCode));

    String waiveBookingFee = jobDispatchEventRequest.getPromoWaiveBookingFee();
    Double promoAmtVal = jobDispatchEventRequest.getPromoAmount();
    if (VehicleCommFrameworkConstants.Y.equalsIgnoreCase(waiveBookingFee)) {
      promoAmtVal = bookingFee;
    }
    Optional.ofNullable(promoAmtVal)
        .ifPresent(
            value -> valuesMap.put(IVDFieldTag.PROMO_AMOUNT, DataConversion.toIVDMoneyType(value)));
    valuesMap.put(
        IVDFieldTag.ENFORCE_MODIFICATION,
        BooleanUtils.negate(jobDispatchEventRequest.getCanReject()));
    valuesMap.put(IVDFieldTag.COLLECT_FARE, jobDispatchEventRequest.getCollectFare());
    if (StringUtils.isNotEmpty(jobDispatchEventRequest.getPaymentPlus())) {
      valuesMap.put(IVDFieldTag.PAYMENT_INFO, jobDispatchEventRequest.getPaymentPlus());
    }
    valuesMap.put(IVDFieldTag.AUTO_ASSIGN_FLAG, jobDispatchEventRequest.getAutoAssignFlag());
    valuesMap.put(IVDFieldTag.DISP_CALL_OUT_BTN, jobDispatchEventRequest.getEnableCalloutButton());

    Optional.ofNullable(jobDispatchEventRequest.getAutoAcceptFlag())
        .filter(flag -> flag == VehicleCommFrameworkConstants.ENABLE_AUTO_BID)
        .ifPresent(flag -> valuesMap.put(IVDFieldTag.AUTO_BID_FLAG, flag));

    setNewGstDetails(jobDispatchEventRequest, valuesMap);

    if (jobDispatchEventRequest.getNewAdminValue() != null
        && jobDispatchEventRequest.getNewAdminDiscountVal() != null
        && jobDispatchEventRequest.getNewAdminEffectiveDate() != null) {

      sendNewAdminValues(jobDispatchEventRequest, valuesMap);
    }

    if (jobDispatchEventRequest.getCurrentGst() != null) {
      valuesMap.put(IVDFieldTag.CURRENT_GST, jobDispatchEventRequest.getCurrentGst());
      valuesMap.put(IVDFieldTag.CURRENT_GST_INCLUSIVE, jobDispatchEventRequest.getCurrentGstIncl());
    }

    if (jobDispatchEventRequest.getCurrentAdminVal() != null
        && jobDispatchEventRequest.getCurrentAdminDiscountVal() != null) {

      sendCurrAdminValues(jobDispatchEventRequest, valuesMap);
    }

    Optional.ofNullable(jobDispatchEventRequest.getCabchargePolicy())
        .ifPresent(cabchargePolicy -> setCabChargePolicy(cabchargePolicy, valuesMap));

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  private GenericEventCommand convertStreetHail(
      byte[] bMsgToIVD, JobDispatchEventRequest jobDispatchEventRequest) {
    byte[] header = Arrays.copyOf(bMsgToIVD, 9);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), 4);
    header[8] = (byte) 0;
    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.SOURCE, jobDispatchEventRequest.getMessageSource());
    valuesMap.put(IVDFieldTag.JOB_NUMBER, jobDispatchEventRequest.getJobNo());
    valuesMap.put(
        IVDFieldTag.PAYMENT_METHOD,
        vehicleCommCacheService
            .getPaymentMethodFromCacheAsName(jobDispatchEventRequest.getPaymentMethod())
            .getPaymentMode());
    valuesMap.put(IVDFieldTag.PREMIER_ALLOWED, jobDispatchEventRequest.getIsPremierAllowed());
    Optional.ofNullable(jobDispatchEventRequest.getBookingChannel())
        .filter(StringUtils::isNotEmpty)
        .map(
            channel ->
                Map.entry(IVDFieldTag.BOOKING_CHANNEL, VehCommUtils.getChannelIntVal(channel)))
        .ifPresent(entry -> valuesMap.put(entry.getKey(), entry.getValue()));
    Optional.ofNullable(jobDispatchEventRequest.getAccountId())
        .ifPresent(accountId -> valuesMap.put(IVDFieldTag.ACCOUNT_ID, accountId));
    Optional.ofNullable(jobDispatchEventRequest.getCompanyName())
        .ifPresent(companyName -> valuesMap.put(IVDFieldTag.ACCOUNT_COMPANY_NAME, companyName));
    Optional.ofNullable(jobDispatchEventRequest.getPassengerName())
        .ifPresent(passengerName -> valuesMap.put(IVDFieldTag.PASSENGER_NAME, passengerName));

    Optional.ofNullable(jobDispatchEventRequest.getPassengerContactNumber())
        .ifPresent(
            contactNumber -> valuesMap.put(IVDFieldTag.PASSENGER_CONTACT_NUMBER, contactNumber));
    Optional.ofNullable(jobDispatchEventRequest.getPromoCode())
        .filter(StringUtils::isNotEmpty)
        .ifPresent(promoCode -> valuesMap.put(IVDFieldTag.PROMO_CODE, promoCode));
    String waiveBookingFee = jobDispatchEventRequest.getPromoWaiveBookingFee();
    double bookingFee = 0.0;
    Double promoAmtVal =
        jobDispatchEventRequest.getPromoAmount() != null
            ? jobDispatchEventRequest.getPromoAmount()
            : null;
    if (VehicleCommFrameworkConstants.Y.equalsIgnoreCase(waiveBookingFee)) {
      promoAmtVal = bookingFee;
    }
    Optional.ofNullable(promoAmtVal)
        .ifPresent(
            promoAmtValue ->
                valuesMap.put(
                    IVDFieldTag.PROMO_AMOUNT, DataConversion.toIVDMoneyType(promoAmtValue)));
    valuesMap.put(IVDFieldTag.PAYMENT_INFO, jobDispatchEventRequest.getPaymentPlus());
    setNewGstDetails(jobDispatchEventRequest, valuesMap);

    if (jobDispatchEventRequest.getCurrentGst() != null) {
      valuesMap.put(IVDFieldTag.CURRENT_GST, jobDispatchEventRequest.getCurrentGst());
      valuesMap.put(IVDFieldTag.CURRENT_GST_INCLUSIVE, jobDispatchEventRequest.getCurrentGstIncl());
    }
    Optional.ofNullable(jobDispatchEventRequest.getCcNumber())
        .filter(StringUtils::isNotEmpty)
        .ifPresent(ccNumber -> valuesMap.put(IVDFieldTag.CC_NUMBER, ccNumber));
    Optional.ofNullable(jobDispatchEventRequest.getCcExpiry())
        .filter(StringUtils::isNotEmpty)
        .ifPresent(ccExpiry -> valuesMap.put(IVDFieldTag.CC_EXPIRY_DATE, ccExpiry));

    valuesMap.put(IVDFieldTag.CAB_REWARDS_FLAG, jobDispatchEventRequest.getLoyaltyEnable());
    Optional.ofNullable(jobDispatchEventRequest.getLoyaltyAmount())
        .ifPresent(
            loyaltyAmount ->
                valuesMap.put(
                    IVDFieldTag.CAB_REWARDS_AMT, DataConversion.toIVDMoneyType(loyaltyAmount)));

    valuesMap.put(IVDFieldTag.COLLECT_FARE, jobDispatchEventRequest.getCollectFare());
    Optional.ofNullable(jobDispatchEventRequest.getPaymentPlus())
        .ifPresent(paymentPlus -> valuesMap.put(IVDFieldTag.PAYMENT_INFO, paymentPlus));
    Optional.ofNullable(jobDispatchEventRequest.getPrivateField())
        .ifPresent(privateFiled -> valuesMap.put(IVDFieldTag.PRIVATE_FIELD, privateFiled));

    Optional.ofNullable(jobDispatchEventRequest.getComfortProtectPremium())
        .ifPresent(
            comfortProPremium ->
                valuesMap.put(
                    IVDFieldTag.HLA_FEE, DataConversion.toIVDMoneyType(comfortProPremium)));
    if (jobDispatchEventRequest.getCurrentAdminVal() != null
        && jobDispatchEventRequest.getCurrentAdminDiscountVal() != null) {
      sendCurrAdminValues(jobDispatchEventRequest, valuesMap);
    }
    if (jobDispatchEventRequest.getNewAdminValue() != null
        && jobDispatchEventRequest.getNewAdminDiscountVal() != null
        && jobDispatchEventRequest.getNewAdminEffectiveDate() != null) {

      sendNewAdminValues(jobDispatchEventRequest, valuesMap);
    }
    if (jobDispatchEventRequest.getCpFreeInsurance() != null
        && "Y".equalsIgnoreCase(jobDispatchEventRequest.getCpFreeInsurance())) {
      valuesMap.put(IVDFieldTag.IS_FEE_WAIVED, true);
    } else if (jobDispatchEventRequest.getCpFreeInsurance() != null
        && "N".equalsIgnoreCase(jobDispatchEventRequest.getCpFreeInsurance())) {
      valuesMap.put(IVDFieldTag.IS_FEE_WAIVED, false);
    }
    Optional.ofNullable(jobDispatchEventRequest.getPlatformFeeItem())
        .ifPresent(platformFeeItems -> getPlatformFeeListItem(platformFeeItems, message));
    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);
    Optional.ofNullable(jobDispatchEventRequest.getCabchargePolicy())
        .ifPresent(cabchargePolicy -> setCabChargePolicy(cabchargePolicy, valuesMap));
    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * Method to add bew GST values to map
   *
   * @param jobDispatchEventRequest request
   * @param valuesMap map
   */
  private static void setNewGstDetails(
      JobDispatchEventRequest jobDispatchEventRequest, Map<IVDFieldTag, Object> valuesMap) {
    if (jobDispatchEventRequest.getNewGst() != null
        && jobDispatchEventRequest.getNewGstEffectiveDate() != null) {
      valuesMap.put(IVDFieldTag.NEW_GST, jobDispatchEventRequest.getNewGst());
      valuesMap.put(IVDFieldTag.NEW_GST_INCLUSIVE, jobDispatchEventRequest.getNewGstIncl());
      valuesMap.put(
          IVDFieldTag.NEW_GST_EFFECTIVE_DATE,
          VehCommUtils.localDateTimeToDate(jobDispatchEventRequest.getNewGstEffectiveDate()));
    }
  }

  /**
   * Method to add destination details to map
   *
   * @param jobDispatchEventRequest request
   * @param valuesMap map
   */
  private static void setDestinationDetails(
      JobDispatchEventRequest jobDispatchEventRequest, Map<IVDFieldTag, Object> valuesMap) {
    Double dLon = jobDispatchEventRequest.getDestLng();
    Double dLat = jobDispatchEventRequest.getDestLat();
    if (ObjectUtils.anyNull(dLon, dLat)
        || EscUtilities.doubleEquals(dLon, 0.0)
        || EscUtilities.doubleEquals(dLat, 0.0)) {
      valuesMap.put(IVDFieldTag.DESTINATION_ADDRESS, jobDispatchEventRequest.getDestAddr());
    } else {
      valuesMap.put(IVDFieldTag.DESTINATION_X, DataConversion.toCoordData(dLon));
      valuesMap.put(IVDFieldTag.DESTINATION_Y, DataConversion.toCoordData(dLat));
      valuesMap.put(IVDFieldTag.DESTINATION_ADDRESS, jobDispatchEventRequest.getDestAddr());
    }
  }

  /**
   * Method to add cab charge policy details to map
   *
   * @param cabchargePolicy cabchargePolicy
   * @param valuesMap map
   */
  private static void setCabChargePolicy(
      CabchargePolicy cabchargePolicy, Map<IVDFieldTag, Object> valuesMap) {
    try {
      if (cabchargePolicy.getPolicyDetails() != null) {
        for (PolicyDetails infoVO : cabchargePolicy.getPolicyDetails()) {
          IVDListItem policyListItem = getPolicyListItem(infoVO);
          valuesMap.put(IVDFieldTag.POLICY_LOCATION_CHECK_CRITERIA_LIST, policyListItem);
        }
      }
      if (cabchargePolicy.getAmountCap() != null) {
        valuesMap.put(
            IVDFieldTag.POLICY_AMOUNT_CAP,
            DataConversion.toIVDMoneyType(cabchargePolicy.getAmountCap()));
      }

    } catch (Exception e) {
      log.error("[setCabChargePolicy] Exception in setCabChargePolicy : ", e);
    }
  }

  /**
   * Method to add policy details
   *
   * @param infoVO policy details
   * @return IVDListItem
   */
  private static IVDListItem getPolicyListItem(PolicyDetails infoVO) {
    IVDListItem policyIvdListItem = new IVDListItem();
    try {
      if (infoVO.getPolicyPickupLat() != null && infoVO.getPolicyPickupLng() != null) {
        policyIvdListItem.putCoord(
            IVDFieldTag.POLICY_PICKUP_X, DataConversion.toCoordData(infoVO.getPolicyDestLng()));
        policyIvdListItem.putCoord(
            IVDFieldTag.POLICY_PICKUP_Y, DataConversion.toCoordData(infoVO.getPolicyDestLat()));
      }
      if (infoVO.getPolicyDestLat() != null && infoVO.getPolicyDestLng() != null) {
        policyIvdListItem.putCoord(
            IVDFieldTag.POLICY_DEST_X, DataConversion.toCoordData(infoVO.getPolicyDestLng()));
        policyIvdListItem.putCoord(
            IVDFieldTag.POLICY_DEST_Y, DataConversion.toCoordData(infoVO.getPolicyDestLat()));
      }
      if (infoVO.getPolicyRadius() != null) {
        policyIvdListItem.putUInt32(
            IVDFieldTag.POLICY_RADIUS,
            DataConversion.toMeter(infoVO.getPolicyRadius().longValue()));
      }

    } catch (Exception e) {
      log.error("[getPolicyListItem] Exception in getPolicyListItem : ", e);
    }
    return policyIvdListItem;
  }

  /**
   * Method to add current admin values to map
   *
   * @param jobDispatchEventRequest request
   * @param valuesMap map
   */
  private static void sendCurrAdminValues(
      JobDispatchEventRequest jobDispatchEventRequest, Map<IVDFieldTag, Object> valuesMap) {
    try {
      valuesMap.put(IVDFieldTag.CURRENT_ADMIN_VALUE, jobDispatchEventRequest.getCurrentAdminVal());
      valuesMap.put(
          IVDFieldTag.CURRENT_ADMIN_DISCOUNT_VALUE,
          jobDispatchEventRequest.getCurrentAdminDiscountVal());
      valuesMap.put(IVDFieldTag.CURRENT_ADMIN_TYPE, jobDispatchEventRequest.getCurrentAdminType());
      valuesMap.put(
          IVDFieldTag.CURRENT_ADMIN_GST_MSG, jobDispatchEventRequest.getCurrentAdminGstMsg());
    } catch (Exception e) {
      log.error(
          "[sendCurrAdminValues] Exception occurred in setting Current Admin Gst Values : ", e);
    }
  }

  /**
   * Method to add new admin values to map
   *
   * @param jobDispatchEventRequest request
   * @param valuesMap map
   */
  private static void sendNewAdminValues(
      JobDispatchEventRequest jobDispatchEventRequest, Map<IVDFieldTag, Object> valuesMap) {
    try {
      valuesMap.put(IVDFieldTag.NEW_ADMIN_VALUE, jobDispatchEventRequest.getNewAdminValue());
      valuesMap.put(
          IVDFieldTag.NEW_ADMIN_DISCOUNT_VALUE, jobDispatchEventRequest.getNewAdminDiscountVal());
      valuesMap.put(IVDFieldTag.NEW_ADMIN_TYPE, jobDispatchEventRequest.getNewAdminType());
      valuesMap.put(IVDFieldTag.NEW_ADMIN_GST_MSG, jobDispatchEventRequest.getNewAdminGstMsg());
      valuesMap.put(
          IVDFieldTag.NEW_ADMIN_EFFECTIVE_DATE,
          VehCommUtils.localDateTimeToDate(jobDispatchEventRequest.getNewAdminEffectiveDate()));
    } catch (Exception e) {
      log.error("[sendNewAdminValues] Exception occurred in setting New Admin Gst Values : ", e);
    }
  }

  /**
   * Method to put extra stop details to message
   *
   * @param extraStopsInfo extra stop details
   * @param message message content
   */
  private static void setExtraStops(
      List<ExtraStopsInfo> extraStopsInfo, IVDMessageContent message) {

    if (extraStopsInfo != null && !extraStopsInfo.isEmpty()) {
      extraStopsInfo.forEach(
          extraStopToken -> {
            IVDListItem extraStop = new IVDListItem();
            extraStop.putText(IVDFieldTag.EXTRA_STOP_NAME, extraStopToken.getExtraStopName());
            extraStop.putByte(
                IVDFieldTag.EXTRA_STOP_QUANTITY,
                (byte) extraStopToken.getExtraStopQty().intValue());
            if (StringUtils.isNotEmpty(extraStopToken.getExtraStopDetail())) {

              extraStop.putText(IVDFieldTag.EXTRA_STOP_DETAIL, extraStopToken.getExtraStopDetail());
              message.putListItem(IVDFieldTag.EXTRA_STOPS, extraStop, false);
            }
          });
    }
  }

  /**
   * Method to put tariff details into message
   *
   * @param tariffInfo tariff details
   * @param message message content
   */
  private static void setTariffs(List<TariffInfo> tariffInfo, IVDMessageContent message) {

    if (tariffInfo != null && !tariffInfo.isEmpty()) {
      tariffInfo.forEach(
          tariff -> {
            IVDListItem tariffItem = new IVDListItem();
            tariffItem.putText(IVDFieldTag.TARIFF_CODE, tariff.getTariffTypeCode());
            tariffItem.putMoney(
                IVDFieldTag.TARIFF_AMOUNT,
                DataConversion.toIVDMoneyType(tariff.getDiscountedTotal().getFare()));
            tariffItem.putByte(
                IVDFieldTag.TARIFF_QUANTITY, (byte) tariff.getTariffUnit().intValue());
            message.putListItem(IVDFieldTag.TARIFF, tariffItem, false);
          });
    }
  }

  /**
   * Method to put Multi Stop to message
   *
   * @param multiStop - multistop
   * @param message - message content
   */
  private static void setMultiStops(MultiStop multiStop, IVDMessageContent message) {
    if (ObjectUtils.allNull(
        multiStop.getIntermediateLng(),
        multiStop.getIntermediateLat(),
        multiStop.getIntermediateAddr())) {
      log.debug("[setMultiStops] MultiStop is empty");
      return;
    }
    IVDListItem multiStopItem = new IVDListItem();
    multiStopItem.putText(IVDFieldTag.MULTI_STOP_ADDRESS, multiStop.getIntermediateAddr());
    multiStopItem.putCoord(
        IVDFieldTag.MULTI_STOP_X, DataConversion.toCoordData(multiStop.getIntermediateLng()));
    multiStopItem.putCoord(
        IVDFieldTag.MULTI_STOP_Y, DataConversion.toCoordData(multiStop.getIntermediateLat()));
    message.putListItem(IVDFieldTag.MULTI_STOP, multiStopItem, false);
  }

  private GenericEventCommand convertVehicleTrackingForStop(byte[] bMsgToIVD) {
    GenericEventCommand genericEventCommand = new GenericEventCommand();
    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);
    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);
    int msgLength = header.length;
    System.arraycopy(header, 0, bMsgToIVD, 0, msgLength);
    genericEventCommand.setByteArray(bMsgToIVD);
    genericEventCommand.setByteArraySize(msgLength);
    return genericEventCommand;
  }

  /**
   * converting to bean to byte for vehicle tracking
   *
   * @param vehicleTrackCommand vehicleTrackCommand
   * @param isStart isStart - if true then its START else STOP
   * @return genericEventCommand
   */
  @Override
  public GenericEventCommand convertToByteVehicleTrack(
      VehicleTrackCommand vehicleTrackCommand, boolean isStart) {
    log.debug("Start of convertToByteVehicleTrack: {} ", vehicleTrackCommand);
    byte[] bMsgToIVD = generateHeader(vehicleTrackCommand.getIvdNo(), vehicleTrackCommand.getId());
    GenericEventCommand genericEventCommand;
    if (isStart) {
      genericEventCommand = convertVehicleTrackingForStart(bMsgToIVD, vehicleTrackCommand);
    } else {
      genericEventCommand = convertVehicleTrackingForStop(bMsgToIVD);
    }
    String strIPAddress = vehicleTrackCommand.getIpAddress();
    return generatedByte(genericEventCommand, strIPAddress);
  }

  private GenericEventCommand convertVehicleTrackingForStart(
      byte[] bMsgToIVD, VehicleTrackCommand vehicleTrackCommand) {
    log.debug("Start of convertVehicleTrackingForStart in BeanToByteConverterImpl");
    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);

    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.REPORTING_INTERVAL, vehicleTrackCommand.getInterval());
    valuesMap.put(IVDFieldTag.DURATION, vehicleTrackCommand.getDuration());
    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);
    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  private GenericEventCommand convertJobCancelForIvdResponse(
      byte[] bMsgToIVD, JobDispatchEventRequest jobDispatchEventRequest) {
    log.debug("Start of convertJobCancelForIvdResponse in BeanToByteConverterImpl");
    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);
    IVDMessageContent message = new IVDMessageContent();
    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);

    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);
    valuesMap.put(IVDFieldTag.SOURCE, jobDispatchEventRequest.getMessageSource());
    valuesMap.put(IVDFieldTag.JOB_NUMBER, jobDispatchEventRequest.getJobNo());
    valuesMap.put(
        IVDFieldTag.EXTENDED_OFFER_DISPLAY_TIME,
        jobDispatchEventRequest.getExtendedOfferDisplayTime());
    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);
    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  private static void getPlatformFeeListItem(
      List<PlatformFeeItem> platformFeeItem, IVDMessageContent message) {
    if (platformFeeItem != null && !platformFeeItem.isEmpty()) {
      platformFeeItem.forEach(
          platformFeeItem1 -> {
            IVDListItem platformFee = new IVDListItem();
            if (Objects.nonNull(platformFeeItem1.getPlatformFeeApplicability())) {
              platformFee.putChar(
                  IVDFieldTag.ISAPPLICABLE,
                  PlatformFeeApplicabilityEnum.valueOf(
                          platformFeeItem1.getPlatformFeeApplicability().toUpperCase())
                      .getShortCode());
            }
            platformFee.putMoney(
                IVDFieldTag.FEE_ABOVE_THRESHOLD,
                DataConversion.toIVDMoneyType(platformFeeItem1.getFeeAboveThresholdLimit()));
            platformFee.putMoney(
                IVDFieldTag.FEE_BELOW_THRESHOLD,
                DataConversion.toIVDMoneyType(platformFeeItem1.getFeeBelowThresholdLimit()));
            platformFee.putMoney(
                IVDFieldTag.THRESHOLD_LIMIT,
                DataConversion.toIVDMoneyType(platformFeeItem1.getThresholdLimit()));
            message.putListItem(IVDFieldTag.PLATFORM_FEE_LIST, platformFee, false);
          });
    }
  }

  /**
   * Method to set byte size and array to GenericEventCommand for emergency close request
   *
   * @param bMsgToIVD byte array
   * @param emergencyClose verify otp values
   * @return GenericEventCommand
   */
  private GenericEventCommand convertEmergencyCloseRequest(
      byte[] bMsgToIVD, EmergencyClose emergencyClose) {

    byte[] header = Arrays.copyOf(bMsgToIVD, HEADER_ARRAY_LEN);

    IVDMessageContent message = new IVDMessageContent();

    VehCommUtils.fillTimestamp(header, System.currentTimeMillis(), TIMESTAMP_START_INDEX);

    Arrays.fill(header, ARRAY_FILL_INDEX_FROM, ARRAY_FILL_INDEX_TO, (byte) 0);

    Map<IVDFieldTag, Object> valuesMap = new EnumMap<>(IVDFieldTag.class);

    valuesMap.put(IVDFieldTag.EMERGENCY_ID, emergencyClose.getEmergId());

    valuesMap.put(IVDFieldTag.IP_ADDRESS, emergencyClose.getIpAddress());

    JobDispatchUtil.setMultipleItemsIntoIvdMessage(valuesMap, message);

    return getGenericEventCommand(message, header, bMsgToIVD);
  }

  /**
   * This method converts the MDTActionResponse[Logon,Logoff,IvdHardware,PowerUP] to bytes
   *
   * @param mdtApiResponse mdtApiResponse
   * @param mdtActionType mdtAction
   * @return MdtResponseCommand
   */
  @Override
  public MdtResponseCommand convertMDTResponseToBytes(
      MdtApiResponse mdtApiResponse, MdtAction mdtActionType, byte[] ipAddArr, int ivdNo) {
    IVDMessage response = generateHeaderForMDTResponseAction(mdtApiResponse, mdtActionType, ivdNo);
    return convertMDTResponse(response, mdtApiResponse, mdtActionType, ipAddArr);
  }

  /**
   * This Method generates the header for the MDTResponseAction[Logon,Logoff,IvdHardware,PowerUP]
   *
   * @param mdtapiResponse mdtapiResponse
   * @param mdtActionType mdtActionType
   * @return IVDMessage
   */
  private IVDMessage generateHeaderForMDTResponseAction(
      MdtApiResponse mdtapiResponse, MdtAction mdtActionType, int ivdNo) {
    switch (mdtActionType) {
      case LOGOUT_REQUEST -> {
        return VehCommUtils.createResponse(IVDMessageType.LOG_OFF_RESULT, ivdNo);
      }
      case LOGON_REQUEST -> {
        return VehCommUtils.createResponse(IVDMessageType.LOGON_RESULT, ivdNo);
      }
      case IVD_HARDWARE_INFO -> {
        return new IVDMessage();
      }
      case POWER_UP -> {
        IVDMessageType messageId;
        messageId =
            mdtapiResponse.getReasonCode() != null
                ? IVDMessageType.POWER_UP_FAILURE
                : IVDMessageType.POWER_UP_RESPONSE;
        return VehCommUtils.createResponse(messageId, ivdNo);
      }
      default -> throw new BadRequestException(ErrorCode.INVALID_MDT_ACTION.getCode());
    }
  }

  /**
   * Method to map the value and tag to custom map
   *
   * @param response -response
   * @param mdtapiResponse - api response
   * @param mdtActionType - action
   * @return MdtResponseCommand
   */
  private MdtResponseCommand convertMDTResponse(
      IVDMessage response,
      MdtApiResponse mdtapiResponse,
      MdtAction mdtActionType,
      byte[] ipAddArr) {

    byte[] responseBytes;
    switch (mdtActionType) {
      case LOGOUT_REQUEST -> {
        response.putText(IVDFieldTag.DRIVER_ID, mdtapiResponse.getDriverId());
        if (mdtapiResponse.getLogOffStatus() != null) {
          response.putBoolean(IVDFieldTag.LOG_OFF_STATUS, mdtapiResponse.getLogOffStatus());
        }
        response.putText(IVDFieldTag.VEHICLE_PLATE_NUMBER, mdtapiResponse.getVehiclePlateNum());
      }
      case LOGON_REQUEST -> {
        char logRequest = mdtapiResponse.getLogonStatus().charAt(0);
        response.putChar(IVDFieldTag.LOGON_STATUS, logRequest);
        response.putText(IVDFieldTag.DRIVER_NAME, mdtapiResponse.getDriverName());
        response.putText(IVDFieldTag.DRIVER_ID, mdtapiResponse.getDriverId());

        response.putText(IVDFieldTag.UNIQUE_DRIVER_ID, mdtapiResponse.getUniqueDriverId());

        if (StringUtils.isNotBlank(mdtapiResponse.getTimeLeftForSuspension())) {
          response.putTime(
              IVDFieldTag.TIME_LEFT_FOR_SUSPENSION,
              Integer.parseInt(mdtapiResponse.getTimeLeftForSuspension()));
        }

        String outRetainIVDStat = String.valueOf(mdtapiResponse.getRetainIvdStatus());
        response.putBoolean(
            IVDFieldTag.RETAIN_IVD_STATUS,
            (outRetainIVDStat != null && "1".equals(outRetainIVDStat.trim())));

        List<Product> productList = mdtapiResponse.getProductlist();
        if (!CollectionUtils.isEmpty(productList)) {
          productList.stream()
              .filter(p -> p.getIvdCode() != null)
              .forEach(
                  product -> {
                    IVDListItem item = new IVDListItem();
                    item.putInt16(IVDFieldTag.PRODUCT_CODE, product.getIvdCode().shortValue());
                    response.putListItem(IVDFieldTag.PRODUCT, item, false);
                  });
        }

        String outLoyaltyFlag = String.valueOf(mdtapiResponse.getLoyaltyStatus());
        response.putBoolean(
            IVDFieldTag.LOYALTY_STATUS,
            ((outLoyaltyFlag != null) && "1".equals(outLoyaltyFlag.trim())));

        setStarDriverValues(
            mdtapiResponse.getStarDriverGreeting(), mdtapiResponse.getStarDriver(), response);

        Integer cjSyncTimeSec = mdtapiResponse.getMdtBlockingSyncInterval();
        // Default 40 seconds
        response.putTime(
            IVDFieldTag.MDT_SYNC_BLOCKING_INTERVAL, Objects.requireNonNullElse(cjSyncTimeSec, 40));

        List<RooftopMessages> rooftopMessages = mdtapiResponse.getRooftopMessages();

        setRoofTopMessages(rooftopMessages, response);

        setValidatedLogonValues(response, mdtapiResponse);

        setJobBidOfferTimeOut(mdtapiResponse.getAutoBidJobOfferTimeOut(), response);

        Integer autoAcceptInterval = mdtapiResponse.getAutoAcceptDialogBoxTimeOut();

        setAutoAcceptIntervalVal(autoAcceptInterval, response);

        String zestAdminGSTEnabled = String.valueOf(mdtapiResponse.getAdminGstFlag());

        setAdminGstEnabled(zestAdminGSTEnabled, response);

        response.putText(IVDFieldTag.VEHICLE_PLATE_NUMBER, mdtapiResponse.getVehiclePlateNum());
        response.putText(IVDFieldTag.IP_ADDRESS, mdtapiResponse.getIpAddress());
        response.putText(IVDFieldTag.IMSI, mdtapiResponse.getImsi());
      }

      case IVD_HARDWARE_INFO -> {
        return getMdtResponseCommand(new byte[0], 0);
      }

      case POWER_UP -> {
        if (mdtapiResponse.getReasonCode() == null) {
          response.putText(IVDFieldTag.PAYMENT_MODULE_IP, mdtapiResponse.getPaymentModuleIp());

          setValidatedPowerUpValues(response, mdtapiResponse);

          response.putText(IVDFieldTag.VEHICLE_PLATE_NUMBER, mdtapiResponse.getVehiclePlateNum());
          response.putText(IVDFieldTag.IMSI, mdtapiResponse.getImsi());

          setJobNumberBlock(
              mdtapiResponse.getJobNumberBlockStart(),
              mdtapiResponse.getJobNumberBlockEnd(),
              response);
        } else {
          response.putByte(IVDFieldTag.REASON_CODE, Byte.parseByte(mdtapiResponse.getReasonCode()));
        }
      }

      default -> throw new BadRequestException(ErrorCode.INVALID_MDT_ACTION.getCode());
    }

    responseBytes = response.toHexBytes();

    int msgLength = responseBytes.length;
    if (msgLength > 0) {

      responseBytes = BytesUtil.joinBytes(responseBytes, ipAddArr);
    }
    // Prepends Response Bytes Length (UINT16) Used By Comms Server
    if (responseBytes.length > 0) {

      byte[] lenBytes = BytesUtil.convertToBytes(responseBytes.length, BytesSize.UINT16);
      BytesUtil.reverseBytes(lenBytes);
      responseBytes = BytesUtil.joinBytes(lenBytes, responseBytes);
    }

    return getMdtResponseCommand(responseBytes, msgLength);
  }

  @NotNull
  private MdtResponseCommand getMdtResponseCommand(byte[] responseBytes, int msgLength) {
    return MdtResponseCommand.builder()
        .byteArray(responseBytes)
        .byteArraySize(msgLength)
        .byteString(BytesUtil.toString(responseBytes))
        .eventDate(getInstantDate())
        .build();
  }

  private static void setValidatedPowerUpValues(
      IVDMessage response, MdtApiResponse mdtapiResponse) {
    if (mdtapiResponse.getVehicleTypeId() != null) {
      response.putByte(IVDFieldTag.VEHICLE_TYPE_ID, mdtapiResponse.getVehicleTypeId().byteValue());
    }

    if (mdtapiResponse.getPaymentModulePort() != null) {
      response.putUInt16(IVDFieldTag.PAYMENT_MODULE_PORT, mdtapiResponse.getPaymentModulePort());
    }

    if (StringUtils.isNotBlank(mdtapiResponse.getCompanyId())) {
      response.putByte(IVDFieldTag.COMPANY_ID, Byte.parseByte(mdtapiResponse.getCompanyId()));
    }
    if (mdtapiResponse.getSingJb() != null) {
      response.putBoolean(IVDFieldTag.SING_JB, mdtapiResponse.getSingJb());
    }

    if (mdtapiResponse.getEnableOtpVerification() != null) {
      response.putBoolean(
          IVDFieldTag.FORGOT_PWD_WITH_OTP, mdtapiResponse.getEnableOtpVerification());
    }
  }

  private static void setValidatedLogonValues(IVDMessage response, MdtApiResponse mdtapiResponse) {
    if (mdtapiResponse.getPendingOnCallJobStatus() != null) {
      response.putByte(
          IVDFieldTag.PENDING_ONCALL_JOB_STATUS,
          mdtapiResponse.getPendingOnCallJobStatus().byteValue());
    }

    if (mdtapiResponse.getTtsStatus() != null) {
      response.putBoolean(IVDFieldTag.TTS_STATUS, mdtapiResponse.getTtsStatus());
    }
    if (mdtapiResponse.getTaxiTourGuide() != null) {
      response.putBoolean(IVDFieldTag.TAXI_TOUR_GUIDE, mdtapiResponse.getTaxiTourGuide());
    }

    if (mdtapiResponse.getCjOfferTimeout() != null) {
      response.putTime(IVDFieldTag.CJ_OFFER_TIMEOUT, mdtapiResponse.getCjOfferTimeout());
    }
    if (mdtapiResponse.getAutoLogOffTimeout() != null) {
      response.putTime(
          IVDFieldTag.AUTO_LOGOFF_TIMEOUT, mdtapiResponse.getAutoLogOffTimeout().intValue());
    }

    if (mdtapiResponse.getMaxRejectCount() != null) {
      response.putInt16(
          IVDFieldTag.AUTO_REJECT_COUNT, mdtapiResponse.getMaxRejectCount().shortValue());
    }
    if (mdtapiResponse.getMaxMonitorDuration() != null) {
      response.putInt16(
          IVDFieldTag.DURATION_IN_SECS, mdtapiResponse.getMaxMonitorDuration().shortValue());
    }
    if (mdtapiResponse.getWarningThreshold() != null) {
      response.putInt16(
          IVDFieldTag.WARNING_COUNT, mdtapiResponse.getWarningThreshold().shortValue());
    }
    if (mdtapiResponse.getAutoStcDistance() != null) {
      response.putInt32(IVDFieldTag.AUTO_STC, mdtapiResponse.getAutoStcDistance());
    }
    if (mdtapiResponse.getAutoBidFlag() != null) {
      response.putByte(IVDFieldTag.AUTO_BID_FLAG, mdtapiResponse.getAutoBidFlag().byteValue());
    }
    if (mdtapiResponse.getDefaultAutoBidAccept() != null) {
      response.putByte(
          IVDFieldTag.AUTO_BID_DEFAULT_VALUE_FLAG,
          mdtapiResponse.getDefaultAutoBidAccept().byteValue());
    }

    if (mdtapiResponse.getForceChangePwd() != null) {
      response.putBoolean(IVDFieldTag.RESET_FLAG, mdtapiResponse.getForceChangePwd());
    }
    if (mdtapiResponse.getForgotPwdwithOtp() != null) {
      response.putBoolean(IVDFieldTag.FORGOT_PWD_WITH_OTP, mdtapiResponse.getForgotPwdwithOtp());
    }
  }

  private void setStarDriverValues(
      String starDriverGreeting, Boolean starDriver, IVDMessage response) {
    if (starDriver != null) {
      response.putBoolean(IVDFieldTag.STAR_DRIVER_FLAG, starDriver);
    }
    response.putText(
        IVDFieldTag.STAR_DRIVER_GREETING,
        Objects.requireNonNullElse(
            starDriverGreeting, "Good Day! You are travelling with" + " our Super Star driver"));
  }

  private void setJobBidOfferTimeOut(Integer autoBidJobOfferTimeOut, IVDMessage response) {
    if (autoBidJobOfferTimeOut != null) {

      response.putTime(IVDFieldTag.AUTO_BID_JOB_OFFER_TMOUT, autoBidJobOfferTimeOut);
    } else {

      log.warn("[setJobBidOfferTimeOut] No auto bid offer timeout setting, put default 5 secs");
      response.putTime(IVDFieldTag.AUTO_BID_JOB_OFFER_TMOUT, 5);
    }
  }

  private void setAutoAcceptIntervalVal(Integer autoAcceptInterval, IVDMessage response) {

    if (autoAcceptInterval != null) {
      response.putTime(IVDFieldTag.AUTO_ACCEPT_DIALOG_BOX_TIMEOUT, autoAcceptInterval);
    }
  }

  private void setJobNumberBlock(
      Long jobNumberBlockStart, Long jobNumberBlockEnd, IVDMessage response) {
    if (jobNumberBlockStart != null) {
      response.putInt64(IVDFieldTag.JOB_NUMBER_BLOCK_START, jobNumberBlockStart);
    }
    if (jobNumberBlockEnd != null) {
      response.putInt64(IVDFieldTag.JOB_NUMBER_BLOCK_END, jobNumberBlockEnd);
    }
  }

  private void setAdminGstEnabled(String zestAdminGSTEnabled, IVDMessage response) {

    if (zestAdminGSTEnabled != null) {

      response.putBoolean(
          IVDFieldTag.ADMIN_GST_FLAG,
          !StringUtils.isEmpty(zestAdminGSTEnabled) && zestAdminGSTEnabled.equalsIgnoreCase("Y"));
    }
  }

  private void setRoofTopMessages(List<RooftopMessages> rooftopMessages, IVDMessage response) {
    if (!CollectionUtils.isEmpty(rooftopMessages)) {
      rooftopMessages.forEach(
          msg -> {
            IVDListItem item = new IVDListItem();
            item.putText(IVDFieldTag.MESSAGE, msg.getMessage());
            item.putByte(IVDFieldTag.COLOUR, Byte.parseByte(msg.getColour()));
            item.putBoolean(IVDFieldTag.SCROLL, msg.getScroll());
            item.putByte(IVDFieldTag.SPEED, Byte.parseByte(msg.getSpeed()));
            item.putByte(IVDFieldTag.BITMAP_NUMBER, Byte.parseByte(msg.getBitmap()));
            response.putListItem(IVDFieldTag.ROOFTOP_MESSAGE, item, false);
          });
    }
  }

  public String getInstantDate() {
    ZonedDateTime instant = ZonedDateTime.now(ZoneId.of("UTC"));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    return formatter.format(instant);
  }
}
