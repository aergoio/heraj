/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.api.ContractAsyncOperation;
import hera.api.ContractOperation;
import hera.api.model.Abi;
import hera.api.model.AbiSet;
import hera.api.model.AccountAddress;
import hera.api.model.Hash;
import hera.api.model.Receipt;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.HerajException;
import hera.util.DangerousSupplier;
import io.grpc.ManagedChannel;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
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
  public ResultOrError<Receipt> getReceipt(final Hash hash) {
    try {
      return contractAsyncOperation.getReceipt(hash).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<Hash> deploy(AccountAddress creator, DangerousSupplier<InputStream> bytecode,
      AbiSet abiSet) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResultOrError<AbiSet> getAbiSet(AccountAddress contract) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResultOrError<Abi> getAbiSet(AccountAddress contract, String functionName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResultOrError<Hash> execute(AccountAddress executor, AccountAddress contract, Abi abi,
      Object... args) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResultOrError<Object> query(AccountAddress contract) {
    // TODO Auto-generated method stub
    return null;
  }

}
