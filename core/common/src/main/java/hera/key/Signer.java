/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;

@ApiAudience.Public
@ApiStability.Unstable
public interface Signer extends TxSigner {

  /**
   * Get principal of signer.
   *
   * @return an principal of signer
   */
  AccountAddress getPrincipal();

}
