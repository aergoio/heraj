/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static hera.api.tupleorerror.FunctionChain.fail;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.TransactionAsyncOperation;
import hera.api.TransactionOperation;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.RpcException;
import io.grpc.ManagedChannel;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class TransactionTemplate implements TransactionOperation {

  protected final TransactionAsyncOperation transactionAsyncOperation;

  public TransactionTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public TransactionTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(new TransactionAsyncTemplate(aergoService));
  }

  @Override
  public ResultOrError<Transaction> getTransaction(final TxHash txHash) {
    try {
      return transactionAsyncOperation.getTransaction(txHash).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<TxHash> commit(final Transaction transaction) {
    try {
      return transactionAsyncOperation.commit(transaction).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<TxHash> send(final Transaction transaction) {
    try {
      return transactionAsyncOperation.send(transaction).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }
}
