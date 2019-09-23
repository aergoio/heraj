/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.ValidationUtils.assertNotNull;

import hera.Context;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.ThreadLocalContextProvider;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractAergoApi;
import hera.api.AccountOperation;
import hera.api.BlockOperation;
import hera.api.BlockchainOperation;
import hera.api.ChainIdHashHolder;
import hera.api.ContractOperation;
import hera.api.KeyStoreOperation;
import hera.api.TransactionOperation;
import hera.api.model.ChainIdHash;
import hera.client.internal.ManagedChannelFactory;
import hera.exception.RpcException;
import io.grpc.ManagedChannel;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class AergoClient extends AbstractAergoApi implements ChainIdHashHolder, Closeable {

  protected final ContextProvider contextProvider;

  protected ManagedChannel channel;

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

  /**
   * AergoClient constructor.
   *
   * @param baseContext a client base context
   */
  AergoClient(final Context baseContext) {
    assertNotNull(baseContext, "Base context must not null");
    this.contextProvider = new ThreadLocalContextProvider(baseContext, this);
  }

  protected ManagedChannel getChannel() {
    if (null == this.channel) {
      this.channel = new ManagedChannelFactory().apply(contextProvider.get());
    }
    return this.channel;
  }

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
  public ChainIdHash getCachedChainIdHash() {
    return contextProvider.get().getChainIdHash();
  }

  @Override
  public void cacheChainIdHash(final ChainIdHash chainIdHash) {
    final Context context = contextProvider.get();
    contextProvider.put(context.withChainIdHash(chainIdHash));
  }

  @Override
  public void close() {
    try {
      if (null != this.channel) {
        this.channel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
      }
    } catch (Exception e) {
      logger.info("Fail to close aergo client by {}", e.toString());
      throw new RpcException(e);
    }
  }

}
