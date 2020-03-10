/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Transaction;
import hera.api.model.TxHash;

/**
 * Provide transaction related operations. It provides followings:
 *
 * <ul>
 *  <li>lookup transaction</li>
 *  <li>commit transaction</li>
 *  <li>sending aergo with a key stored in server keystore</li>
 * </ul>
 *
 * @author bylee, Taeik Lim
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface TransactionOperation {

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

}
