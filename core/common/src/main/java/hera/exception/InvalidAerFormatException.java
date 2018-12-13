/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class InvalidAerFormatException extends HerajException {

  private static final long serialVersionUID = -4515299837190754045L;

  public InvalidAerFormatException(String message) {
    super(message);
  }

  public InvalidAerFormatException(Throwable cause) {
    super(cause);
  }

  public InvalidAerFormatException(String message, Throwable cause) {
    super(message, cause);
  }

}
