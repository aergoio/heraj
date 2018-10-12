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
import hera.api.model.ContractCall;
import hera.api.model.ContractInterface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.AdaptException;
import hera.key.AergoKey;

@ApiAudience.Public
@ApiStability.Unstable
public interface ContractEitherOperation {

  /**
   * Get receipt of transaction.
   *
   * @param contractTxHash contract transaction hash
   * @return receipt of transaction or error
   */
  ResultOrError<ContractTxReceipt> getReceipt(ContractTxHash contractTxHash);

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
   * @return contract definition transaction hash or error
   */
  ResultOrError<ContractTxHash> deploy(AergoKey key, AccountAddress creator, long nonce,
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
   * @return contract definition transaction hash or error
   */
  @SuppressWarnings("unchecked")
  default ResultOrError<ContractTxHash> deploy(AergoKey key, Account creator, long nonce,
      Base58WithCheckSum encodedPayload) {
    return creator.adapt(AccountAddress.class).map(a -> deploy(key, a, nonce, encodedPayload))
        .orElse(fail(new AdaptException(creator.getClass(), AccountAddress.class)));
  }

  /**
   * Get smart contract interface corresponding to contract address.
   *
   * @param contractAddress contract address
   * @return contract interface or error
   */
  ResultOrError<ContractInterface> getContractInterface(ContractAddress contractAddress);

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
   * @param contractCall {@link ContractCall}
   * @return contract execution transaction hash or error
   */
  ResultOrError<ContractTxHash> execute(AergoKey key, AccountAddress executor, long nonce,
      ContractCall contractCall);

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
   * @param contractCall {@link ContractCall}
   * @return contract execution transaction hash or error
   */
  @SuppressWarnings("unchecked")
  default ResultOrError<ContractTxHash> execute(AergoKey key, Account executor, long nonce,
      ContractCall contractCall) {
    return executor.adapt(AccountAddress.class).map(a -> execute(key, a, nonce, contractCall))
        .orElse(fail(new AdaptException(executor.getClass(), AccountAddress.class)));
  }


  /**
   * Query the smart contract state by calling smart contract function.
   *
   * @param contractCall {@link ContractCall}
   * @return contract result or error
   */
  ResultOrError<ContractResult> query(ContractCall contractCall);

}
