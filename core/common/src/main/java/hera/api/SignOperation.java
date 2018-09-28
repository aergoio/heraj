/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;

public interface SignOperation {

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
