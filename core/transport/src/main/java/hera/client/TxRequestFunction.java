/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function2;
import hera.api.model.TxHash;
import hera.key.Signer;

@ApiAudience.Private
@ApiStability.Unstable
public interface TxRequestFunction extends Function2<Signer, Long, TxHash> {

  /**
   * Make a transaction.
   *
   * @param signer a signer to make transaction
   * @param nonce  an nonce to be used in making transaction
   * @return a tx hash
   */
  TxHash apply(Signer signer, Long nonce);

}
