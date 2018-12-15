/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class InvalidAerAmountException extends HerajException {

  private static final long serialVersionUID = -4515299837190754045L;

  public InvalidAerAmountException(String message) {
    super(message);
  }

  public InvalidAerAmountException(Throwable cause) {
    super(cause);
  }

  public InvalidAerAmountException(String message, Throwable cause) {
    super(message, cause);
  }

}
