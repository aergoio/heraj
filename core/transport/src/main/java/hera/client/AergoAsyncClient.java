/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.ContextAware;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractAsyncAergoApi;
import hera.api.AccountAsyncOperation;
import hera.api.BlockAsyncOperation;
import hera.api.BlockChainAsyncOperation;
import hera.api.ContractAsyncOperation;
import hera.api.SignAsyncOperation;
import hera.api.TransactionAsyncOperation;
import hera.strategy.ConnectStrategy;
import hera.strategy.SignStrategy;
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
public class AergoAsyncClient extends AbstractAsyncAergoApi implements Closeable, AutoCloseable {

  @NonNull
  protected final Context context;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final ManagedChannel channel = (ManagedChannel) context.getStrategy(ConnectStrategy.class)
      .map(ConnectStrategy::connect).get();

  @Getter(lazy = true)
  private final SignAsyncOperation signAsyncOperation = buildSignAsyncOperation();

  @Getter(lazy = true)
  private final AccountAsyncOperation accountAsyncOperation = buildAccountAsyncOperation();

  @Getter(lazy = true)
  private final TransactionAsyncOperation transactionAsyncOperation =
      buildTransactionAsyncOperation();

  @Getter(lazy = true)
  private final BlockAsyncOperation blockAsyncOperation = buildBlockAsyncOperation();

  @Getter(lazy = true)
  private final BlockChainAsyncOperation blockChainAsyncOperation = buildBlockChainAsyncOperation();

  @Getter(lazy = true)
  private final ContractAsyncOperation contractAsyncOperation = buildContractAsyncOperation();

  protected SignAsyncOperation buildSignAsyncOperation() {
    final SignAsyncOperation signAsyncOperation = context.getStrategy(SignStrategy.class)
        .map(s -> s.getSignOperation()).flatMap(o -> o.adapt(SignAsyncOperation.class)).get();
    resolveInjection(signAsyncOperation);
    return signAsyncOperation;
  }

  protected AccountAsyncOperation buildAccountAsyncOperation() {
    final AccountAsyncOperation accountAsyncOperation = new AccountAsyncTemplate();
    resolveInjection(accountAsyncOperation);
    return accountAsyncOperation;
  }

  protected TransactionAsyncOperation buildTransactionAsyncOperation() {
    final TransactionAsyncOperation transactionAsyncOperation = new TransactionAsyncTemplate();
    resolveInjection(transactionAsyncOperation);
    return transactionAsyncOperation;
  }

  protected BlockAsyncOperation buildBlockAsyncOperation() {
    final BlockAsyncOperation blockAsyncOperation = new BlockAsyncTemplate();
    resolveInjection(blockAsyncOperation);
    return blockAsyncOperation;
  }

  protected BlockChainAsyncOperation buildBlockChainAsyncOperation() {
    final BlockChainAsyncOperation blockchainAsyncOperation = new BlockChainAsyncTemplate();
    resolveInjection(blockchainAsyncOperation);
    return blockchainAsyncOperation;
  }

  protected ContractAsyncOperation buildContractAsyncOperation() {
    final ContractAsyncOperation contractAsyncOperation = new ContractAsyncTemplate();
    resolveInjection(contractAsyncOperation);
    return contractAsyncOperation;
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
