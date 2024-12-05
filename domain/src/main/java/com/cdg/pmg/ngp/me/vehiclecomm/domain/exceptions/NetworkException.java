package com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions;

public class NetworkException extends DomainException {
  /**
   * Instantiates a new Network error.
   *
   * @param errorCode the error code
   */
  public NetworkException(Long errorCode) {
    super(errorCode);
  }

  /**
   * Instantiates a new Network error.
   *
   * @param message the message
   * @param errorCode the error code
   */
  public NetworkException(String message, Long errorCode) {
    super(message, errorCode);
  }

  /**
   * Instantiates a new Network error.
   *
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public NetworkException(String message, Long errorCode, Object... arguments) {
    super(message, errorCode, arguments);
  }

  /**
   * Instantiates a new Network error.
   *
   * @param messageString the message string
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public NetworkException(
      String messageString, String message, Long errorCode, Object... arguments) {
    super(messageString, message, errorCode, arguments);
  }

  public NetworkException(Long errorCode, Object... arguments) {
    super(errorCode, arguments);
  }
}
