/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServiceError {

  @Getter
  protected final String message;

  @Getter
  protected final String stacktrace;
}
