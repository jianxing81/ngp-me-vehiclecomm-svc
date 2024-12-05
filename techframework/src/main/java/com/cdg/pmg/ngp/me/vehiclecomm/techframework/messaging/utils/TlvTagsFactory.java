package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesSize;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.TagByteSize;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TlvTagsFactory {
  TlvTagsFactory() {}

  private static final ConcurrentMap<IVDFieldTag, BytesSize> tagByteSizeMap =
      new ConcurrentHashMap<>();

  /**
   * @param tag tag
   * @return BytesSize
   */
  public static BytesSize getTagByteSize(IVDFieldTag tag) {
    return tagByteSizeMap.get(tag);
  }

  static {
    TlvTagsFactory.init();
  }

  /** Generating tag byte size */
  public static void init() {
    IVDFieldTag[] allFields = IVDFieldTag.values();
    try {
      for (IVDFieldTag field : allFields) {
        TagByteSize byteSize =
            field.getClass().getField(field.name()).getAnnotation(TagByteSize.class);

        if (byteSize == null) continue;

        BytesSize size = byteSize.size();
        tagByteSizeMap.put(field, size);
      }
    } catch (Exception e) {
      log.error("Exception when generate tagByteSize", e);
    }
  }
}
