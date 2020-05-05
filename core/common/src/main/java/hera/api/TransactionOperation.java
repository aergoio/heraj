/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.Name;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.TxReceipt;
import hera.key.Signer;

/**
 * Provide transaction related operations.
 *
 * @author bylee, Taeik Lim
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface TransactionOperation {

  /**
   * Get transaction.
   *
   * @param txHash a transaction hash
   * @return transaction. null if no matching one.
   */
  Transaction getTransaction(TxHash txHash);

  /**
   * Get tx receipt.
   *
   * @param txHash a transaction hash
   * @return a tx receipt. null if no matching one.
   */
  TxReceipt getTxReceipt(TxHash txHash);

  /**
   * Commit transaction.
   *
   * @param transaction transaction to commit
   * @return transaction hash
   */
  TxHash commit(Transaction transaction);

  /**
   * Send aergo.
   *
   * @param signer    a signer to send aergo
   * @param recipient a recipient to get aergo
   * @param amount    an amount to send
   * @param nonce     an nonce used in making transaction
   * @param fee       transaction fee
   * @param payload   a payload
   * @return transaction hash
   */
  TxHash sendTx(Signer signer, AccountAddress recipient, Aer amount, long nonce,
      Fee fee, BytesValue payload);

  /**
   * Send aergo.
   *
   * @param signer    a signer to send aergo
   * @param recipient a recipient to get aergo
   * @param amount    an amount to send
   * @param nonce     an nonce used in making transaction
   * @param fee       transaction fee
   * @param payload   a payload
   * @return transaction hash
   */
  TxHash sendTx(Signer signer, Name recipient, Aer amount, long nonce,
      Fee fee, BytesValue payload);

}
