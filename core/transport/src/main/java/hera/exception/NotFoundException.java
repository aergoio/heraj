/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class NotFoundException extends RpcException {
  
  private static final long serialVersionUID = -4798911407894712354L;

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
