/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.ContextHolder;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.EmptyContext;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractAergoApi;
import hera.api.AccountOperation;
import hera.api.BlockOperation;
import hera.api.BlockchainOperation;
import hera.api.ContractOperation;
import hera.api.KeyStoreOperation;
import hera.api.TransactionOperation;
import io.grpc.ManagedChannel;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class AergoClient extends AbstractAergoApi implements Closeable {

  @NonNull
  protected Context globalContext;

  protected ContextProvider contextProvider = new ContextProvider() {

    @Override
    public Context get() {
      final Context context = ContextHolder.get(this);
      if (context.equals(EmptyContext.getInstance())) {
        ContextHolder.set(this, globalContext);
      }
      return ContextHolder.get(this);
    }
  };

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final ManagedChannel channel = new ManagedChannelFactory().apply(contextProvider.get());

  @Getter(lazy = true)
  private final AccountOperation accountOperation = resolveInjection(new AccountTemplate());

  @Getter(lazy = true)
  private final KeyStoreOperation keyStoreOperation = resolveInjection(new KeyStoreTemplate());

  @Getter(lazy = true)
  private final BlockOperation blockOperation = resolveInjection(new BlockTemplate());

  @Getter(lazy = true)
  private final BlockchainOperation blockchainOperation =
      resolveInjection(new BlockchainTemplate());

  @Getter(lazy = true)
  private final TransactionOperation transactionOperation =
      resolveInjection(new TransactionTemplate());

  @Getter(lazy = true)
  private final ContractOperation contractOperation = resolveInjection(new ContractTemplate());

  protected <T> T resolveInjection(final T target) {
    if (target instanceof ContextProviderInjectable) {
      ((ContextProviderInjectable) target).setContextProvider(contextProvider);
    }
    if (target instanceof ChannelInjectable) {
      ((ChannelInjectable) target).setChannel(getChannel());
    }
    return target;
  }

  @Override
  public void close() {
    try {
      if (null != getChannel()) {
        getChannel().shutdown().awaitTermination(3, TimeUnit.SECONDS);
      }
    } catch (final Throwable e) {
      logger.info("Fail to close aergo client by {}", e.toString());
    }
  }

}
