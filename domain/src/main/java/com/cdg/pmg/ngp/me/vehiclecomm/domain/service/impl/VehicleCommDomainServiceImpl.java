package com.cdg.pmg.ngp.me.vehiclecomm.domain.service.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Rcsa;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.RcsaMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DriverAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.MessageTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RcsaEvents;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleMdtEventType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.FareTariff;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;

@ServiceComponent
public class VehicleCommDomainServiceImpl implements VehicleCommDomainService {

  /**
   * This method is used to parse the geometric locations using the offset and multiplier from CMS
   * configuration
   *
   * @param esbJob esbJob
   */
  @Override
  public void parseGeoLocations(EsbJob esbJob) {
    esbJob.parseGeoLocations();
  }

  /**
   * This method is used to parse the fields related to money to a value object
   *
   * @param esbJob esbJob
   */
  @Override
  public void parseMoneyValues(EsbJob esbJob) {
    esbJob.parseMoney();
  }

  /**
   * This method checks if the java bean corresponding to the byte data is valid or not
   *
   * @param esbJob esbJob
   */
  @Override
  public void validateByteData(EsbJob esbJob) {
    esbJob.validateByteData();
  }

  /**
   * This method checks if ivd no is valid or not
   *
   * @param esbJob esbJob
   */
  @Override
  public void validateIvdNo(EsbJob esbJob) {
    esbJob.validateIvdNo();
  }

  /**
   * This method checks if the vehicle details from MDT service is valid to use
   *
   * @param esbJob esbJob
   */
  @Override
  public void validateVehicleDetails(EsbJob esbJob) {
    esbJob.validateVehicleDetails();
  }

  /**
   * This method checks if acknowledgement is required for the event
   *
   * @param esbJob esbJob
   * @return ackRequired
   */
  @Override
  public boolean isAckRequired(EsbJob esbJob) {
    return esbJob.isAckRequired();
  }

  /**
   * This method is used to validate comfort protect values and adjust them
   *
   * @param esbJob esbJob
   */
  @Override
  public void validateComfortProtect(EsbJob esbJob) {
    esbJob.validateComfortProtect();
  }

  /**
   * This method is used to validate platform fee applicability
   *
   * @param esbJob esbJob
   */
  @Override
  public void validatePlatformFeeApplicability(EsbJob esbJob) {
    esbJob.validatePlatformFeeApplicability();
  }

  /**
   * This method is used to validate required fields for voice streaming message
   *
   * @param rcsaEvent rcsaEvent
   */
  @Override
  public void validateIvdNo(Rcsa rcsaEvent) {
    rcsaEvent.validateIvdNo();
  }

  /**
   * This method checks if ivd no is valid or not
   *
   * @param esbVehicle esbVehicle
   */
  @Override
  public void validateIvdNo(EsbVehicle esbVehicle) {
    esbVehicle.validateIvdNo();
  }

  /**
   * This method checks if acknowledgement is required for the event
   *
   * @param rcsaEvent rcsaEvent
   * @return ackRequired
   */
  @Override
  public boolean isAckRequired(Rcsa rcsaEvent) {
    return rcsaEvent.isAckRequired();
  }

  /**
   * This method checks if acknowledgement is required for the event
   *
   * @param esbVehicle esbVehicle
   * @return ackRequired
   */
  @Override
  public boolean isAckRequired(EsbVehicle esbVehicle) {
    return esbVehicle.isAckRequired();
  }

  /**
   * This method checks if the vehicle details from MDT service is valid to use
   *
   * @param rcsaEvent rcsaEvent
   */
  @Override
  public void validateVehicleDetails(Rcsa rcsaEvent) {
    rcsaEvent.validateVehicleDetails();
  }

  /**
   * This method checks if the vehicle details from MDT service is valid to use
   *
   * @param esbVehicle esbVehicle
   */
  @Override
  public void validateVehicleDetails(EsbVehicle esbVehicle) {
    esbVehicle.validateVehicleDetails();
  }

  /**
   * This method checks if the zone info details from MDT service is valid to use
   *
   * @param esbVehicle esbVehicle
   */
  @Override
  public void validateZoneInfoDetails(EsbVehicle esbVehicle) {
    esbVehicle.validateZoneInfoDetails();
  }

  /**
   * setting rcsa event for the purpose of producing to kafka topic
   *
   * @param rcsa rcsa
   * @param rcsaEvents rcsaEvent
   */
  @Override
  public void setRcsaEventType(Rcsa rcsa, RcsaEvents rcsaEvents) {
    rcsa.setRcsaEventType(rcsaEvents);
  }

