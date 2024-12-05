package integration.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.driverevent.DriverEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.driverevent.VehicleSuspend;
import integration.constants.IntegrationTestConstants;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("unchecked")
public class DriverEventUtility {
  public static DriverEvent constructDriverEventAutoAcceptTrue() {
    return DriverEvent.builder()
        .withEventType(IntegrationTestConstants.DRIVER_EVENT)
        .withEventIdentifier(IntegrationTestConstants.DRIVER_SUSPEND)
        .withCommandType("4")
        .withCommandVariable("0")
        .withVehicleSuspends(List.of(DriverEventUtility.vehicleSuspendAutoAcceptTrue()))
        .build();
  }

  public static DriverEvent constructDriverEventAutoAcceptTrueCommandTypeFive() {
    return DriverEvent.builder()
        .withEventType(IntegrationTestConstants.DRIVER_EVENT)
        .withEventIdentifier(IntegrationTestConstants.DRIVER_SUSPEND)
        .withCommandType("5")
        .withCommandVariable("0")
        .withVehicleSuspends(List.of(DriverEventUtility.vehicleSuspendAutoAcceptTrue()))
        .build();
  }

  public static DriverEvent constructDriverEventAutoBidTrue() {
    return DriverEvent.builder()
        .withEventType(IntegrationTestConstants.DRIVER_EVENT)
        .withEventIdentifier(IntegrationTestConstants.DRIVER_SUSPEND)
        .withCommandType("5")
        .withCommandVariable("0")
        .withVehicleSuspends(List.of(DriverEventUtility.vehicleSuspendAutoBidTrue()))
        .build();
  }

  public static DriverEvent constructDriverEventAutoAcceptTrueIphone() {
    return DriverEvent.builder()
        .withEventType(IntegrationTestConstants.DRIVER_EVENT)
        .withEventIdentifier(IntegrationTestConstants.DRIVER_SUSPEND)
        .withCommandType("4")
        .withCommandVariable("0")
        .withVehicleSuspends(List.of(DriverEventUtility.vehicleSuspendAutoAcceptTrueIphone()))
        .build();
  }

  public static DriverEvent constructDriverEventAutoAcceptTrueIphoneCommandTypeFive() {
    return DriverEvent.builder()
        .withEventType(IntegrationTestConstants.DRIVER_EVENT)
        .withEventIdentifier(IntegrationTestConstants.DRIVER_SUSPEND)
        .withCommandType("5")
        .withCommandVariable("0")
        .withVehicleSuspends(List.of(DriverEventUtility.vehicleSuspendAutoAcceptTrueIphone()))
        .build();
  }

  public static DriverEvent constructDriverEventAutoBidTrueIphoneCommandTypeFive() {
    return DriverEvent.builder()
        .withEventType(IntegrationTestConstants.DRIVER_EVENT)
        .withEventIdentifier(IntegrationTestConstants.DRIVER_SUSPEND)
        .withCommandType("5")
        .withCommandVariable("0")
        .withVehicleSuspends(List.of(DriverEventUtility.vehicleSuspendAutoBidTrueIphone()))
        .build();
  }

  public static VehicleSuspend vehicleSuspendAutoAcceptTrueIphone() {
    return VehicleSuspend.builder()
        .withIvdNo(12345)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(VehicleSuspend.DeviceTypeEnum.IPHONE)
        .withSuspendTimeInMinutes("10")
        .withAutoBidFlag(false)
        .withAutoAcceptFlag(true)
        .build();
  }

  public static VehicleSuspend vehicleSuspendAutoBidTrueIphone() {
    return VehicleSuspend.builder()
        .withIvdNo(12345)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(VehicleSuspend.DeviceTypeEnum.IPHONE)
        .withSuspendTimeInMinutes("10")
        .withAutoBidFlag(true)
        .withAutoAcceptFlag(false)
        .build();
  }

  public static VehicleSuspend vehicleSuspendAutoAcceptTrue() {
    return VehicleSuspend.builder()
        .withIvdNo(12345)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(VehicleSuspend.DeviceTypeEnum.MDT)
        .withSuspendTimeInMinutes("10")
        .withAutoBidFlag(false)
        .withAutoAcceptFlag(true)
        .build();
  }

  public static VehicleSuspend vehicleSuspendAutoBidTrue() {
    return VehicleSuspend.builder()
        .withIvdNo(12345)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(VehicleSuspend.DeviceTypeEnum.MDT)
        .withSuspendTimeInMinutes("10")
        .withAutoBidFlag(true)
        .withAutoAcceptFlag(false)
        .build();
  }
}
