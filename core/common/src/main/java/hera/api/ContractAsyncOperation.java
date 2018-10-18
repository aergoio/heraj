/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.ContextAware;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Base58WithCheckSum;
import hera.api.model.Account;
import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.tupleorerror.ResultOrErrorFuture;

@ApiAudience.Public
@ApiStability.Unstable
public interface ContractAsyncOperation extends ContextAware {

  /**
   * Get receipt of transaction asynchronously.
   *
   * @param contractTxHash contract transaction hash
   * @return future of receipt of transaction or error
   */
  ResultOrErrorFuture<ContractTxReceipt> getReceipt(ContractTxHash contractTxHash);

  /**
   * Deploy smart contract contract code in payload form encoded in base58 with checksum.
   *
   * @param creator smart contract creator
   * @param nonce nonce of {@code creator}
   * @param encodedPayload contract code in payload form encoded in base58 with checksum
   * @return future of contract definition transaction hash or error
   */
  ResultOrErrorFuture<ContractTxHash> deploy(Account creator, long nonce,
      Base58WithCheckSum encodedPayload);

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
   * @param nonce nonce of {@code executor}
   * @param contractInvocation {@link ContractInvocation}
   * @return future of contract execution transaction hash or error
   */
  ResultOrErrorFuture<ContractTxHash> execute(Account executor, long nonce,
      ContractInvocation contractInvocation);

  /**
   * Query the smart contract state by calling smart contract function.
   *
   * @param contractInvocation {@link ContractInvocation}
   * @return future of contract result or error
   */
  ResultOrErrorFuture<ContractResult> query(ContractInvocation contractInvocation);

}
