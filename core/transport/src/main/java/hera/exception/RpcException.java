/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import hera.exception.HerajException;

public class RpcException extends HerajException {

  private static final long serialVersionUID = -4798911407894712354L;

  protected RpcException() {
    super();
  }

  public RpcException(String message) {
    super(message);
  }

  public RpcException(Throwable cause) {
    super(cause);
  }

  public RpcException(String message, Throwable cause) {
    super(message, cause);
  }

}
