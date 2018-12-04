/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class RpcArgumentException extends RpcException {

  private static final long serialVersionUID = -8523997167258572459L;

  public RpcArgumentException(final String target, final String requirement) {
    this(String.format("%s should be %s", target, requirement));
  }

  public RpcArgumentException(final String message) {
    super(message);
  }

}
