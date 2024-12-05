package com.cdg.pmg.ngp.me.vehiclecomm.domain.service;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Rcsa;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.RcsaMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DriverAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.MessageTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RcsaEvents;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleMdtEventType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.FareTariff;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;

public interface VehicleCommDomainService {

  /**
   * This method is used to parse the geometric locations using the offset and multiplier from CMS
   * configuration
   *
   * @param esbJob esbJob
   */
  void parseGeoLocations(EsbJob esbJob);

  /**
   * This method is used to parse the fields related to money to a value object
   *
   * @param esbJob esbJob
   */
  void parseMoneyValues(EsbJob esbJob);

  /**
   * This method checks if the java bean corresponding to the byte data is valid or not
   *
   * @param esbJob esbJob
   */
  void validateByteData(EsbJob esbJob);

  /**
   * This method checks if ivd no is valid or not
   *
   * @param esbJob esbJob
   */
  void validateIvdNo(EsbJob esbJob);

  /**
   * This method checks if the vehicle details from MDT service is valid to use
   *
   * @param esbJob esbJob
   */
  void validateVehicleDetails(EsbJob esbJob);

  /**
   * This method checks if acknowledgement is required for the event
   *
   * @param esbJob esbJob
   * @return ackRequired
   */
  boolean isAckRequired(EsbJob esbJob);

  /**
   * This method is used to validate comfort protect values and adjust them
   *
   * @param esbJob esbJob
   */
  void validateComfortProtect(EsbJob esbJob);

  /**
   * This method is used to validate platform fee applicability
   *
   * @param esbJob esbJob
   */
  void validatePlatformFeeApplicability(EsbJob esbJob);

  /**
   * This method is used to validate required fields for voice streaming message
   *
   * @param rcsaEvent rcsaEvent
   */
  void validateIvdNo(Rcsa rcsaEvent);

  /**
   * This method checks if ivd no is valid or not
   *
   * @param esbVehicle esbVehicle
   */
  void validateIvdNo(EsbVehicle esbVehicle);

  /**
   * This method checks if acknowledgement is required for the event
   *
   * @param rcsaEvent esbJob
   * @return ackRequired
   */
  boolean isAckRequired(Rcsa rcsaEvent);

  /**
   * This method checks if acknowledgement is required for the event
   *
   * @param esbVehicle esbVehicle
   * @return ackRequired
   */
  boolean isAckRequired(EsbVehicle esbVehicle);

  /**
   * This method checks if the vehicle details from MDT service is valid to use
   *
   * @param rcsaEvent rcsaEvent
   */
  void validateVehicleDetails(Rcsa rcsaEvent);

  /**
   * This method checks if the vehicle details from MDT service is valid to use
   *
   * @param esbVehicle esbVehicle
   */
  void validateVehicleDetails(EsbVehicle esbVehicle);

  /**
   * This method checks if the zone info details from MDT service is valid to use
   *
   * @param esbVehicle esbVehicle
   */
  void validateZoneInfoDetails(EsbVehicle esbVehicle);

  /**
   * Setting rcsaEvent to aggregater root
   *
   * @param rcsa rcsa
   * @param rcsaEvents rcsaEvent
   */
  void setRcsaEventType(Rcsa rcsa, RcsaEvents rcsaEvents);

  /**
   * This method is used to set DriverAction
   *
   * @param esbJob esbJob
   * @param driverAction driverAction
   */
  void setDriverAction(EsbJob esbJob, DriverAction driverAction);

  /**
   * This method is used to find DriverAction For JobReject based on reasonCode
   *
   * @param esbJob esbJob
   * @param reasonCode reasonCode
   */
  DriverAction findDriverActionForJobReject(EsbJob esbJob, Integer reasonCode);

  /**
   * This method checks whether the mobile number is valid
   *
   * @param esbVehicle esbVehicle
   */
  void validateMobileNumber(EsbVehicle esbVehicle);

  void validateEmergencyId(Rcsa rcsaEvent);

  /**
   * This method is used to parse the geometric locations using the offset and multiplier from CMS
   * configuration
   *
   * @param rcsaEvent rcsaEvent
   */
  void parseGeoLocations(Rcsa rcsaEvent);

  /**
   * This method is used to parse the geometric locations using the offset and multiplier from CMS
   * configuration
   *
   * @param esbVehicle esbVehicle
   */
  void parseGeoLocations(EsbVehicle esbVehicle);

  /**
   * This method is used to set device type
   *
   * @param rcsaEvent rcsaEvent
   */
  void setDeviceType(Rcsa rcsaEvent, DeviceType deviceType);

  /**
   * This method is used to set device type
   *
   * @param esbVehicle esbVehicle
   */
  void setDeviceType(EsbVehicle esbVehicle, DeviceType deviceType);

  /**
   * This method is used to set vehicle event type
   *
   * @param rcsaEvent rcsaEvent
   * @param vehicleEventType vehicleEventType
   */
  void setVehicleEventType(Rcsa rcsaEvent, VehicleEvent vehicleEventType);

  /**
   * This method is used to set vehicle event type
   *
   * @param esbVehicle esbVehicle
   * @param vehicleEventType vehicleEventType
   */
  void setVehicleEventType(EsbVehicle esbVehicle, VehicleEvent vehicleEventType);

  /**
   * This method is used to set vehicle mdt event type
   *
   * @param esbVehicle esbVehicle
   * @param vehicleMdtEventType vehicleMdtEventType
   */
  void setVehicleMdtEventType(EsbVehicle esbVehicle, VehicleMdtEventType vehicleMdtEventType);

  /**
   * set value to Ivd Byte Array
   *
   * @param rcsaMessage rcsa message
   * @param genericEventCommand generic event command
   */
  void setIvdByteArray(RcsaMessage rcsaMessage, GenericEventCommand genericEventCommand);

  /**
   * Method to set value to Message type
   *
   * @param rcsaMessage rcsa message
   * @param messageTypeEnum messageTypeEnum
   */
  void setMessageType(RcsaMessage rcsaMessage, MessageTypeEnum messageTypeEnum);

  /** Method to set value to response message ID */
  void setResponseMessageId(EsbVehicle esbVehicle, int responseMessageId);

  /** Method used to validate cache key required fields */
  boolean validateCacheRequiredFields(EsbJob esbJob);

  /** Method to set value to response message ID */
  void setResponseMessageId(EsbJob esbJob, int responseMessageId);

  /** Method to check if fare calculation response event if required to be sent or not */
  boolean isFareCalculationResponseNotRequired(FareTariff fareTariff);
}
