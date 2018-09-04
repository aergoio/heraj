/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.api.ContractAsyncOperation;
import hera.api.ContractOperation;
import hera.api.model.Abi;
import hera.api.model.AbiSet;
import hera.api.model.AccountAddress;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.HerajException;
import hera.util.DangerousSupplier;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@RequiredArgsConstructor
public class ContractTemplate implements ContractOperation {
  protected final ContractAsyncOperation contractAsyncOperation;

  public ContractTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public ContractTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(new ContractAsyncTemplate(aergoService));
  }

  @Override
  public ResultOrError<ContractTxReceipt> getReceipt(final ContractTxHash deployTxHash) {
    try {
      return contractAsyncOperation.getReceipt(deployTxHash).get(TIMEOUT, MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<ContractTxHash> deploy(final AccountAddress creator,
      final DangerousSupplier<byte[]> contractCodePayload) {
    try {
      return contractAsyncOperation.deploy(creator, contractCodePayload).get(TIMEOUT, MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<AbiSet> getAbiSet(final AccountAddress contract) {
    try {
      return contractAsyncOperation.getAbiSet(contract).get(TIMEOUT, MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<ContractTxHash> execute(final AccountAddress executor,
      final AccountAddress contract, final Abi abi, final Object... args) {
    try {
      return contractAsyncOperation.execute(executor, contract, abi, args).get(TIMEOUT,
          MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<Object> query(final AccountAddress contract, final Abi abi,
      final Object... args) {
    try {
      return contractAsyncOperation.query(contract, abi, args).get(TIMEOUT, MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

}
