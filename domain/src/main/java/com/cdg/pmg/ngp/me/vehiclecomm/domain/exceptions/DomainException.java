package com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions;

import java.io.Serial;
import lombok.Getter;

/** Domain Exception class */
@Getter
public class DomainException extends RuntimeException {

  @Serial private static final long serialVersionUID = 8548092679936686059L;
  private static final String DOMAIN_EXCEPTION_MESSAGE = "Domain exception";
  private final transient String message;
  private final transient Long errorCode;
  private final transient Object[] arguments;
  private final Throwable throwable;

  /**
   * Instantiates a new Domain exception.
   *
   * @param message the message
   * @param errorCode the error code
   */
  public DomainException(final String message, final Long errorCode) {
    super(DOMAIN_EXCEPTION_MESSAGE);
    this.message = message;
    this.arguments = new Object[0];
    this.errorCode = errorCode;
    this.throwable = null;
  }

  /**
   * Instantiates a new Domain exception.
   *
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public DomainException(final String message, final Long errorCode, final Object... arguments) {
    super(DOMAIN_EXCEPTION_MESSAGE);
    this.message = message;
    this.arguments = arguments;
    this.errorCode = errorCode;
    this.throwable = null;
  }

  /**
   * Instantiates a new Domain exception.
   *
   * @param messageString the message string
   * @param message the message
   * @param errorCode the error code
   * @param arguments the arguments
   */
  public DomainException(
      final String messageString,
      final String message,
      final Long errorCode,
      final Object... arguments) {
    super(messageString);
    this.message = message;
    this.arguments = arguments;
    this.errorCode = errorCode;
    this.throwable = null;
  }

  public DomainException(
      Throwable throwable,
      final String messageString,
      final String message,
      final Long errorCode,
      final Object... arguments) {
    super(messageString);
    this.throwable = throwable;
    this.message = message;
    this.arguments = arguments;
    this.errorCode = errorCode;
  }

  public DomainException(Throwable throwable) {
    super(DOMAIN_EXCEPTION_MESSAGE);
    this.throwable = throwable;
    this.arguments = new Object[0];
    this.errorCode = null;
    this.message = DOMAIN_EXCEPTION_MESSAGE;
  }

  public DomainException(final Long errorCode) {
    super(DOMAIN_EXCEPTION_MESSAGE);
    this.arguments = new Object[0];
    this.errorCode = errorCode;
    this.throwable = null;
    this.message = DOMAIN_EXCEPTION_MESSAGE;
  }

  public DomainException(final Long errorCode, final Object... arguments) {
    super(DOMAIN_EXCEPTION_MESSAGE);
    this.arguments = arguments;
    this.errorCode = errorCode;
    this.throwable = null;
    this.message = DOMAIN_EXCEPTION_MESSAGE;
  }
}
