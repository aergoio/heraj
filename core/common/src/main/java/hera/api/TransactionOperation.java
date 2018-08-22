/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import java.util.Optional;

public interface TransactionOperation {

  /**
   * Get transaction.
   *
   * @param hash transaction's hash
   * @return transaction or error
   */
  ResultOrError<Optional<Transaction>> getTransaction(Hash hash);

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

  /**
   * Commit transaction.
   *
   * @param transaction transaction to commit
   * @return transaction hash or error
   */
  ResultOrError<Optional<Hash>> commit(Transaction transaction);

}
