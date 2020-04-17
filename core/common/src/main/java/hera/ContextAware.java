/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface ContextAware {

  void setContext(Context context);

}
