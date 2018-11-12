/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class RpcConnectionException extends RpcException {

  private static final long serialVersionUID = -4798911407894712354L;

  public RpcConnectionException(String message) {
    super(message);
  }

  public RpcConnectionException(Throwable cause) {
    super(cause);
  }

  public RpcConnectionException(String message, Throwable cause) {
    super(message, cause);
  }

}
