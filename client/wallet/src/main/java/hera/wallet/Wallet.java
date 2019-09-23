/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.KeyStoreException;
import hera.exception.WalletCommitException;
import hera.exception.WalletConnectionException;
import hera.exception.WalletRpcException;
import java.io.Closeable;
import java.util.List;

/**
 * A legacy wallet api.
 *
 * @deprecated use {@link WalletApi} instead.
 *
 */
@ApiAudience.Public
@ApiStability.Unstable
@Deprecated
public interface Wallet extends AccountHoldable, KeyManageable, QueryClient, Closeable {

  /**
   * Get cached chain id hash. null if no cached one.
   *
   * @return a chain id hash
   */
  ChainIdHash getCachedChainIdHash();

  /**
   * Cache chain id hash with current node.
   */
  void cacheChainIdHash();

  /**
   * Cache chain id hash with corresponding {@code chainIdHash}.
   *
   * @param chainIdHash a chain id hash
   */
  void cacheChainIdHash(ChainIdHash chainIdHash);

  /**
   * Unlock and load account corresponding with {@code authentication}.
   *
   * @param authentication an authentication
   * @return an unlocked account. null if failure
   *
   * @throws KeyStoreException on keystore error
   */
  Account loadAccount(Authentication authentication);

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
   * Create name info of a current account.
   *
   * @param name an new name
   * @return a create name transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash createName(String name);

  /**
   * Update name info of to an new owner.
   *
   * @param name an already binded name
   * @param newOwner an new owner of name
   * @return a update name transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash updateName(String name, AccountAddress newOwner);

  /**
   * Staking an account with amount of a current account.
   *
   * @param amount an amount to stake
   * @return a staking transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash stake(Aer amount);

  /**
   * Unstaking an account with amount of a current account.
   *
   * @param amount an amount to stake
   * @return a staking transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash unstake(Aer amount);

  /**
   * Vote to bp {@code candidates}.
   *
   * @param candidates a candidates to vote
   * @return voting transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash voteBp(List<String> candidates);

  /**
   * Vote to {@code candidates} with {@code voteId}.
   *
   * @param voteId a vote id
   * @param candidates a candidates to vote
   * @return voting transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash vote(String voteId, List<String> candidates);

  /**
   * Send <b>aer</b>.
   *
   * @param recipient a recipient name
   * @param amount an amount
   * @return a send transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash send(String recipient, Aer amount);

  /**
   * Send <b>aer</b> with {@code fee}.
   *
   * @param recipient a recipient name
   * @param amount an amount
   * @param fee a fee
   * @return a send transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash send(String recipient, Aer amount, Fee fee);

  /**
   * Send <b>aer</b> and {@code payload}.
   *
   * @param recipient a recipient name
   * @param amount an amount
   * @param payload a payload
   * @return a send transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash send(String recipient, Aer amount, BytesValue payload);

  /**
   * Send <b>aer</b> with {@code fee} and {@code payload}.
   *
   * @param recipient a recipient name
   * @param amount an amount
   * @param fee a fee
   * @param payload a payload
   * @return a send transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash send(String recipient, Aer amount, Fee fee, BytesValue payload);

  /**
   * Send <b>aer</b> with {@code fee}.
   *
   * @param recipient a recipient
   * @param amount an amount
   * @return a send transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash send(AccountAddress recipient, Aer amount);

  /**
   * Send <b>aer</b> with {@code fee}.
   *
   * @param recipient a recipient
   * @param amount an amount
   * @param fee a fee
   * @return a send transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash send(AccountAddress recipient, Aer amount, Fee fee);

  /**
   * Send <b>aer</b> with {@code fee} and {@code payload}.
   *
   * @param recipient a recipient
   * @param amount an amount
   * @param payload a payload
   * @return a send transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash send(AccountAddress recipient, Aer amount, BytesValue payload);

  /**
   * Send <b>aer</b> with {@code fee} and {@code payload}.
   *
   * @param recipient a recipient
   * @param amount an amount
   * @param fee a fee
   * @param payload a payload
   * @return a send transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash send(AccountAddress recipient, Aer amount, Fee fee, BytesValue payload);

  /**
   * Sign and commit transaction.
   *
   * @param rawTransaction a raw transaction
   * @return a transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash commit(RawTransaction rawTransaction);

  /**
   * Commit a signed transaction.
   *
   * @param signedTransaction a signed transaction
   * @return a transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  TxHash commit(Transaction signedTransaction);

  /**
   * Deploy smart contract.
   *
   * @param contractDefinition a contract definition
   * @return a contract transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  ContractTxHash deploy(ContractDefinition contractDefinition);

  /**
   * Deploy smart contract.
   *
   * @param contractDefinition a contract definition
   * @param fee a fee to make a transaction
   * @return a contract transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  ContractTxHash deploy(ContractDefinition contractDefinition, Fee fee);

  /**
   * Execute a smart contract function.
   *
   * @param contractInvocation a contract invocation
   * @return a contract transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  ContractTxHash execute(ContractInvocation contractInvocation);

  /**
   * Execute a smart contract function.
   *
   * @param contractInvocation a contract invocation
   * @param fee a fee to make a transaction
   * @return a contract transaction hash
   *
   * @throws WalletCommitException on commit failure
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  ContractTxHash execute(ContractInvocation contractInvocation, Fee fee);

  /**
   * {@inheritDoc}
   */
  void close();

}
