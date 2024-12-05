package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.validator.CustomEnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Custom annotation class for enum validator */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CustomEnumValidator.class)
public @interface CustomEnum {
  /**
   * Message string.
   *
   * @return the string
   */
  String message() default "{javax.validation.constraints.NotNull.message}";

  /**
   * Status code int.
   *
   * @return the int
   */
  long errorCode() default 1L;

  /**
   * Groups class [ ].
   *
   * @return the class [ ]
   */
  Class<?>[] groups() default {};

  /**
   * Payload class [ ].
   *
   * @return the class [ ]
   */
  Class<? extends Payload>[] payload() default {};

  /**
   * Enum class [ ].
   *
   * @return the class []
   */
  Class<? extends Enum> enumClass();

  /** The interface List. */
  @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
  @Retention(RUNTIME)
  @Documented
  @interface List {
    /**
     * Value custom enum [ ].
     *
     * @return the custom enum [ ]
     */
    CustomEnum[] value();
  }
}
