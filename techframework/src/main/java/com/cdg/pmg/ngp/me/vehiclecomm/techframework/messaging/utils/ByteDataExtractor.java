package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesSize;
import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.BooleanUtils;

@UtilityClass
public class ByteDataExtractor {

  private static final Map<Class<?>, Function<String, ?>> parsers =
      Map.of(
          Integer.class, Integer::valueOf,
          Short.class, Short::valueOf,
          Long.class, Long::valueOf,
          Double.class, Double::valueOf,
          Boolean.class, BooleanUtils::toBoolean,
          Byte.class, Byte::valueOf);

  public static <T> Map<String, T> extractMultipleFieldsFromByteArray(
      IVDMessage ivdMessage, List<IVDFieldTag> tagList, Class<T> type) {
    return tagList.stream()
        .map(
            tag ->
                new AbstractMap.SimpleEntry<>(
                    tag.getName(), extractFieldFromByteArray(ivdMessage, tag, type)))
        .filter(entry -> entry.getValue() != null)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public static <T> T extractFieldFromByteArray(
      IVDMessage ivdMessage, IVDFieldTag tag, Class<T> type) {
    BytesSize size = TlvTagsFactory.getTagByteSize(tag);
    Optional<Object> optionalData = Optional.ofNullable(ivdMessage.get(tag, size));
    if (optionalData.isEmpty()) {
      return null;
    }
    Object data = optionalData.get();
    if (type == Date.class) {
      if (data instanceof Date) {
        return type.cast(data);
      } else {
        long milliseconds = (Long) data;
        return type.cast(new Date(milliseconds));
      }
    }
    String value;
    if (size == BytesSize.BOOLEAN) {
      value = String.valueOf(((boolean) data) ? 1 : 0);
    } else {
      value = String.valueOf(data);
    }
    return convert(type, value);
  }

  private static <T> T convert(Class<T> type, String value) {
    return type.cast(parsers.getOrDefault(type, Function.identity()).apply(value));
  }
}
