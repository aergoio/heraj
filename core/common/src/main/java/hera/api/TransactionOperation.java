/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrError;

public interface TransactionOperation {

  /**
   * Get transaction.
   *
   * @param txHash transaction's hash
   * @return transaction or error
   */
  ResultOrError<Transaction> getTransaction(TxHash txHash);

  /**
   * Sign for transaction.
   *
   * @param transaction transaction to sign
   * @return signing result or error
   *
   * @deprecated please use {@link SignOperation#sign(Transaction)}.
   */
  @Deprecated
  ResultOrError<Signature> sign(Transaction transaction);

  /**
   * Verify transaction.
   *
   * @param transaction transaction to verify
   * @return verify result or error
   *
   * @deprecated please use {@link SignOperation#verify(Transaction)}
   */
  @Deprecated
  ResultOrError<Boolean> verify(Transaction transaction);

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
