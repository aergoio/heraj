/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class UnableToGenerateKeyException extends HerajException {

  private static final long serialVersionUID = -41654396211154651L;

  public UnableToGenerateKeyException(String message) {
    super(message);
  }

  public UnableToGenerateKeyException(Throwable cause) {
    super(cause);
  }

  public UnableToGenerateKeyException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public HerajException clone() {
    return new UnableToGenerateKeyException(getMessage(), getCause());
  }

}
