/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class WalletException extends HerajException {

  private static final long serialVersionUID = 3919743892260214223L;

  public WalletException(final String message) {
    super(message);
  }

  public WalletException(final Throwable cause) {
    super(cause);
  }

  public WalletException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
