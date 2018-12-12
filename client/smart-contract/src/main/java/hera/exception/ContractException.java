/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class ContractException extends HerajException {

  private static final long serialVersionUID = -2608475122225368070L;

  public ContractException(final String message) {
    super(message);
  }

  public ContractException(final Throwable cause) {
    super(cause);
  }

  public ContractException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
