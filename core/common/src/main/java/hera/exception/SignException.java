/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class SignException extends HerajException {

  private static final long serialVersionUID = -5006002440529452290L;

  public SignException(String message) {
    super(message);
  }

  public SignException(Throwable cause) {
    super(cause);
  }

  public SignException(String message, Throwable cause) {
    super(message, cause);
  }

}
