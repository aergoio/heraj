/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static hera.api.tupleorerror.FunctionChain.fail;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.ContractAsyncOperation;
import hera.api.ContractEitherOperation;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.RpcException;
import hera.util.DangerousSupplier;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class ContractEitherTemplate implements ContractEitherOperation {
  protected final ContractAsyncOperation contractAsyncOperation;

  public ContractEitherTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public ContractEitherTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(new ContractAsyncTemplate(aergoService));
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
  public ResultOrError<ContractTxHash> deploy(final AccountAddress creator,
      final DangerousSupplier<byte[]> contractCodePayload) {
    try {
      return contractAsyncOperation.deploy(creator, contractCodePayload).get(TIMEOUT, MILLISECONDS);
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
  public ResultOrError<ContractTxHash> execute(final AccountAddress executor,
      final ContractAddress contractAddress, final ContractFunction contractFunction,
      final Object... args) {
    try {
      return contractAsyncOperation.execute(executor, contractAddress, contractFunction, args)
          .get(TIMEOUT, MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<ContractResult> query(final ContractAddress contractAddress,
      final ContractFunction contractFunction, final Object... args) {
    try {
      return contractAsyncOperation.query(contractAddress, contractFunction, args).get(TIMEOUT,
          MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

}
