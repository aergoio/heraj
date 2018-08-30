/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Abi;
import hera.api.model.AbiSet;
import hera.api.model.AccountAddress;
import hera.api.model.Hash;
import hera.api.model.Receipt;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.util.DangerousSupplier;
import java.io.InputStream;

public interface ContractAsyncOperation {

  /**
   * Get receipt of transaction asynchronously.
   *
   * @param hash transaction hash
   * @return future of receipt of transaction or error
   */
  ResultOrErrorFuture<Receipt> getReceipt(Hash hash);

  /**
   * Deploy smart contract with byte code and abi set.
   *
   * @param creator smart contract creator
   * @param bytecode byte code
   * @param abiSet abi set
   * @return future of contract definition transaction hash or error
   */
  ResultOrErrorFuture<Hash> deploy(AccountAddress creator, DangerousSupplier<InputStream> bytecode,
      AbiSet abiSet);

  /**
   * Get abi set corresponding to contract address.
   *
   * @param contract contract address
   * @return future of abi set or error
   */
  ResultOrErrorFuture<AbiSet> getAbiSet(AccountAddress contract);

  /**
   * Get abi set corresponding to contract address and function name.
   *
   * @param contract contract address
   * @param functionName function name
   * @return future of abi or error
   */
  ResultOrErrorFuture<Abi> getAbiSet(AccountAddress contract, String functionName);

  /**
   * Execute the smart contract.
   *
   * @param executor contract executor
   * @param contract contract address
   * @param abi abi
   * @param args contract function arguments
   * @return future of contract definition transaction hash or error
   */
  ResultOrErrorFuture<Hash> execute(AccountAddress executor, AccountAddress contract, Abi abi,
      Object... args);

  /**
   * Query the smart contract state.
   *
   * @param contract contract address
   * @return future of query result or error
   */
  ResultOrErrorFuture<Object> query(AccountAddress contract);

}
