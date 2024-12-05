package com.cdg.pmg.ngp.me.vehiclecomm.domain.validator;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.CustomEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

/** To check whether the value matches in enum list */
public class CustomEnumValidator implements ConstraintValidator<CustomEnum, Object> {
  private Class<? extends Enum> enumClass;

  /**
   * Initialize the annotation instance
   *
   * @param constraintAnnotation annotation instance for a given constraint declaration
   */
  @Override
  public void initialize(CustomEnum constraintAnnotation) {
    this.enumClass = constraintAnnotation.enumClass();
  }

  /**
   * To validate the constraint with object value
   *
   * @param value object to validate
   * @param context context in which the constraint is evaluated
   * @return boolean
   */
  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    Enum<?>[] enumConstants = enumClass.getEnumConstants();
    if (null == value) return true;
    return Arrays.stream(enumConstants)
        .map(Enum::name)
        .anyMatch(enumName -> enumName.equals(value.toString()));
  }
}
