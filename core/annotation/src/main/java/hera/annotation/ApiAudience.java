/*
 * @copyright defined in LICENSE.txt
 */

package hera.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ApiAudience.Public
@ApiStability.Unstable
public class ApiAudience {
  /**
   * Intended for use by any project or application.
   */
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Public {}

  /**
   * Intended for use only within hera itself.
   */
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Private {}
}
