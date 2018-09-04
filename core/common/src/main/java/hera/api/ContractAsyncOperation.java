/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Abi;
import hera.api.model.AbiSet;
import hera.api.model.AccountAddress;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.util.DangerousSupplier;
import java.io.InputStream;

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
   * @param contractCodePayload contract code in payload form encoded with base58
   * @return future of contract definition transaction hash or error
   */
  ResultOrErrorFuture<ContractTxHash> deploy(AccountAddress creator,
      DangerousSupplier<InputStream> contractCodePayload);

  /**
   * Get abi set corresponding to contract address.
   *
   * @param contract contract address
   * @return future of abi set or error
   */
  ResultOrErrorFuture<AbiSet> getAbiSet(AccountAddress contract);

  /**
   * Execute the smart contract.
   *
   * @param executor contract executor
   * @param contract contract address
   * @param abi abi
   * @param args contract function arguments
   * @return future of contract execution transaction hash or error
   */
  ResultOrErrorFuture<ContractTxHash> execute(AccountAddress executor, AccountAddress contract,
      Abi abi, Object... args);

  /**
   * Query the smart contract state.
   *
   * @param contract contract address
   * @param abi abi
   * @param args contract function arguments
   * @return future of query result or error
   */
  ResultOrErrorFuture<Object> query(AccountAddress contract, Abi abi, Object... args);

}
