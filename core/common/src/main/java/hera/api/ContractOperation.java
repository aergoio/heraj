/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.Fee;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.api.model.TxHash;
import hera.key.Signer;
import java.util.List;

/**
 * Provide contract related operations. It provides followings:
 *
 * @author bylee, Taeik Lim
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface ContractOperation {

  /**
   * Get contract tx receipt.
   *
   * @param contractTxHash a contract transaction hash
   * @return a receipt of transaction. null if no matching one.
   * @deprecated use {@link #getContractTxReceipt(TxHash)} instead.
   */
  @Deprecated
  ContractTxReceipt getReceipt(ContractTxHash contractTxHash);

  /**
   * Get contract tx receipt.
   *
   * @param txHash a contract transaction hash
   * @return a receipt of transaction. null if no matching one.
   */
  ContractTxReceipt getContractTxReceipt(TxHash txHash);

  /**
   * Deploy smart contract.
   *
   * @param creator            an creator account
   * @param contractDefinition contract definition
   * @param nonce              an nonce used in making transaction
   * @return contract definition transaction hash
   * @deprecated use {@link #deployTx(Signer, ContractDefinition, long, Fee)} instead.
   */
  @Deprecated
  ContractTxHash deploy(Account creator, ContractDefinition contractDefinition, long nonce);

  /**
   * Deploy smart contract.
   *
   * @param creator            an creator account
   * @param contractDefinition contract definition
   * @param nonce              an nonce used in making transaction
   * @param fee                transaction fee
   * @return contract definition transaction hash
   * @deprecated use {@link #deployTx(Signer, ContractDefinition, long, Fee)} instead.
   */
  @Deprecated
  ContractTxHash deploy(Account creator, ContractDefinition contractDefinition, long nonce,
      Fee fee);

  /**
   * Deploy smart contract.
   *
   * @param signer             a signer whose principal is smart contract creator.
   * @param contractDefinition a contract definition
   * @param nonce              an nonce used in making transaction
   * @param fee                a transaction fee
   * @return contract definition transaction hash
   * @deprecated use {@link #deployTx(Signer, ContractDefinition, long, Fee)} instead.
   */
  @Deprecated
  ContractTxHash deploy(Signer signer, ContractDefinition contractDefinition, long nonce,
      Fee fee);

  /**
   * Deploy smart contract.
   *
   * @param signer             a signer whose principal is smart contract creator.
   * @param contractDefinition a contract definition
   * @param nonce              an nonce used in making transaction
   * @param fee                a transaction fee
   * @return contract definition transaction hash
   */
  TxHash deployTx(Signer signer, ContractDefinition contractDefinition, long nonce,
      Fee fee);

  /**
   * Re-deploy smart contract. A principal of {@code signer} must be creator and {@code
   * contractAddress} must be existing one.
   *
   * @param signer             a signer whose principal is smart contract creator.
   * @param existingContract   an existing contract address
   * @param contractDefinition contract definition to re-deploy
   * @param nonce              an nonce used in making transaction
   * @param fee                a transaction fee
   * @return contract definition transaction hash
   * @deprecated use {@link #redeployTx(Signer, ContractAddress, ContractDefinition, long, Fee)}.
   */
  @Deprecated
  ContractTxHash redeploy(Signer signer, ContractAddress existingContract,
      ContractDefinition contractDefinition, long nonce, Fee fee);

  /**
   * Re-deploy smart contract. A principal of {@code signer} must be creator and {@code
   * contractAddress} must be existing one.
   *
   * @param signer             a signer whose principal is smart contract creator.
   * @param existingContract   an existing contract address
   * @param contractDefinition contract definition to re-deploy
   * @param nonce              an nonce used in making transaction
   * @param fee                a transaction fee
   * @return contract definition transaction hash
   */
  TxHash redeployTx(Signer signer, ContractAddress existingContract,
      ContractDefinition contractDefinition, long nonce, Fee fee);

  /**
   * Get smart contract interface corresponding to contract address.
   *
   * @param contractAddress contract address
   * @return contract interface. null if no matching one.
   */
  ContractInterface getContractInterface(ContractAddress contractAddress);

  /**
   * Execute the smart contract.
   *
   * @param executor           an executor account
   * @param contractInvocation {@link ContractInvocation}
   * @param nonce              an nonce used in making transaction
   * @return contract execution transaction hash
   * @deprecated use {@link #executeTx(Signer, ContractInvocation, long, Fee)} instead.
   */
  @Deprecated
  ContractTxHash execute(Account executor, ContractInvocation contractInvocation, long nonce);

  /**
   * Execute the smart contract.
   *
   * @param executor           an executor account
   * @param contractInvocation {@link ContractInvocation}
   * @param nonce              an nonce used in making transaction
   * @param fee                transaction fee
   * @return contract execution transaction hash
   * @deprecated use {@link #executeTx(Signer, ContractInvocation, long, Fee)} instead.
   */
  @Deprecated
  ContractTxHash execute(Account executor, ContractInvocation contractInvocation, long nonce,
      Fee fee);

  /**
   * Execute the smart contract.
   *
   * @param signer             a signer which will execute smart contract.
   * @param contractInvocation {@link ContractInvocation}
   * @param nonce              an nonce used in making transaction
   * @param fee                transaction fee
   * @return contract execution transaction hash
   * @deprecated use {@link #executeTx(Signer, ContractInvocation, long, Fee)} instead.
   */
  @Deprecated
  ContractTxHash execute(Signer signer, ContractInvocation contractInvocation, long nonce,
      Fee fee);

  /**
   * Execute the smart contract.
   *
   * @param signer             a signer which will execute smart contract.
   * @param contractInvocation {@link ContractInvocation}
   * @param nonce              an nonce used in making transaction
   * @param fee                transaction fee
   * @return contract execution transaction hash
   */
  TxHash executeTx(Signer signer, ContractInvocation contractInvocation, long nonce,
      Fee fee);

  /**
   * Query the smart contract state by calling smart contract function.
   *
   * @param contractInvocation {@link ContractInvocation}
   * @return contract result
   */
  ContractResult query(ContractInvocation contractInvocation);

  /**
   * List events corresponding to an event filter.
   *
   * @param filter an event filter
   * @return event list. empty list if no matching one.
   */
  List<Event> listEvents(EventFilter filter);

  /**
   * Subscribe event corresponding to an event filter.
   *
   * @param filter   an event filter
   * @param observer a stream observer which is invoked on event
   * @return a subscription
   */
  Subscription<Event> subscribeEvent(EventFilter filter, StreamObserver<Event> observer);

}
