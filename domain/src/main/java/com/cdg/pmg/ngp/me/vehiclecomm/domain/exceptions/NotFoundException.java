package com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions;

import lombok.Getter;

/** The type NotFound exception. */
@Getter
public class NotFoundException extends DomainException {

  /**
   * Instantiates a new Internal server error.
   *
   * @param errorCode the error code
   */
  public NotFoundException(Long errorCode) {
    super(errorCode);
  }

  /**
   * Instantiates a new Internal server error.
   *
   * @param message the message
   * @param errorCode the error code
   */
  public NotFoundException(String message, Long errorCode) {
    super(message, errorCode);
  }

  /**
   * Instantiates a new Internal server error.
   *
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public NotFoundException(String message, Long errorCode, Object... arguments) {
    super(message, errorCode, arguments);
  }

  /**
   * Instantiates a new Internal server error.
   *
   * @param messageString the message string
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public NotFoundException(
      String messageString, String message, Long errorCode, Object... arguments) {
    super(messageString, message, errorCode, arguments);
  }
}
