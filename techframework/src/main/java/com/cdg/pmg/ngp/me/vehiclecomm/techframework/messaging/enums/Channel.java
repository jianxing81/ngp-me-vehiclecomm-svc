package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.VehicleCommFrameworkConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Channel {
  IPHONE("IPHONE", "APP", 6),
  ANDROID("ANDROID", "APP", 4),
  WINDOW("WINDOW", "APP", 2),
  MOBILE("MOBILE", "APP", 2),
  NOKIA("NOKIA", "APP", 3),
  AIC("AIC", "APP", 99),
  BB("BB", "APP", 5),
  CSA("CSA", "CSA", 9),
  SMS("SMS", "SMS", 7),
  IVR("IVR", "IVR", 12),
  IRD("IRD", "IVR", 12),
  IRD_POSTAL("IRD Postal", "IVR", 8),
  WEB("WEB", "WEB", 1),
  OPENAPI(VehicleCommFrameworkConstants.OPENAPI, VehicleCommFrameworkConstants.OPENAPI, 99),
  HANDY("HANDY", "HANDY", 99),
  MWF("MWF", VehicleCommFrameworkConstants.OPENAPI, 99),
  DC("DC", VehicleCommFrameworkConstants.OPENAPI, 10),
  NFC("NFC", VehicleCommFrameworkConstants.OPENAPI, 11),
  H5ALIPAY("H5ALIPAY", "H5", 13),
  H5("H5", "H5", 14),
  H5DBSPAYLAH("H5DBSPAYLAH", "H5", 15),
  H5VOUCH("H5VOUCH", "H5", 16),
  H5SINGTELDASH("H5SINGTELDASH", "H5", 17),
  H5LAZADA("H5LAZADA", "H5", 18),
  ZEST("ZEST", VehicleCommFrameworkConstants.OPENAPI, 19),
  H5KRISPLUS("H5KRISPLUS", "H5", 20),
  DRIVERAPP("Driver App", "App", 21),
  GOJEK("GOJEK", VehicleCommFrameworkConstants.OPENAPI, 22),
  IVR_P("IVR_P", VehicleCommFrameworkConstants.IVR_P, 23),
  IRD_P("IRD_P", VehicleCommFrameworkConstants.IVR_P, 24),
  IRD_POSTAL_P("IRD_POSTAL_P", VehicleCommFrameworkConstants.IVR_P, 25);

  private final String value;
  private final String channelCategory;
  private final int channelId;

  public static Channel getType(String str) {
    if (str != null) {
      for (Channel t : Channel.values()) {
        if (str.equals(t.getValue())) {
          return t;
        }
      }
    }
    throw new IllegalArgumentException();
  }
}
