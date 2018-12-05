/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.math.BigInteger;

@ApiAudience.Public
@ApiStability.Unstable
public interface TransactionAsyncOperation {

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
   * Send transaction. This method automatically fill nonce, sign and commit in a server. This
   * method is valid only if sender is stored in a server key store. Make sure that {@code sender}
   * is unlocked.
   *
   * @param sender aergo sender
   * @param recipient aergo recipient
   * @param amount aergo amount
   * @return future of transaction hash or error
   */
  ResultOrErrorFuture<TxHash> send(AccountAddress sender, AccountAddress recipient,
      BigInteger amount);

}
