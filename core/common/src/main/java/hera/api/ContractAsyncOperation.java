/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static hera.api.tupleorerror.FunctionChain.fail;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Base58WithCheckSum;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.AdaptException;
import hera.key.AergoKey;

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
   * Deploy smart contract contract code in payload form encoded in base58 with checksum. The key
   * can be null if the context is holding a {@code RemoteSignStrategy}. Then sign is done via a
   * server. Make sure {@code creator} is unlocked in this case. <br>
   * If the context is holding a {@code LocalSignStrategy}, key must be provided. In this case, the
   * {@code creator} may not be unlocked because unlocking via the server is meaningless in this
   * case.
   *
   * @param key key to sign a transaction
   * @param creator smart contract creator
   * @param nonce nonce of {@code creator}
   * @param encodedPayload contract code in payload form encoded in base58 with checksum
   * @return future of contract definition transaction hash or error
   */
  ResultOrErrorFuture<ContractTxHash> deploy(AergoKey key, AccountAddress creator, long nonce,
      Base58WithCheckSum encodedPayload);

  /**
   * Deploy smart contract contract code in payload form encoded in base58 with checksum. The key
   * can be null if the context is holding a {@code RemoteSignStrategy}. Then sign is done via a
   * server. Make sure {@code creator} is unlocked in this case. <br>
   * If the context is holding a {@code LocalSignStrategy}, key must be provided. In this case, the
   * {@code creator} may not be unlocked because unlocking via the server is meaningless in this
   * case.
   *
   * @param key key to sign a transaction
   * @param creator smart contract creator
   * @param nonce nonce of {@code creator}
   * @param encodedPayload contract code in payload form encoded in base58 with checksum
   * @return future of contract definition transaction hash or error
   */
  @SuppressWarnings("unchecked")
  default ResultOrErrorFuture<ContractTxHash> deploy(AergoKey key, Account creator, long nonce,
      Base58WithCheckSum encodedPayload) {
    return creator.adapt(AccountAddress.class).map(a -> deploy(key, a, nonce, encodedPayload))
        .orElse(ResultOrErrorFutureFactory
            .supply(() -> fail(new AdaptException(creator.getClass(), AccountAddress.class))));
  }

  /**
   * Get smart contract interface corresponding to contract address.
   *
   * @param contractAddress contract address
   * @return future of contract interface or error
   */
  ResultOrErrorFuture<ContractInterface> getContractInterface(ContractAddress contractAddress);

  /**
   * Execute the smart contract. The key can be null if the context is holding a
   * {@code RemoteSignStrategy}. Then sign is done via a server. Make sure {@code creator} is
   * unlocked in this case. <br>
   * If the context is holding a {@code LocalSignStrategy}, key must be provided. In this case, the
   * {@code executor} may not be unlocked because unlocking via the server is meaningless in this
   * case.
   *
   * @param key key to sign a transaction
   * @param executor contract executor
   * @param nonce nonce of {@code executor}
   * @param contractInvocation {@link ContractInvocation}
   * @return future of contract execution transaction hash or error
   */
  ResultOrErrorFuture<ContractTxHash> execute(AergoKey key, AccountAddress executor, long nonce,
      ContractInvocation contractInvocation);

  /**
   * Execute the smart contract. The key can be null if the context is holding a
   * {@code RemoteSignStrategy}. Then sign is done via a server. Make sure {@code creator} is
   * unlocked in this case. <br>
   * If the context is holding a {@code LocalSignStrategy}, key must be provided. In this case, the
   * {@code executor} may not be unlocked because unlocking via the server is meaningless in this
   * case.
   *
   * @param key key to sign a transaction
   * @param executor contract executor
   * @param nonce nonce of {@code executor}
   * @param contractInvocation {@link ContractInvocation}
   * @return future of contract execution transaction hash or error
   */
  @SuppressWarnings("unchecked")
  default ResultOrErrorFuture<ContractTxHash> execute(AergoKey key, Account executor, long nonce,
      ContractInvocation contractInvocation) {
    return executor.adapt(AccountAddress.class).map(a -> execute(key, a, nonce, contractInvocation))
        .orElse(ResultOrErrorFutureFactory
            .supply(() -> fail(new AdaptException(executor.getClass(), AccountAddress.class))));
  }

  /**
   * Query the smart contract state by calling smart contract function.
   *
   * @param contractInvocation {@link ContractInvocation}
   * @return future of contract result or error
   */
  ResultOrErrorFuture<ContractResult> query(ContractInvocation contractInvocation);

}
