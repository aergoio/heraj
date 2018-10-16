/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static hera.api.tupleorerror.FunctionChain.fail;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.TransactionEitherOperation;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.RpcException;
import io.grpc.ManagedChannel;
import java.util.concurrent.TimeUnit;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class TransactionEitherTemplate implements TransactionEitherOperation, ChannelInjectable {

  protected Context context;

  protected TransactionAsyncTemplate transactionAsyncOperation =
      new TransactionAsyncTemplate();

  @Override
  public void setContext(final Context context) {
    this.context = context;
    transactionAsyncOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    transactionAsyncOperation.injectChannel(channel);
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
