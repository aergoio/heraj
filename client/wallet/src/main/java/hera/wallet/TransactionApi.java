/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.util.List;

/**
 * A transaction related api. No 'Tx' prefix here since {@code TransactionApi} already represent
 * it's transaction related one.
 *
 * @author taeiklim
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface TransactionApi {

  /**
   * Create name info of a current account.
   *
   * @param name an new name
   * @return a create name transaction hash
   */
  TxHash createName(String name);

  /**
   * Update name info of to an new owner.
   *
   * @param name     an already binded name
   * @param newOwner an new owner of name
   * @return a update name transaction hash
   */
  TxHash updateName(String name, AccountAddress newOwner);

  /**
   * Staking an account with amount of a current account.
   *
   * @param amount an amount to stake
   * @return a staking transaction hash
   */
  TxHash stake(Aer amount);

  /**
   * Unstaking an account with amount of a current account.
   *
   * @param amount an amount to stake
   * @return a staking transaction hash
   */
  TxHash unstake(Aer amount);

  /**
   * Vote to bp {@code candidates}.
   *
   * @param candidates a candidates to vote
   * @return voting transaction hash
   */
  TxHash voteBp(List<String> candidates);

  /**
   * Vote to {@code candidates} with {@code voteId}.
   *
   * @param voteId     a vote id
   * @param candidates a candidates to vote
   * @return voting transaction hash
   */
  TxHash vote(String voteId, List<String> candidates);

  /**
   * Send <b>aer</b> with {@code fee}.
   *
   * @param recipient a recipient name
   * @param amount    an amount
   * @param fee       a fee
   * @return a send transaction hash
   */
  TxHash send(String recipient, Aer amount, Fee fee);

  /**
   * Send <b>aer</b> with {@code fee} and {@code payload}.
   *
   * @param recipient a recipient name
   * @param amount    an amount
   * @param fee       a fee
   * @param payload   a payload
   * @return a send transaction hash
   */
  TxHash send(String recipient, Aer amount, Fee fee, BytesValue payload);

  /**
   * Send <b>aer</b> with {@code fee}.
   *
   * @param recipient a recipient
   * @param amount    an amount
   * @param fee       a fee
   * @return a send transaction hash
   */
  TxHash send(AccountAddress recipient, Aer amount, Fee fee);

  /**
   * Send <b>aer</b> with {@code fee} and {@code payload}.
   *
   * @param recipient a recipient
   * @param amount    an amount
   * @param fee       a fee
   * @param payload   a payload
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
   * @param fee                a fee
   * @return a contract transaction hash
   */
  ContractTxHash deploy(ContractDefinition contractDefinition, Fee fee);

  /**
   * Re-deploy a deployed smart smart contract. It works private mode only.
   *
   * @param existingContract   an existing contract address
   * @param contractDefinition a contract definition to re-deploy
   * @param fee                a fee
   * @return a contract transaction hash
   */
  ContractTxHash redeploy(ContractAddress existingContract, ContractDefinition contractDefinition,
      Fee fee);

  /**
   * Execute a smart contract function.
   *
   * @param contractInvocation a contract invocation
   * @param fee                a fee
   * @return a contract transaction hash
   */
  ContractTxHash execute(ContractInvocation contractInvocation, Fee fee);

}
