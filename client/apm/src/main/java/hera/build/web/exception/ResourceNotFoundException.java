/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.exception;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class ResourceNotFoundException extends HttpException {

  public ResourceNotFoundException() {
    this(null);
  }

  public ResourceNotFoundException(final String message) {
    super(SC_NOT_FOUND, message);
  }

}
