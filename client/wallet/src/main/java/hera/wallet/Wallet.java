/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.math.BigInteger;

public interface Wallet extends LookupClient {

  /**
   * Bind a keystore with wallet. This operation has a meaning only for {@link WalletType#Secure}.
   * For other wallet type, do nothing.
   *
   * @param keyStore a java keystore
   */
  void bindKeyStore(java.security.KeyStore keyStore);

  /**
   * Save an aergo key of current account with an encrypt key {@code password}. This operation has
   * no meaning if {@link WalletType#Naive}.
   *
   * @param password an encrypt key
   */
  void saveKey(String password);

  /**
   * Export an aergo key of a current account with encrypted.
   *
   * @param authentication an authentication
   * @return encoded encrypted private key
   */
  String exportKey(Authentication authentication);

  /**
   * Get an account address of current account.
   *
   * @return an account address
   */
  AccountAddress getAddress();

  /**
   * Get recently used nonce value.
   *
   * @return a recently used nonce
   */
  long getRecentlyUsedNonce();

  /**
   * Get state of current account.
   *
   * @return a state of current account
   */
  AccountState getAccountState();

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
   * Send AER with {@code fee}.
   *
   * @param recipient a recipient
   * @param amount an amount in AER. Must be in a number format
   * @param fee a fee
   * @return verify result
   */
  TxHash send(AccountAddress recipient, String amount, Fee fee);

  /**
   * Send AER with {@code fee}.
   *
   * @param recipient a recipient
   * @param amount an amount in AER
   * @param fee a fee
   * @return verify result
   */
  TxHash send(AccountAddress recipient, BigInteger amount, Fee fee);

  /**
   * Sign and commit {@code RawTransaction}.
   *
   * @param rawTransaction raw transaction
   * @return transaction hash
   */
  TxHash commit(RawTransaction rawTransaction);

  /**
   * Commit transaction.
   *
   * @param signedTransaction signed transaction to commit
   * @return transaction hash
   */
  TxHash commit(Transaction signedTransaction);

  /**
   * Deploy smart contract.
   *
   * @param contractDefinition a contract definition
   * @param fee a fee to make a transaction
   * @return contract interface of contract definition
   */
  ContractInterface deploy(ContractDefinition contractDefinition, Fee fee);

  /**
   * Execute a smart contract function.
   *
   * @param contractInvocation a contract invocation
   * @param fee a fee to make a transaction
   * @return a contract transaction hash
   */
  ContractTxHash execute(ContractInvocation contractInvocation, Fee fee);

  /**
   * Query the smart contract state by calling smart contract function.
   *
   * @param contractInvocation a contract invocation
   * @return contract result
   */
  ContractResult query(ContractInvocation contractInvocation);

}
