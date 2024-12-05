package com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.AggregateRoot;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.constants.VehicleCommDomainConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.IvdVehicleEventMessageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.VehicleDetails;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.ZoneInfoDetails;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleMdtEventType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.BadRequestException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.ByteArrayData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.ByteData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.CmsConfiguration;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.Coordinate;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EsbVehicle extends AggregateRoot<String> {
  private IvdVehicleEventMessageRequest ivdVehicleEventMessageRequest;
  private transient CmsConfiguration cmsConfiguration;
  private VehicleDetails vehicleDetails;
  private ZoneInfoDetails zoneInfoDetails;
  private ByteData byteData;
  private Coordinate vehicleLatitude;
  private Coordinate vehicleLongitude;
  private DeviceType deviceType;
  private VehicleEvent vehicleEventType;
  private ByteArrayData byteArrayData;
  private VehicleMdtEventType vehicleMdtEventType;
  private Integer responseMessageId;
  private Integer heading;

  public void validateIvdNo() {
    if (byteData.getIvdNo() == null)
      throw new BadRequestException(ErrorCode.IVD_NO_REQUIRED.getCode());
  }

  public boolean isAckRequired() {
    return checkIfAckRequired();
  }

  private boolean checkIfAckRequired() {
    return byteData.isAckRequired()
        || List.of(cmsConfiguration.getStoreForwardEvents().split(","))
            .contains(ivdVehicleEventMessageRequest.getEventIdentifier());
  }

  public void parseGeoLocations() {
    setGeoLocations();
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

  /** Method to set geolocation values */
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

  public void validateVehicleDetails() {
    if (!isVehicleDetailsValid()) {
      throw new BadRequestException(ErrorCode.INVALID_VEHICLE_DETAILS.getCode());
    }
  }

  public void validateZoneInfoDetails() {
    if (!isZoneInfoDetailsValid()) {
      throw new BadRequestException(ErrorCode.INVALID_ZONE_INFO_DETAILS.getCode());
    }
  }

  private boolean isVehicleDetailsValid() {
    return vehicleDetails != null && vehicleDetails.getId() != null;
  }

  private boolean isZoneInfoDetailsValid() {
    return zoneInfoDetails != null && zoneInfoDetails.getId() != null;
  }

  public void validateMobileNumber() {
    if (!isMobileNumberValid()) {
      throw new BadRequestException(ErrorCode.MOBILE_NO_REQUIRED.getCode());
    }
  }

  private boolean isMobileNumberValid() {
    return byteData.getMobileNumber() != null;
  }

  @Override
  public boolean equals(final Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
