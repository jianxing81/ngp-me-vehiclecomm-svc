package com.cdg.pmg.ngp.me.vehiclecomm.domain.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.constants.VehicleCommDomainConstant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** The VehicleComm Helper util class for domain requirements */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Helper {

  private static final DateTimeFormatter dateTimeFormatter =
      new DateTimeFormatterBuilder()
          .appendPattern(VehicleCommDomainConstant.DATE_FORMAT)
          .optionalStart()
          .appendFraction(ChronoField.NANO_OF_SECOND, 0, 3, true)
          .optionalEnd()
          .optionalStart()
          .appendLiteral('Z')
          .optionalEnd()
          .toFormatter();

  public static DateTimeFormatter buildDateTimeFormatter() {
    return dateTimeFormatter;
  }

  public static LocalDateTime getCurrentTime() {
    return LocalDateTime.parse(
        LocalDateTime.now(ZoneOffset.UTC).format(dateTimeFormatter), dateTimeFormatter);
  }

  public static LocalDateTime parseLocaleDate(String date) {
    return LocalDateTime.parse(date, dateTimeFormatter);
  }

  public static String formatLocaleDate(LocalDateTime date) {
    return dateTimeFormatter.format(date);
  }
}
