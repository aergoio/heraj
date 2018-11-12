/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class RpcException extends HerajException {

  private static final long serialVersionUID = 5082048400258592422L;

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
