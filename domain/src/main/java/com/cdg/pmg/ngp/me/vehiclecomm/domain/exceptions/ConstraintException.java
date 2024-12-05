package com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions;

import lombok.Getter;

/** The type Constraint Exception. */
@Getter
public class ConstraintException extends DomainException {

  /**
   * Instantiates a new constraint error.
   *
   * @param message the message
   * @param errorCode the error code
   */
  public ConstraintException(String message, Long errorCode) {
    super(message, errorCode);
  }

  /**
   * Instantiates a new constraint error.
   *
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public ConstraintException(String message, Long errorCode, Object... arguments) {
    super(message, errorCode, arguments);
  }

  /**
   * Instantiates a new constraint error.
   *
   * @param messageString the message string
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public ConstraintException(
      String messageString, String message, Long errorCode, Object... arguments) {
    super(messageString, message, errorCode, arguments);
  }

  public ConstraintException(
      Throwable e, String messageString, String message, Long errorCode, Object... arguments) {
    super(e, messageString, message, errorCode, arguments);
  }

  public ConstraintException(Throwable e) {
    super(e);
  }

  public ConstraintException(Long errorCode, Object... arguments) {
    super(errorCode, arguments);
  }
}
