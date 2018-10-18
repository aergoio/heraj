/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.ContextAware;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractEitherAergoApi;
import hera.api.AccountEitherOperation;
import hera.api.BlockChainEitherOperation;
import hera.api.BlockEitherOperation;
import hera.api.ContractEitherOperation;
import hera.api.TransactionEitherOperation;
import hera.strategy.ConnectStrategy;
import io.grpc.ManagedChannel;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@ApiAudience.Public
@ApiStability.Unstable
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AergoEitherClient extends AbstractEitherAergoApi implements Closeable, AutoCloseable {

  @NonNull
  protected final Context context;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final ManagedChannel channel = (ManagedChannel) context.getStrategy(ConnectStrategy.class)
      .map(ConnectStrategy::connect).get();

  @Getter(lazy = true)
  private final AccountEitherOperation accountEitherOperation = buildAccountEitherOperation();

  @Getter(lazy = true)
  private final TransactionEitherOperation transactionEitherOperation =
      buildTransactionEitherOperation();

  @Getter(lazy = true)
  private final BlockEitherOperation blockEitherOperation = buildBlockEitherOperation();

  @Getter(lazy = true)
  private final BlockChainEitherOperation blockChainEitherOperation =
      buildBlockChainEitherOperation();

  @Getter(lazy = true)
  private final ContractEitherOperation contractEitherOperation = buildContractEitherOperation();

  protected AccountEitherOperation buildAccountEitherOperation() {
    final AccountEitherOperation accountEitherOperation = new AccountEitherTemplate();
    resolveInjection(accountEitherOperation);
    return accountEitherOperation;
  }

  protected TransactionEitherOperation buildTransactionEitherOperation() {
    final TransactionEitherOperation transactionEitherOperation = new TransactionEitherTemplate();
    resolveInjection(transactionEitherOperation);
    return transactionEitherOperation;
  }

  protected BlockEitherOperation buildBlockEitherOperation() {
    final BlockEitherOperation blockEitherOperation = new BlockEitherTemplate();
    resolveInjection(blockEitherOperation);
    return blockEitherOperation;
  }

  protected BlockChainEitherOperation buildBlockChainEitherOperation() {
    final BlockChainEitherOperation blockchainEitherOperation = new BlockChainEitherTemplate();
    resolveInjection(blockchainEitherOperation);
    return blockchainEitherOperation;
  }

  protected ContractEitherOperation buildContractEitherOperation() {
    final ContractEitherOperation contractEitherOperation = new ContractEitherTemplate();
    resolveInjection(contractEitherOperation);
    return contractEitherOperation;
  }

  protected void resolveInjection(final Object target) {
    if (target instanceof ContextAware) {
      ((ContextAware) target).setContext(context);
    }
    if (target instanceof ChannelInjectable) {
      ((ChannelInjectable) target).injectChannel(getChannel());
    }
  }

  @Override
  public void close() {
    try {
      getChannel().shutdown().awaitTermination(3, TimeUnit.SECONDS);
    } catch (final Throwable e) {
      logger.debug("Fail to close aergo client", e);
    }
  }

}
