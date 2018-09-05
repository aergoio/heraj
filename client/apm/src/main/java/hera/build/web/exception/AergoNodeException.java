/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.exception;

import hera.exception.HerajException;

public class AergoNodeException extends HttpException {

  public AergoNodeException(final String message, final HerajException cause) {
    super(500, message, cause);
  }
}
