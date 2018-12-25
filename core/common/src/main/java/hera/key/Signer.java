/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.exception.SignException;

@ApiAudience.Private
@ApiStability.Unstable
public interface Signer {

  /**
   * Sign to raw transaction.
   *
   * @param rawTransaction raw transaction to sign
   * @return a signed transaction
   * @throws SignException on failure
   */
  Transaction sign(final RawTransaction rawTransaction);

  /**
   * Check if {@code Transaction} is valid.
   *
   * @param transaction transaction to verify
   * @return if valid
   */
  boolean verify(final Transaction transaction);

}
