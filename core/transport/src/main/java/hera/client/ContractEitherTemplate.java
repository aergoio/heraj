/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static hera.api.tupleorerror.FunctionChain.fail;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.ContractAsyncOperation;
import hera.api.ContractEitherOperation;
import hera.api.encode.Base58WithCheckSum;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractCall;
import hera.api.model.ContractInterface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.RpcException;
import hera.key.AergoKey;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class ContractEitherTemplate implements ContractEitherOperation {
  protected final ContractAsyncOperation contractAsyncOperation;

  public ContractEitherTemplate(final ManagedChannel channel, final Context context) {
    this(newFutureStub(channel), context);
  }

  public ContractEitherTemplate(final AergoRPCServiceFutureStub aergoService,
      final Context context) {
    this(new ContractAsyncTemplate(aergoService, context));
  }

  @Override
  public ResultOrError<ContractTxReceipt> getReceipt(final ContractTxHash deployTxHash) {
    try {
      return contractAsyncOperation.getReceipt(deployTxHash).get(TIMEOUT, MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<ContractTxHash> deploy(final AergoKey key, final AccountAddress creator,
      final long nonce, final Base58WithCheckSum encodedPayload) {
    try {
      return contractAsyncOperation.deploy(key, creator, nonce, encodedPayload).get(TIMEOUT,
          MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<ContractInterface> getContractInterface(
      final ContractAddress contractAddress) {
    try {
      return contractAsyncOperation.getContractInterface(contractAddress).get(TIMEOUT,
          MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<ContractTxHash> execute(final AergoKey key, final AccountAddress executor,
      final long nonce, final ContractCall contractCall) {
    try {
      return contractAsyncOperation.execute(key, executor, nonce, contractCall).get(TIMEOUT,
          MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<ContractResult> query(final ContractCall contractCall) {
    try {
      return contractAsyncOperation.query(contractCall).get(TIMEOUT, MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

}
