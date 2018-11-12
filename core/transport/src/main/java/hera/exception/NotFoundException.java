/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class NotFoundException extends RpcException {

  private static final long serialVersionUID = -143184750194736228L;

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(Throwable cause) {
    super(cause);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

}
