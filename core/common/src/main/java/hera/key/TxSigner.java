/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;

@ApiAudience.Public
@ApiStability.Unstable
@Deprecated
public interface TxSigner {

  /**
   * Sign to raw transaction.
   *
   * @param rawTransaction raw transaction to sign
   * @return a signed transaction
   */
  Transaction sign(RawTransaction rawTransaction);

}
