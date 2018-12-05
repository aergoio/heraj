/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrError;
import java.math.BigInteger;

@ApiAudience.Public
@ApiStability.Unstable
public interface TransactionEitherOperation {

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
   * Send transaction. This method automatically fill nonce, sign and commit in a server. This
   * method is valid only if sender is stored in a server key store. Make sure that {@code sender}
   * is unlocked.
   *
   * @param sender aergo sender
   * @param recipient aergo recipient
   * @param amount aergo amount
   * @return transaction hash or error
   */
  ResultOrError<TxHash> send(AccountAddress sender, AccountAddress recipient, BigInteger amount);

}
