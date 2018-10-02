/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;

@ApiAudience.Public
@ApiStability.Unstable
public interface SignEitherOperation {

  /**
   * Sign for transaction.
   *
   * @param transaction transaction to sign
   * @return signing result or error
   */
  ResultOrError<Signature> sign(Transaction transaction);

  /**
   * Verify transaction.
   *
   * @param transaction transaction to verify
   * @return verify result or error
   */
  ResultOrError<Boolean> verify(Transaction transaction);

}
