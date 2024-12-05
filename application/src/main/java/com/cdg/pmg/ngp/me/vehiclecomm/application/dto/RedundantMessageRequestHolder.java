package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

public class RedundantMessageRequestHolder {

  private RedundantMessageRequestHolder() {}

  private static final ThreadLocal<RedundantMessageKeyData> messageHolder = new ThreadLocal<>();

  public static void set(RedundantMessageKeyData redundantMessageKeyData) {
    messageHolder.set(redundantMessageKeyData);
  }

  public static RedundantMessageKeyData get() {
    return messageHolder.get();
  }

  public static void clear() {
    messageHolder.remove();
  }
}
