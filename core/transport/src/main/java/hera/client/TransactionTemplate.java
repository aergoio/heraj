/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.TransactionEitherOperation;
import hera.api.TransactionOperation;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class TransactionTemplate implements TransactionOperation {

  protected final TransactionEitherOperation transactionEitherOperation;

  public TransactionTemplate(final ManagedChannel channel, final Context context) {
    this(newFutureStub(channel), context);
  }

  public TransactionTemplate(final AergoRPCServiceFutureStub aergoService, final Context context) {
    this(new TransactionEitherTemplate(aergoService, context));
  }

  @Override
  public Transaction getTransaction(final TxHash txHash) {
    return transactionEitherOperation.getTransaction(txHash).getResult();
  }

  @Override
  public TxHash commit(final Transaction transaction) {
    return transactionEitherOperation.commit(transaction).getResult();
  }

  @Override
  public TxHash send(final Transaction transaction) {
    return transactionEitherOperation.send(transaction).getResult();
  }

}
