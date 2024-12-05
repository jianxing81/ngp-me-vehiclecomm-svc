package integration.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.vehicleevent.VehicleEvent;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VehicleEventRequestUtility {
  public static VehicleEvent constructVehicleMessageEventStructureForMdt() {
    return VehicleEvent.builder()
        .withEventType("VehicleEvent")
        .withIvdNo(1234)
        .withDriverId("sh12")
        .withVehicleId("1234")
        .withDeviceType(VehicleEvent.DeviceType.MDT)
        .withEvent(VehicleEvent.Event.DISABLE_AUTO_BID)
        .build();
  }

  public static VehicleEvent constructVehicleMessageEventStructureForIphone() {
    return VehicleEvent.builder()
        .withEventType("VehicleEvent")
        .withIvdNo(1234)
        .withDriverId("sh12")
        .withVehicleId("1234")
        .withDeviceType(VehicleEvent.DeviceType.IPHONE)
        .withEvent(VehicleEvent.Event.DISABLE_AUTO_BID)
        .build();
  }

  public static VehicleEvent constructAppStatusSyncEventForAndroid() {
    return VehicleEvent.builder()
        .withEventType("VehicleEvent")
        .withIvdNo(1234)
        .withDriverId("1482254")
        .withStatus("BUSY")
        .withVehicleId("SHA7494L")
        .withDeviceType(VehicleEvent.DeviceType.ANDROID)
        .withEvent(VehicleEvent.Event.APP_STATUS_SYNC)
        .build();
  }
}
