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
import hera.api.model.Fee;
import hera.api.tupleorerror.ResultOrErrorFuture;

@ApiAudience.Public
@ApiStability.Unstable
public interface ContractAsyncOperation {

  /**
   * Get receipt of transaction asynchronously.
   *
   * @param contractTxHash contract transaction hash
   * @return future of receipt of transaction or error
   */
  ResultOrErrorFuture<ContractTxReceipt> getReceipt(ContractTxHash contractTxHash);

  /**
   * Deploy smart contract contract.
   *
   * @param creator smart contract creator
   * @param contractDefinition contract definition
   * @param nonce an nonce used in making transaction
   * @param fee transaction fee
   * @return future of contract definition transaction hash or error
   */
  ResultOrErrorFuture<ContractTxHash> deploy(Account creator, ContractDefinition contractDefinition,
      long nonce, Fee fee);


  /**
   * Get smart contract interface corresponding to contract address.
   *
   * @param contractAddress contract address
   * @return future of contract interface or error
   */
  ResultOrErrorFuture<ContractInterface> getContractInterface(ContractAddress contractAddress);

  /**
   * Execute the smart contract.
   *
   * @param executor contract executor
   * @param contractInvocation {@link ContractInvocation}
   * @param nonce an nonce used in making transaction
   * @param fee transaction fee
   * @return future of contract execution transaction hash or error
   */
  ResultOrErrorFuture<ContractTxHash> execute(Account executor,
      ContractInvocation contractInvocation, long nonce, Fee fee);

  /**
   * Query the smart contract state by calling smart contract function.
   *
   * @param contractInvocation {@link ContractInvocation}
   * @return future of contract result or error
   */
  ResultOrErrorFuture<ContractResult> query(ContractInvocation contractInvocation);

}
