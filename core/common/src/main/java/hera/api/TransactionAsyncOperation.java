/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.ContextAware;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;

@ApiAudience.Public
@ApiStability.Unstable
public interface TransactionAsyncOperation extends ContextAware {


  /**
   * Get transaction asynchronously.
   *
   * @param txHash transaction's hash
   * @return future of an transaction or error
   */
  ResultOrErrorFuture<Transaction> getTransaction(TxHash txHash);

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
