package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TagByteSize {
  BytesSize size() default BytesSize.BYTE;
}
