package hera.exception;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public class InvalidKeyStoreFormatException extends HerajException {

  private static final long serialVersionUID = 8640476144823299969L;

  public InvalidKeyStoreFormatException(final String message) {
    super(message);
  }

  public InvalidKeyStoreFormatException(final Throwable cause) {
    super(cause);
  }

  public InvalidKeyStoreFormatException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
