/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public class ConnectionException extends HerajException {

  private static final long serialVersionUID = -4798911407894712354L;

  public ConnectionException(String message) {
    super(message);
  }

  public ConnectionException(Throwable cause) {
    super(cause);
  }

  public ConnectionException(String message, Throwable cause) {
    super(message, cause);
  }

}
