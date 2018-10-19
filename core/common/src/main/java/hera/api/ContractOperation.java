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

@ApiAudience.Public
@ApiStability.Unstable
public interface ContractOperation extends ContextAware {

  /**
   * Get receipt of transaction.
   *
   * @param contractTxHash contract transaction hash
   * @return receipt of transaction
   */
  ContractTxReceipt getReceipt(ContractTxHash contractTxHash);

  /**
   * Deploy smart contract.
   *
   * @param creator smart contract creator
   * @param contractDefinition contract definition
   * @return contract definition transaction hash
   */
  ContractTxHash deploy(Account creator, ContractDefinition contractDefinition);

  /**
   * Get smart contract interface corresponding to contract address.
   *
   * @param contractAddress contract address
   * @return contract interface
   */
  ContractInterface getContractInterface(ContractAddress contractAddress);

  /**
   * Execute the smart contract.
   *
   * @param executor smart contract executor
   * @param contractInvocation {@link ContractInvocation}
   * @return contract execution transaction hash
   */
  ContractTxHash execute(Account executor, ContractInvocation contractInvocation);

  /**
   * Query the smart contract state by calling smart contract function.
   *
   * @param contractInvocation {@link ContractInvocation}
   * @return contract result
   */
  ContractResult query(ContractInvocation contractInvocation);

}
