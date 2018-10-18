/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.ContextAware;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractAergoApi;
import hera.api.AccountOperation;
import hera.api.BlockChainOperation;
import hera.api.BlockOperation;
import hera.api.ContractOperation;
import hera.api.TransactionOperation;
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
public class AergoClient extends AbstractAergoApi implements Closeable, AutoCloseable {

  @NonNull
  protected final Context context;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final ManagedChannel channel = (ManagedChannel) context.getStrategy(ConnectStrategy.class)
      .map(ConnectStrategy::connect).get();

  @Getter(lazy = true)
  private final AccountOperation accountOperation = buildAccountOperation();

  @Getter(lazy = true)
  private final TransactionOperation transactionOperation = buildTransactionOperation();

  @Getter(lazy = true)
  private final BlockOperation blockOperation = buildBlockOperation();

  @Getter(lazy = true)
  private final BlockChainOperation blockChainOperation = buildBlockChainOperation();

  @Getter(lazy = true)
  private final ContractOperation contractOperation = buildContractOperation();

  protected AccountOperation buildAccountOperation() {
    final AccountOperation accountOperation = new AccountTemplate();
    resolveInjection(accountOperation);
    return accountOperation;
  }

  protected TransactionOperation buildTransactionOperation() {
    final TransactionOperation transactionOperation = new TransactionTemplate();
    resolveInjection(transactionOperation);
    return transactionOperation;
  }

  protected BlockOperation buildBlockOperation() {
    final BlockOperation blockOperation = new BlockTemplate();
    resolveInjection(blockOperation);
    return blockOperation;
  }

  protected BlockChainOperation buildBlockChainOperation() {
    final BlockChainOperation blockchainOperation = new BlockChainTemplate();
    resolveInjection(blockchainOperation);
    return blockchainOperation;
  }

  protected ContractOperation buildContractOperation() {
    final ContractOperation contractOperation = new ContractTemplate();
    resolveInjection(contractOperation);
    return contractOperation;
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
