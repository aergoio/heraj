/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HttpException extends RuntimeException {
  @Getter
  protected final int statusCode;

  public HttpException(final int statusCode, final String message, final Throwable cause) {
    super(message, cause);
    this.statusCode = statusCode;
  }

  public HttpException(final int statusCode, final String message) {
    super(message);
    this.statusCode = statusCode;
  }

}
