/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static hera.api.tupleorerror.FunctionChain.fail;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInferface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.AdaptException;
import hera.util.DangerousSupplier;

@ApiAudience.Public
@ApiStability.Unstable
public interface ContractOperation {

  /**
   * Get receipt of transaction.
   *
   * @param contractTxHash contract transaction hash
   * @return receipt of transaction or error
   */
  ResultOrError<ContractTxReceipt> getReceipt(ContractTxHash contractTxHash);

  /**
   * Deploy smart contract contract code in payload form encoded in base58.
   *
   * @param creator smart contract creator
   * @param rawContractCode contract code in payload form encoded in base58
   * @return contract definition transaction hash or error
   */
  ResultOrError<ContractTxHash> deploy(AccountAddress creator,
      DangerousSupplier<byte[]> rawContractCode);

  /**
   * Deploy smart contract contract code in payload form encoded in base58.
   *
   * @param creator smart contract creator
   * @param rawContractCode contract code in payload form encoded in base58
   * @return contract definition transaction hash or error
   */
  @SuppressWarnings("unchecked")
  default ResultOrError<ContractTxHash> deploy(Account creator,
      DangerousSupplier<byte[]> rawContractCode) {
    return creator.adapt(AccountAddress.class).map(a -> deploy(a, rawContractCode))
        .orElse(fail(new AdaptException(creator.getClass(), AccountAddress.class)));
  }

  /**
   * Get smart contract interface corresponding to contract address.
   *
   * @param contractAddress contract address
   * @return contract interface or error
   */
  ResultOrError<ContractInferface> getContractInterface(ContractAddress contractAddress);

  /**
   * Execute the smart contract.
   *
   * @param executor contract executor
   * @param contractAddress contract address
   * @param contractFunction contract function
   * @param args contract function arguments
   * @return contract execution transaction hash or error
   */
  ResultOrError<ContractTxHash> execute(AccountAddress executor, ContractAddress contractAddress,
      ContractFunction contractFunction, Object... args);

  /**
   * Execute the smart contract.
   *
   * @param executor contract executor
   * @param contractAddress contract address
   * @param contractFunction contract function
   * @param args contract function arguments
   * @return contract execution transaction hash or error
   */
  @SuppressWarnings("unchecked")
  default ResultOrError<ContractTxHash> execute(Account executor, ContractAddress contractAddress,
      ContractFunction contractFunction, Object... args) {
    return executor.adapt(AccountAddress.class)
        .map(a -> execute(a, contractAddress, contractFunction, args))
        .orElse(fail(new AdaptException(executor.getClass(), AccountAddress.class)));
  }

  /**
   * Query the smart contract state.
   *
   * @param contractAddress contract address
   * @param contractFunction contract function
   * @param args contract function arguments
   * @return contract result or error
   */
  ResultOrError<ContractResult> query(ContractAddress contractAddress,
      ContractFunction contractFunction, Object... args);

}
