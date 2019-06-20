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
import hera.api.model.Subscription;
import hera.key.Signer;
import java.util.List;

/**
 * Provide contract related operations. It provides followings:
 *
 * <ul>
 * <li>lookup contract transaction receipt</li>
 * <li>getting already deployed contract interface</li>
 * <li>deploying / executing / querying contract</li>
 * <li>contract event related operations</li>
 * </ul>
 *
 * @author bylee, Taeik Lim
 *
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface ContractOperation {

  /**
   * Get receipt of transaction.
   *
   * @param contractTxHash contract transaction hash
   * @return receipt of transaction
   */
  ContractTxReceipt getReceipt(ContractTxHash contractTxHash);

  /**
   * Use {@link #deploy(Signer, ContractDefinition, long, Fee)} instead.
   *
   * @param creator an creator account
   * @param contractDefinition contract definition
   * @param nonce an nonce used in making transaction
   * @return contract definition transaction hash
   */
  @Deprecated
  ContractTxHash deploy(Account creator, ContractDefinition contractDefinition, long nonce);

  /**
   * Use {@link #deploy(Signer, ContractDefinition, long, Fee)} instead.
   *
   * @param creator an creator account
   * @param contractDefinition contract definition
   * @param nonce an nonce used in making transaction
   * @param fee transaction fee
   * @return contract definition transaction hash
   */
  @Deprecated
  ContractTxHash deploy(Account creator, ContractDefinition contractDefinition, long nonce,
      Fee fee);

  /**
   * Deploy smart contract.
   *
   * @param signer a signer whose principle is smart contract creator.
   * @param contractDefinition contract definition
   * @param nonce an nonce used in making transaction
   * @param fee transaction fee
   * @return contract definition transaction hash
   */
  ContractTxHash deploy(Signer signer, ContractDefinition contractDefinition, long nonce,
      Fee fee);

  /**
   * Get smart contract interface corresponding to contract address.
   *
   * @param contractAddress contract address
   * @return contract interface
   */
  ContractInterface getContractInterface(ContractAddress contractAddress);

  /**
   * Use {@link #execute(Signer, ContractInvocation, long, Fee)} instead.
   *
   * @param executor an executor account
   * @param contractInvocation {@link ContractInvocation}
   * @param nonce an nonce used in making transaction
   * @return contract execution transaction hash
   */
  @Deprecated
  ContractTxHash execute(Account executor, ContractInvocation contractInvocation, long nonce);

  /**
   * Use {@link #execute(Signer, ContractInvocation, long, Fee)} instead.
   *
   * @param executor an executor account
   * @param contractInvocation {@link ContractInvocation}
   * @param nonce an nonce used in making transaction
   * @param fee transaction fee
   * @return contract execution transaction hash
   */
  @Deprecated
  ContractTxHash execute(Account executor, ContractInvocation contractInvocation, long nonce,
      Fee fee);

  /**
   * Execute the smart contract.
   *
   * @param signer a signer which will execute smart contract.
   * @param contractInvocation {@link ContractInvocation}
   * @param nonce an nonce used in making transaction
   * @param fee transaction fee
   * @return contract execution transaction hash
   */
  ContractTxHash execute(Signer signer, ContractInvocation contractInvocation, long nonce,
      Fee fee);

  /**
   * Query the smart contract state by calling smart contract function.
   *
   * @param contractInvocation {@link ContractInvocation}
   * @return contract result
   */
  ContractResult query(ContractInvocation contractInvocation);

  /**
   * List events corresponding with event filter.
   *
   * @param filter an event filter
   * @return event list
   */
  List<Event> listEvents(EventFilter filter);

  /**
   * Subscribe event corresponding with event filter.
   *
   * @param filter an event filter
   * @param observer a stream observer which is invoked on event
   * @return a subscription
   */
  Subscription<Event> subscribeEvent(EventFilter filter,
      hera.api.model.StreamObserver<Event> observer);

}
