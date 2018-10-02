/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrError;

@ApiAudience.Public
@ApiStability.Unstable
public interface TransactionOperation {

  /**
   * Get transaction.
   *
   * @param txHash transaction's hash
   * @return transaction or error
   */
  ResultOrError<Transaction> getTransaction(TxHash txHash);

  /**
   * Commit transaction.
   *
   * @param transaction transaction to commit
   * @return transaction hash or error
   */
  ResultOrError<TxHash> commit(Transaction transaction);

  /**
   * Send transaction. This method automatically fill nonce, hash and sign in a server.
   *
   * @param transaction transaction to commit
   * @return transaction hash or error
   */
  ResultOrError<TxHash> send(Transaction transaction);

}
