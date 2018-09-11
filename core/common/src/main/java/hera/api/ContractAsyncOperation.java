/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInferface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.util.DangerousSupplier;

public interface ContractAsyncOperation {

  /**
   * Get receipt of transaction asynchronously.
   *
   * @param contractTxHash contract transaction hash
   * @return future of receipt of transaction or error
   */
  ResultOrErrorFuture<ContractTxReceipt> getReceipt(ContractTxHash contractTxHash);

  /**
   * Deploy smart contract contract code in payload form encoded with base58.
   *
   * @param creator smart contract creator
   * @param rawContractCode contract code in payload
   * @return future of contract definition transaction hash or error
   */
  ResultOrErrorFuture<ContractTxHash> deploy(AccountAddress creator,
      DangerousSupplier<byte[]> rawContractCode);

  /**
   * Get smart contract interface corresponding to contract address.
   *
   * @param contractAddress contract address
   * @return future of contract interface or error
   */
  ResultOrErrorFuture<ContractInferface> getContractInterface(ContractAddress contractAddress);

  /**
   * Execute the smart contract.
   *
   * @param executor contract executor
   * @param contractAddress contract address
   * @param contractFunction contract function
   * @param args contract function arguments
   * @return future of contract execution transaction hash or error
   */
  ResultOrErrorFuture<ContractTxHash> execute(AccountAddress executor,
      ContractAddress contractAddress, ContractFunction contractFunction, Object... args);

  /**
   * Query the smart contract state.
   *
   * @param contractAddress contract address
   * @param contractFunction contract function
   * @param args contract function arguments
   * @return future of contract result or error
   */
  ResultOrErrorFuture<ContractResult> query(ContractAddress contractAddress,
      ContractFunction contractFunction, Object... args);

}
