/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Signature;
import hera.api.model.Transaction;

@ApiAudience.Public
@ApiStability.Unstable
public interface SignOperation {

  /**
   * Sign for transaction.
   *
   * @param transaction transaction to sign
   * @return signing result
   */
  Signature sign(Transaction transaction);

  /**
   * Verify transaction.
   *
   * @param transaction transaction to verify
   * @return verify result
   */
  Boolean verify(Transaction transaction);

}
