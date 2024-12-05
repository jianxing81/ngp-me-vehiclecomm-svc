package com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions;

/** The type IvdConversion exception. */
public class IvdConversionException extends DomainException {

  public IvdConversionException(Long errorCode) {
    super(errorCode);
  }

  /**
   * Instantiates a new IvdConversion error.
   *
   * @param message the message
   * @param errorCode the error code
   */
  public IvdConversionException(String message, Long errorCode) {
    super(message, errorCode);
  }

  /**
   * Instantiates a new IvdConversion error.
   *
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public IvdConversionException(String message, Long errorCode, Object... arguments) {
    super(message, errorCode, arguments);
  }

  /**
   * Instantiates a new IvdConversion error.
   *
   * @param messageString the message string
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public IvdConversionException(
      String messageString, String message, Long errorCode, Object... arguments) {
    super(messageString, message, errorCode, arguments);
  }

  public IvdConversionException(Long errorCode, Object... arguments) {
    super(errorCode, arguments);
  }
}
