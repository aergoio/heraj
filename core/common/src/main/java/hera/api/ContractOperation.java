/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Abi;
import hera.api.model.AbiSet;
import hera.api.model.AccountAddress;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.tupleorerror.ResultOrError;
import hera.util.DangerousSupplier;

public interface ContractOperation {

  /**
   * Get receipt of transaction.
   *
   * @param contractTxHash contract transaction hash
   * @return receipt of transaction or error
   */
  ResultOrError<ContractTxReceipt> getReceipt(ContractTxHash contractTxHash);

  /**
   * Deploy smart contract contract code in payload form encoded with base58.
   *
   * @param creator smart contract creator
   * @param rawContractCode contract code in payload form encoded with base58
   * @return contract definition transaction hash or error
   */
  ResultOrError<ContractTxHash> deploy(AccountAddress creator,
      DangerousSupplier<byte[]> rawContractCode);

  /**
   * Get abi set corresponding to contract address.
   *
   * @param contract contract address
   * @return abi set or error
   */
  ResultOrError<AbiSet> getAbiSet(AccountAddress contract);

  /**
   * Execute the smart contract.
   *
   * @param executor contract executor
   * @param contract contract address
   * @param abi abi
   * @param args contract function arguments
   * @return contract execution transaction hash or error
   */
  ResultOrError<ContractTxHash> execute(AccountAddress executor, AccountAddress contract, Abi abi,
      Object... args);

  /**
   * Query the smart contract state.
   *
   * @param contract contract address
   * @param abi abi
   * @param args contract function arguments
   * @return query result or error
   */
  ResultOrError<Object> query(AccountAddress contract, Abi abi, Object... args);

}
