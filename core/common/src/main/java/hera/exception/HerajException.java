/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class HerajException extends RuntimeException {

  public HerajException(String message) {
    super(message);
  }

  public HerajException(Throwable cause) {
    super(cause);
  }

  public HerajException(String message, Throwable cause) {
    super(message, cause);
  }

}
