package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTypeEnum {
  COMMAND_TYPE_1("1"),
  COMMAND_TYPE_2("2"),
  COMMAND_TYPE_3("3"),
  COMMAND_TYPE_4("4"),
  COMMAND_TYPE_5("5"),
  COMMAND_TYPE_6("6"),
  SIMPLE("Simple"),
  STRUCTURE("Structure");

  private final String value;

  public static MessageTypeEnum fromString(String text) {
    return Arrays.stream(MessageTypeEnum.values())
        .filter(e -> e.getValue().equalsIgnoreCase(text))
        .findFirst()
        .orElse(null);
  }
}
