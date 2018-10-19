/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.ContextAware;
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
import hera.api.model.Fee;
import hera.api.tupleorerror.ResultOrError;

@ApiAudience.Public
@ApiStability.Unstable
public interface ContractEitherOperation extends ContextAware {

  /**
   * Get receipt of transaction.
   *
   * @param contractTxHash contract transaction hash
   * @return receipt of transaction or error
   */
  ResultOrError<ContractTxReceipt> getReceipt(ContractTxHash contractTxHash);

  /**
   * Deploy smart contract.
   *
   * @param creator smart contract creator
   * @param contractDefinition contract definition
   * @param fee transaction fee
   * @return contract definition transaction hash or error
   */
  ResultOrError<ContractTxHash> deploy(Account creator, ContractDefinition contractDefinition,
      Fee fee);

  /**
   * Get smart contract interface corresponding to contract address.
   *
   * @param contractAddress contract address
   * @return contract interface or error
   */
  ResultOrError<ContractInterface> getContractInterface(ContractAddress contractAddress);

  /**
   * Execute the smart contract.
   *
   * @param executor smart contract executor
   * @param contractInvocation {@link ContractInvocation}
   * @param fee transaction fee
   * @return contract execution transaction hash or error
   */
  ResultOrError<ContractTxHash> execute(Account executor, ContractInvocation contractInvocation,
      Fee fee);

  /**
   * Query the smart contract state by calling smart contract function.
   *
   * @param contractInvocation {@link ContractInvocation}
   * @return contract result or error
   */
  ResultOrError<ContractResult> query(ContractInvocation contractInvocation);

}
