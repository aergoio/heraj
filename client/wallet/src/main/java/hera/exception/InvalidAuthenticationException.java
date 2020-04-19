package hera.exception;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public class InvalidAuthenticationException extends HerajException {

  private static final long serialVersionUID = -5982577098746592808L;

  public InvalidAuthenticationException() {
    this("Invalid authentication");
  }

  public InvalidAuthenticationException(final String message) {
    super(message);
  }

  public InvalidAuthenticationException(final Throwable cause) {
    super(cause);
  }

  public InvalidAuthenticationException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
