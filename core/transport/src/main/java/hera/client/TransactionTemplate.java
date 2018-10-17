/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.TransactionOperation;
import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import io.grpc.ManagedChannel;

@ApiAudience.Private
@ApiStability.Unstable
public class TransactionTemplate implements TransactionOperation, ChannelInjectable {

  protected Context context;

  protected TransactionEitherTemplate transactionEitherOperation = new TransactionEitherTemplate();

  @Override
  public void setContext(final Context context) {
    this.context = context;
    transactionEitherOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    transactionEitherOperation.injectChannel(channel);
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
  public TxHash send(final AccountAddress sender, final AccountAddress recipient,
      final long amount) {
    return transactionEitherOperation.send(sender, recipient, amount).getResult();
  }

}
