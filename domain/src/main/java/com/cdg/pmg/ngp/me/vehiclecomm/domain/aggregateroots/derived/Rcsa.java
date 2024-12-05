package com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.AggregateRoot;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.constants.VehicleCommDomainConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.RcsaMessageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.VehicleDetails;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RcsaEvents;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.BadRequestException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.ByteData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.CmsConfiguration;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.Coordinate;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rcsa extends AggregateRoot<String> {

  private RcsaMessageRequest rcsaMessageRequest;
  private transient CmsConfiguration cmsConfiguration;
  private ByteData byteData;
  private VehicleDetails vehicleDetails;

  private RcsaEvents rcsaEventsType;
  private Coordinate vehicleLatitude;
  private Coordinate vehicleLongitude;
  private DeviceType deviceType;
  private VehicleEvent vehicleEventType;
  private String driverId;
  private Integer heading;

  public void validateIvdNo() {
    if (byteData.getIvdNo() == null)
      throw new BadRequestException(ErrorCode.IVD_NO_REQUIRED.getCode());
  }

  @Override
  public boolean equals(final Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  public boolean isAckRequired() {
    return checkIfAckRequired();
  }

  private boolean checkIfAckRequired() {
    return byteData.isAckRequired()
        || List.of(cmsConfiguration.getStoreForwardEvents().split(","))
            .contains(rcsaMessageRequest.getEventIdentifier());
  }

  public void validateVehicleDetails() {
    if (!isVehicleDetailsValid()) {
      throw new BadRequestException(ErrorCode.INVALID_VEHICLE_DETAILS.getCode());
    }
  }

  private boolean isVehicleDetailsValid() {
    return vehicleDetails != null && vehicleDetails.getId() != null;
  }

  public void setRcsaEventType(RcsaEvents rcsaEvents) {
    rcsaEventsType = rcsaEvents;
  }

  public void validateEmergencyId() {
    if (byteData != null && byteData.getEmergencyId() == null)
      throw new BadRequestException(ErrorCode.EMERGENCY_ID_REQUIRED.getCode());
  }

  public void parseGeoLocations() {
    setGeoLocations();
  }

  /** Method to set geo location values */
  private void setGeoLocations() {
    Double latitudeOrigin = cmsConfiguration.getLatitudeOrigin();
    Double longitudeOrigin = cmsConfiguration.getLongitudeOrigin();
    Long offsetMultiplier = cmsConfiguration.getOffsetMultiplier();
    if (offsetMultiplier == null || offsetMultiplier == VehicleCommDomainConstant.OFFSET_VALUE) {
      throw new BadRequestException(ErrorCode.INVALID_OFFSET_MULTIPLIER.getCode());
    }
    vehicleLatitude =
        adjustOffsetWithOriginAndMultiplier(
            byteData.getOffsetLongitude(), latitudeOrigin, offsetMultiplier);
    vehicleLongitude =
        adjustOffsetWithOriginAndMultiplier(
            byteData.getOffsetLatitude(), longitudeOrigin, offsetMultiplier);
    heading =
        Objects.isNull(byteData.getDirection())
            ? 0
            : (int) (byteData.getDirection() * VehicleCommDomainConstant.DIRECTION_MULTIPLIER);
  }

  /**
   * Method to adjust offset value
   *
   * @param coordinate coordinate value
   * @param origin origin value
   * @param offsetMultiplier offset
   * @return Coordinate
   */
  private Coordinate adjustOffsetWithOriginAndMultiplier(
      Double coordinate, double origin, long offsetMultiplier) {
    if (coordinate == null || offsetMultiplier == 0) {
      return null;
    }
    return new Coordinate(coordinate / offsetMultiplier + origin);
  }
}