  /**
   * This method is used to set DriverAction
   *
   * @param esbJob esbJob
   * @param driverAction driverAction
   */
  @Override
  public void setDriverAction(EsbJob esbJob, DriverAction driverAction) {
    esbJob.setDriverAction(driverAction);
  }

  /**
   * This method is used to find DriverAction For JobReject based on reasonCode
   *
   * @param esbJob esbJob
   * @param reasonCode reasonCode
   */
  @Override
  public DriverAction findDriverActionForJobReject(EsbJob esbJob, Integer reasonCode) {
    return esbJob.findDriverActionForJobReject(reasonCode);
  }

  @Override
  public void validateMobileNumber(EsbVehicle esbVehicle) {
    esbVehicle.validateMobileNumber();
  }

  @Override
  public void validateEmergencyId(Rcsa rcsaEvent) {
    rcsaEvent.validateEmergencyId();
  }

  /**
   * This method is used to parse the geometric locations using the offset and multiplier from CMS
   * configuration
   *
   * @param rcsaEvent rcsaEvent
   */
  @Override
  public void parseGeoLocations(Rcsa rcsaEvent) {
    rcsaEvent.parseGeoLocations();
  }

  /**
   * This method is used to parse the geometric locations using the offset and multiplier from CMS
   * configuration
   *
   * @param esbVehicle esbVehicle
   */
  @Override
  public void parseGeoLocations(EsbVehicle esbVehicle) {
    esbVehicle.parseGeoLocations();
  }

  /**
   * This method is used to set device type
   *
   * @param rcsaEvent rcsaEvent
   * @param deviceType device type
   */
  @Override
  public void setDeviceType(Rcsa rcsaEvent, DeviceType deviceType) {
    rcsaEvent.setDeviceType(deviceType);
  }

  /**
   * This method is used to set device type
   *
   * @param esbVehicle esbVehicle
   * @param deviceType device type
   */
  @Override
  public void setDeviceType(EsbVehicle esbVehicle, DeviceType deviceType) {
    esbVehicle.setDeviceType(deviceType);
  }

  /**
   * This method is used to set vehicle event type
   *
   * @param rcsaEvent rcsaEvent
   * @param vehicleEventType vehicleEventType
   */
  @Override
  public void setVehicleEventType(Rcsa rcsaEvent, VehicleEvent vehicleEventType) {
    rcsaEvent.setVehicleEventType(vehicleEventType);
  }

  /**
   * This method is used to set vehicle event type
   *
   * @param esbVehicle esbVehicle
   * @param vehicleEventType vehicleEventType
   */
  @Override
  public void setVehicleEventType(EsbVehicle esbVehicle, VehicleEvent vehicleEventType) {
    esbVehicle.setVehicleEventType(vehicleEventType);
  }

  /**
   * This method is used to set vehicle mdt event type
   *
   * @param esbVehicle esbVehicle
   * @param vehicleMdtEventType vehicleMdtEventType
   */
  @Override
  public void setVehicleMdtEventType(
      EsbVehicle esbVehicle, VehicleMdtEventType vehicleMdtEventType) {
    esbVehicle.setVehicleMdtEventType(vehicleMdtEventType);
  }

  @Override
  public void setIvdByteArray(RcsaMessage rcsaMessage, GenericEventCommand genericEventCommand) {
    rcsaMessage.setGenericEventCommand(genericEventCommand);
  }

  @Override
  public void setMessageType(RcsaMessage rcsaMessage, MessageTypeEnum messageTypeEnum) {
    rcsaMessage.setMessageTypeEnum(messageTypeEnum);
  }

  /**
   * Method to set value to response message ID
   *
   * @param esbVehicle esbVehicle
   * @param responseMessageId responseMessageId
   */
  @Override
  public void setResponseMessageId(EsbVehicle esbVehicle, int responseMessageId) {
    esbVehicle.setResponseMessageId(responseMessageId);
  }

  /**
   * Method is used to check cache key required parameter availability
   *
   * @param esbJob -esbJob
   * @return true/false
   */
  @Override
  public boolean validateCacheRequiredFields(EsbJob esbJob) {
    return esbJob.validateCacheRequiredFields();
  }

  /**
   * Method to set value to response message ID
   *
   * @param esbJob esbJob
   * @param responseMessageId responseMessageId
   */
  @Override
  public void setResponseMessageId(EsbJob esbJob, int responseMessageId) {
    esbJob.setResponseMessageId(responseMessageId);
  }

  /**
   * Method to check if fare calculation response event if required to be sent or not
   *
   * @param fareTariff fareTariff
   */
  @Override
  public boolean isFareCalculationResponseNotRequired(FareTariff fareTariff) {
    return null == fareTariff
        || null == fareTariff.getData()
        || fareTariff.getData().getBookingFee().equals(0D);
  }
}
