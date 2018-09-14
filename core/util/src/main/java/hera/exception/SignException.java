/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class SignException extends RuntimeException {

  private static final long serialVersionUID = 1000342726629917623L;

  public SignException(String message) {
    super(message);
  }

  public SignException(final Throwable cause) {
    super(cause);
  }
}
