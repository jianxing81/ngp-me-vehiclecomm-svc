package integration.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.rcsamessageevent.RcsaMessageEvent;
import integration.constants.IntegrationTestConstants;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("unchecked")
public class RcsaMessageEventUtility {

  public static RcsaMessageEvent constructRcsaMessageEventStructure() {
    return RcsaMessageEvent.builder()
        .withEventType(IntegrationTestConstants.RCSA_MESSAGE_EVENT)
        .withIvdNo(1234)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(RcsaMessageEvent.DeviceType.MDT)
        .withIpAddr("2asd")
        .withMsgId(12)
        .withMessageSerialNo("1234")
        .withRequestServiceType(IntegrationTestConstants.REQ_SERVICE_TYPE)
        .withCanMessageId(IntegrationTestConstants.CANCEL_MESSAGE_ID)
        .withCommandVariable("nga12")
        .withMsgContent("adad")
        .withMessageType("Structure")
        .build();
  }

  public static RcsaMessageEvent constructRcsaMessageEventSimple() {
    return RcsaMessageEvent.builder()
        .withEventType(IntegrationTestConstants.RCSA_MESSAGE_EVENT)
        .withIvdNo(1234)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(RcsaMessageEvent.DeviceType.MDT)
        .withIpAddr("2asd")
        .withMsgId(12)
        .withMessageSerialNo("1234")
        .withRequestServiceType(IntegrationTestConstants.REQ_SERVICE_TYPE)
        .withCanMessageId(IntegrationTestConstants.CANCEL_MESSAGE_ID)
        .withCommandVariable("nga12")
        .withMsgContent("adad")
        .withMessageType("Simple")
        .build();
  }

  public static RcsaMessageEvent constructRcsaMessageEventForOne() {
    return RcsaMessageEvent.builder()
        .withEventType(IntegrationTestConstants.RCSA_MESSAGE_EVENT)
        .withIvdNo(1234)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(RcsaMessageEvent.DeviceType.MDT)
        .withIpAddr("2asd")
        .withMsgId(12)
        .withMessageSerialNo("1234")
        .withRequestServiceType(IntegrationTestConstants.REQ_SERVICE_TYPE)
        .withCanMessageId(IntegrationTestConstants.CANCEL_MESSAGE_ID)
        .withCommandVariable("1")
        .withMsgContent("This is test message")
        .withMessageType("1")
        .build();
  }

  public static RcsaMessageEvent constructRcsaMessageEvent() {
    return RcsaMessageEvent.builder()
        .withEventType(IntegrationTestConstants.RCSA_MESSAGE_EVENT)
        .withIvdNo(1234)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(RcsaMessageEvent.DeviceType.MDT)
        .withIpAddr("2asd")
        .withMsgId(12)
        .withMessageSerialNo("1234")
        .withRequestServiceType(IntegrationTestConstants.REQ_SERVICE_TYPE)
        .withCanMessageId(IntegrationTestConstants.CANCEL_MESSAGE_ID)
        .withCommandVariable("1")
        .withMsgContent("adad")
        .withMessageType("2")
        .build();
  }

  public static RcsaMessageEvent constructRcsaMessageEventForThree() {
    return RcsaMessageEvent.builder()
        .withEventType(IntegrationTestConstants.RCSA_MESSAGE_EVENT)
        .withIvdNo(1234)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(RcsaMessageEvent.DeviceType.MDT)
        .withIpAddr("2asd")
        .withMsgId(12)
        .withMessageSerialNo("1234")
        .withRequestServiceType(IntegrationTestConstants.REQ_SERVICE_TYPE)
        .withCanMessageId(IntegrationTestConstants.CANCEL_MESSAGE_ID)
        .withCommandVariable("1")
        .withMsgContent("adad")
        .withMessageType("3")
        .build();
  }

