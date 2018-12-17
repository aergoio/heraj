/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoKey;

public interface Wallet extends LookupClient, NonceManagable {
  
  /**
   * Bind a keystore with wallet. This operation has a meaning only for {@link WalletType#Secure}.
   * For other wallet type, do nothing.
   *
   * @param keyStore a java keystore
   */
  void bindKeyStore(java.security.KeyStore keyStore);

  /**
   * Save an aergo key to the key store. This operation has no meaning to {@link WalletType#Naive}.
   *
   * @param aergoKey an aergo key
   * @param password an encrypt key
   */
  void saveKey(AergoKey aergoKey, String password);

  /**
   * Export an aergo key of a current account with encrypted.
   *
   * @param authentication an authentication
   * @return encoded encrypted private key
   */
  String exportKey(Authentication authentication);

  /**
   * Unlock account with {@code Authentication}.
   *
   * @param authentication an authentication
   * @return unlock result
   */
  boolean unlock(Authentication authentication);

  /**
   * Lock current account with {@code Authentication}.
   *
   * @param authentication an authentication
   * @return unlock result
   */
  boolean lock(Authentication authentication);

  /**
   * Sign for transaction.
   *
   * @param rawTransaction raw transaction to sign
   * @return signed transaction
   */
  Transaction sign(RawTransaction rawTransaction);

  /**
   * Verify transaction.
   *
   * @param transaction transaction to verify
   * @return verify result
   */
  boolean verify(Transaction transaction);

  /**
   * Send <b>aer</b> with {@code fee}.
   *
   * @param recipient a recipient
   * @param amount an amount
   * @param fee a fee
   * @return a send transaction hash
   */
  TxHash send(AccountAddress recipient, Aer amount, Fee fee);

  /**
   * Send <b>aer</b> with {@code fee} and {@code payload}.
   *
   * @param recipient a recipient
   * @param amount an amount
   * @param fee a fee
   * @param payload a payload
   * @return a send transaction hash
   */
  TxHash send(AccountAddress recipient, Aer amount, Fee fee, BytesValue payload);

  /**
   * Sign and commit transaction.
   *
   * @param rawTransaction a raw transaction
   * @return a transaction hash
   */
  TxHash commit(RawTransaction rawTransaction);

  /**
   * Commit a signed transaction.
   *
   * @param signedTransaction a signed transaction
   * @return a transaction hash
   */
  TxHash commit(Transaction signedTransaction);

  /**
   * Deploy smart contract.
   *
   * @param contractDefinition a contract definition
   * @param fee a fee to make a transaction
   * @return a contract transaction hash
   */
  ContractTxHash deploy(ContractDefinition contractDefinition, Fee fee);

  /**
   * Execute a smart contract function.
   *
   * @param contractInvocation a contract invocation
   * @param fee a fee to make a transaction
   * @return a contract transaction hash
   */
  ContractTxHash execute(ContractInvocation contractInvocation, Fee fee);

}
