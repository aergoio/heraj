/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;

public interface TransactionAsyncOperation {

  /**
   * Get transaction asynchronously.
   *
   * @param txHash transaction's hash
   * @return future of an transaction or error
   */
  ResultOrErrorFuture<Transaction> getTransaction(TxHash txHash);

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
  ResultOrErrorFuture<TxHash> commit(Transaction transaction);

  /**
   * Send transaction asynchronously. This method automatically fill nonce, hash and sign in a
   * server.
   *
   * @param transaction transaction to commit
   * @return transaction hash or error
   */
  ResultOrErrorFuture<TxHash> send(Transaction transaction);

}
