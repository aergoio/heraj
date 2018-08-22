/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.Optional;

public interface TransactionAsyncOperation {

  /**
   * Get transaction asynchronously.
   *
   * @param hash transaction's hash
   * @return future of an Optional transaction or error
   */
  ResultOrErrorFuture<Optional<Transaction>> getTransaction(Hash hash);

  /**
   * Sign for transaction asynchronously.
   *
   * @param transaction transaction to sign
   * @return future of signing result or error
   */
  ResultOrErrorFuture<Signature> sign(Transaction transaction);

  /**
   * Verify transaction asynchronously.
   *
   * @param transaction transaction to verify
   * @return future of verify result or error
   */
  ResultOrErrorFuture<Boolean> verify(Transaction transaction);

  /**
   * Commit transaction asynchronously.
   *
   * @param transaction transaction to commit
   * @return future of transaction hash or error
   */
  ResultOrErrorFuture<Optional<Hash>> commit(Transaction transaction);

}
