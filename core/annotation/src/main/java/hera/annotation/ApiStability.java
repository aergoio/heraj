/*
 * @copyright defined in LICENSE.txt
 */

package hera.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ApiAudience.Public
@ApiStability.Unstable
public class ApiStability {
  /**
   * Can evolve while retaining compatibility for minor release boundaries.;
   * can break compatibility only at major release (ie. at m.0).
   */
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Stable {};

  /**
   * No guarantee is provided as to reliability or stability across any
   * level of release granularity.
   */
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Unstable {};

}
