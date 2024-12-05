package com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions;

import lombok.Getter;

/** The type BadRequest exception. */
@Getter
public class BadRequestException extends DomainException {

  public BadRequestException(Long errorCode) {
    super(errorCode);
  }

  /**
   * Instantiates a new BadRequest error.
   *
   * @param message the message
   * @param errorCode the error code
   */
  public BadRequestException(String message, Long errorCode) {
    super(message, errorCode);
  }

  /**
   * Instantiates a new BadRequest error.
   *
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public BadRequestException(String message, Long errorCode, Object... arguments) {
    super(message, errorCode, arguments);
  }

  /**
   * Instantiates a new BadRequest error.
   *
   * @param messageString the message string
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public BadRequestException(
      String messageString, String message, Long errorCode, Object... arguments) {
    super(messageString, message, errorCode, arguments);
  }

  public BadRequestException(Long errorCode, Object... arguments) {
    super(errorCode, arguments);
  }
}
