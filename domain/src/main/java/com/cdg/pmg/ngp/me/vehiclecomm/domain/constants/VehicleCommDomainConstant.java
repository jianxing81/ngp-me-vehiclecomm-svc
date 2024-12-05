package com.cdg.pmg.ngp.me.vehiclecomm.domain.constants;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** The VehicleCommDomainConstant constant. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VehicleCommDomainConstant {
  public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
  public static final String PATTERN = ".+\\[\"(.+)\"\\].+";
  public static final String BOOLEAN_TRUE_AS_BINARY = "1";
  public static final List<String> PLATFORM_FEE_APPLICABILITY = List.of("Y", "N", "W");
  public static final long OFFSET_VALUE = 0L;
  public static final String HDB = "HDB";
  public static final String BLK = "Blk ";
  public static final Double DIRECTION_MULTIPLIER = 11.25;
}
