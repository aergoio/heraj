/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface Identity {

  /**
   * Get identity.
   *
   * @return an identity
   */
  String getInfo();

}
