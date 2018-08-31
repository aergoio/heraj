/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Abi;
import hera.api.model.AbiSet;
import hera.api.model.AccountAddress;
import hera.api.model.Hash;
import hera.api.model.Receipt;
import hera.api.tupleorerror.ResultOrError;
import hera.util.DangerousSupplier;
import java.io.InputStream;

public interface ContractOperation {

  /**
   * Get receipt of transaction.
   *
   * @param hash transaction hash
   * @return receipt of transaction or error
   */
  ResultOrError<Receipt> getReceipt(Hash hash);

  /**
   * Deploy smart contract contract code in payload form encoded with base58.
   *
   * @param creator smart contract creator
   * @param contractCodePayload contract code in payload form encoded with base58
   * @return contract definition transaction hash or error
   */
  ResultOrError<Hash> deploy(AccountAddress creator,
      DangerousSupplier<InputStream> contractCodePayload);

  /**
   * Get abi set corresponding to contract address.
   *
   * @param contract contract address
   * @return abi set or error
   */
  ResultOrError<AbiSet> getAbiSet(AccountAddress contract);

  /**
   * Get abi set corresponding to contract address and function name.
   *
   * @param contract contract address
   * @param functionName function name
   * @return abi or error
   */
  ResultOrError<Abi> getAbi(AccountAddress contract, String functionName);

  /**
   * Execute the smart contract.
   *
   * @param executor contract executor
   * @param contract contract address
   * @param abi abi
   * @param args contract function arguments
   * @return contract definition transaction hash or error
   */
  ResultOrError<Hash> execute(AccountAddress executor, AccountAddress contract, Abi abi,
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
