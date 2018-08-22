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

  @Getter
  protected final String message;

}
