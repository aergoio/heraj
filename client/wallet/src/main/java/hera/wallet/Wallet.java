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
import hera.exception.InvalidAuthentiationException;
import hera.exception.UnbindedAccountException;
import hera.exception.UnbindedKeyStoreException;
import hera.key.AergoKey;
import java.io.Closeable;

public interface Wallet extends LookupClient, NonceManagable, Closeable {

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
   *
   * @throws UnbindedKeyStoreException if it's {@link WalletType#Secure} and keystore is not binded
   */
  void saveKey(AergoKey aergoKey, String password);

  /**
   * Export an aergo key of a current account with encrypted.
   *
   * @param authentication an authentication
   * @return encoded encrypted private key
   *
   * @throws InvalidAuthentiationException on failure
   * @throws UnbindedKeyStoreException if it's {@link WalletType#Secure} and keystore is not binded
   */
  String exportKey(Authentication authentication);

  /**
   * Unlock account with {@code Authentication}.
   *
   * @param authentication an authentication
   * @return unlock result
   *
   * @throws UnbindedKeyStoreException if it's {@link WalletType#Secure} and keystore is not binded
   */
  boolean unlock(Authentication authentication);

  /**
   * Lock current account with {@code Authentication}.
   *
   * @param authentication an authentication
   * @return unlock result
   *
   * @throws UnbindedKeyStoreException if it's {@link WalletType#Secure} and keystore is not binded
   */
  boolean lock(Authentication authentication);

  /**
   * Store the keystore to the path. This operation has a meaning only for
   * {@link WalletType#Secure}. For other wallet type, do nothing.
   *
   * @param path a path
   * @param password a password used in storing key store
   * @return store result
   *
   * @throws UnbindedKeyStoreException if it's {@link WalletType#Secure} and keystore is not binded
   */
  boolean storeKeyStore(String path, String password);

  /**
   * Create name info of a current account.
   *
   * @param name an new name
   * @return a create name transaction hash
   * @throws UnbindedAccountException if account isn't binded
   */
  TxHash createName(String name);

  /**
   * Update name info of to an new owner.
   *
   * @param name an already binded name
   * @param newOwner an new owner of name
   * @return a update name transaction hash
   * @throws UnbindedAccountException if account isn't binded
   */
  TxHash updateName(String name, AccountAddress newOwner);

  /**
   * Sign for transaction.
   *
   * @param rawTransaction raw transaction to sign
   * @return signed transaction
   *
   * @throws UnbindedAccountException if account isn't binded
   */
  Transaction sign(RawTransaction rawTransaction);

  /**
   * Verify transaction.
   *
   * @param transaction transaction to verify
   * @return verify result
   *
   * @throws UnbindedAccountException if account isn't binded
   */
  boolean verify(Transaction transaction);

  /**
   * Send <b>aer</b> with {@code fee}.
   *
   * @param recipient a recipient name
   * @param amount an amount
   * @param fee a fee
   * @return a send transaction hash
   *
   * @throws UnbindedAccountException if account isn't binded
   */
  TxHash send(String recipient, Aer amount, Fee fee);

  /**
   * Send <b>aer</b> with {@code fee} and {@code payload}.
   *
   * @param recipient a recipient name
   * @param amount an amount
   * @param fee a fee
   * @param payload a payload
   * @return a send transaction hash
   *
   * @throws UnbindedAccountException if account isn't binded
   */
  TxHash send(String recipient, Aer amount, Fee fee, BytesValue payload);

  /**
   * Send <b>aer</b> with {@code fee}.
   *
   * @param recipient a recipient
   * @param amount an amount
   * @param fee a fee
   * @return a send transaction hash
   *
   * @throws UnbindedAccountException if account isn't binded
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
   *
   * @throws UnbindedAccountException if account isn't binded
   */
  TxHash send(AccountAddress recipient, Aer amount, Fee fee, BytesValue payload);

  /**
   * Sign and commit transaction.
   *
   * @param rawTransaction a raw transaction
   * @return a transaction hash
   *
   * @throws UnbindedAccountException if account isn't binded
   */
  TxHash commit(RawTransaction rawTransaction);

  /**
   * Commit a signed transaction.
   *
   * @param signedTransaction a signed transaction
   * @return a transaction hash
   *
   * @throws UnbindedAccountException if account isn't binded
   */
  TxHash commit(Transaction signedTransaction);

  /**
   * Deploy smart contract.
   *
   * @param contractDefinition a contract definition
   * @param fee a fee to make a transaction
   * @return a contract transaction hash
   *
   * @throws UnbindedAccountException if account isn't binded
   */
  ContractTxHash deploy(ContractDefinition contractDefinition, Fee fee);

  /**
   * Execute a smart contract function.
   *
   * @param contractInvocation a contract invocation
   * @param fee a fee to make a transaction
   * @return a contract transaction hash
   *
   * @throws UnbindedAccountException if account isn't binded
   */
  ContractTxHash execute(ContractInvocation contractInvocation, Fee fee);

  /**
   * {@inheritDoc}
   */
  void close();

}
