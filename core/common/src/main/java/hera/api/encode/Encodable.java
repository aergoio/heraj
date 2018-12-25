/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.encode;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Private
@ApiStability.Unstable
public interface Encodable {

  /**
   * Get encoded value.
   *
   * @return an encoded value
   */
  String getEncoded();

}
