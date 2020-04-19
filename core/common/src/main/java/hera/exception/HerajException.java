/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public class HerajException extends RuntimeException {

  private static final long serialVersionUID = 1429103468497275409L;

  protected HerajException() {
    super();
  }

  public HerajException(String message) {
    super(message);
  }

  public HerajException(Throwable cause) {
    super(cause);
  }

  public HerajException(String message, Throwable cause) {
    super(message, cause);
  }

}
