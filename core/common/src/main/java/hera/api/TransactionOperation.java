/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.ContextAware;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Transaction;
import hera.api.model.TxHash;

@ApiAudience.Public
@ApiStability.Unstable
public interface TransactionOperation extends ContextAware {

  /**
   * Get transaction.
   *
   * @param txHash transaction's hash
   * @return transaction
   */
  Transaction getTransaction(TxHash txHash);

  /**
   * Commit transaction.
   *
   * @param transaction transaction to commit
   * @return transaction hash
   */
  TxHash commit(Transaction transaction);

  /**
   * Send transaction. This method automatically fill nonce, hash and sign in a server.
   *
   * @param transaction transaction to commit
   * @return transaction hash
   */
  TxHash send(Transaction transaction);

}
