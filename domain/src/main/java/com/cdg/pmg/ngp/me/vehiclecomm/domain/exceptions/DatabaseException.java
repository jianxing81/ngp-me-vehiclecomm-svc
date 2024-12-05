package com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions;

public class DatabaseException extends DomainException {

  /**
   * Instantiates a new database error.
   *
   * @param errorCode the error code
   */
  public DatabaseException(Long errorCode) {
    super(errorCode);
  }

  /**
   * Instantiates a new database error.
   *
   * @param message the message
   * @param errorCode the error code
   */
  public DatabaseException(String message, Long errorCode) {
    super(message, errorCode);
  }

  /**
   * Instantiates a new database error.
   *
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public DatabaseException(String message, Long errorCode, Object... arguments) {
    super(message, errorCode, arguments);
  }

  /**
   * Instantiates a new database error.
   *
   * @param messageString the message string
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public DatabaseException(
      String messageString, String message, Long errorCode, Object... arguments) {
    super(messageString, message, errorCode, arguments);
  }

  public DatabaseException(Long errorCode, Object... arguments) {
    super(errorCode, arguments);
  }
}
