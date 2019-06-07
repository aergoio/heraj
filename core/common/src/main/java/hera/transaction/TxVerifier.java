/*
 * @copyright defined in LICENSE.txt
 */

package hera.transaction;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Transaction;

@ApiAudience.Public
@ApiStability.Unstable
public interface TxVerifier {

  /**
   * Check if {@code Transaction} is valid.
   *
   * @param transaction transaction to verify
   * @return if valid
   */
  boolean verify(Transaction transaction);

}