/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.api.ContractAsyncOperation;
import hera.api.ContractOperation;
import hera.api.model.Hash;
import hera.api.model.Receipt;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.HerajException;
import io.grpc.ManagedChannel;
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

}