  public static RcsaMessageEvent constructRcsaMessageEventForFour() {
    return RcsaMessageEvent.builder()
        .withEventType(IntegrationTestConstants.RCSA_MESSAGE_EVENT)
        .withIvdNo(1234)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(RcsaMessageEvent.DeviceType.MDT)
        .withIpAddr("2asd")
        .withMsgId(12)
        .withMessageSerialNo("1234")
        .withRequestServiceType(IntegrationTestConstants.REQ_SERVICE_TYPE)
        .withCanMessageId(IntegrationTestConstants.CANCEL_MESSAGE_ID)
        .withCommandVariable("1")
        .withMsgContent("adad")
        .withMessageType("4")
        .build();
  }

  public static RcsaMessageEvent constructRcsaMessageEventForFive() {
    return RcsaMessageEvent.builder()
        .withEventType(IntegrationTestConstants.RCSA_MESSAGE_EVENT)
        .withIvdNo(1234)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(RcsaMessageEvent.DeviceType.MDT)
        .withIpAddr("2asd")
        .withMsgId(12)
        .withMessageSerialNo("1234")
        .withRequestServiceType(IntegrationTestConstants.REQ_SERVICE_TYPE)
        .withCanMessageId(IntegrationTestConstants.CANCEL_MESSAGE_ID)
        .withCommandVariable("1")
        .withMsgContent("adad")
        .withMessageType("5")
        .build();
  }

  public static RcsaMessageEvent constructRcsaMessageEventForSix() {
    return RcsaMessageEvent.builder()
        .withEventType(IntegrationTestConstants.RCSA_MESSAGE_EVENT)
        .withIvdNo(1234)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(RcsaMessageEvent.DeviceType.MDT)
        .withIpAddr("12.17.0.1")
        .withMsgId(12)
        .withMessageSerialNo("1234")
        .withRequestServiceType(IntegrationTestConstants.REQ_SERVICE_TYPE)
        .withCanMessageId(IntegrationTestConstants.CANCEL_MESSAGE_ID)
        .withCommandVariable("1")
        .withMsgContent("Test Message")
        .withMessageType("6")
        .build();
  }

  public static RcsaMessageEvent constructRcsaMessageEventForDeviceType() {
    return RcsaMessageEvent.builder()
        .withEventType(IntegrationTestConstants.RCSA_MESSAGE_EVENT)
        .withIvdNo(1234)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(RcsaMessageEvent.DeviceType.MDT)
        .withIpAddr("2asd")
        .withMsgId(12)
        .withMessageSerialNo("1234")
        .withRequestServiceType(IntegrationTestConstants.REQ_SERVICE_TYPE)
        .withCanMessageId(IntegrationTestConstants.CANCEL_MESSAGE_ID)
        .withCommandVariable("1")
        .withMsgContent("adad")
        .withMessageType("")
        .build();
  }

  public static RcsaMessageEvent constructRcsaMessageEventErrorOut() {
    return RcsaMessageEvent.builder()
        .withEventType(IntegrationTestConstants.RCSA_MESSAGE_EVENT)
        .withIvdNo(1234)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(RcsaMessageEvent.DeviceType.IPHONE)
        .withIpAddr("2asd")
        .withMsgId(12)
        .withMessageSerialNo("1234")
        .withRequestServiceType(IntegrationTestConstants.REQ_SERVICE_TYPE)
        .withCanMessageId(IntegrationTestConstants.CANCEL_MESSAGE_ID)
        .withCommandVariable("1")
        .withMsgContent("adad")
        .withMessageType("")
        .build();
  }

  public static RcsaMessageEvent constructRcsaMessageEventErrorOutInvalidMessageType() {
    return RcsaMessageEvent.builder()
        .withEventType(IntegrationTestConstants.RCSA_MESSAGE_EVENT)
        .withIvdNo(1234)
        .withDriverId(IntegrationTestConstants.TEST_DRIVER_ID)
        .withVehicleId(IntegrationTestConstants.TEST_VEHICLE_ID)
        .withDeviceType(RcsaMessageEvent.DeviceType.IPHONE)
        .withIpAddr("2asd")
        .withMsgId(12)
        .withMessageSerialNo("1234")
        .withRequestServiceType(IntegrationTestConstants.REQ_SERVICE_TYPE)
        .withCanMessageId(IntegrationTestConstants.CANCEL_MESSAGE_ID)
        .withCommandVariable("1")
        .withMsgContent("adad")
        .withMessageType("ab")
        .build();
  }
}
