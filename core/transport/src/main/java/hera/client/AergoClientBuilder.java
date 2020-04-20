/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_CLIENT_PROVIDER;
import static hera.client.ClientContextKeys.GRPC_CONNECTION_ENDPOINT;
import static hera.client.ClientContextKeys.GRPC_CONNECTION_NEGOTIATION;
import static hera.client.ClientContextKeys.GRPC_CONNECTION_STRATEGY;
import static hera.client.ClientContextKeys.GRPC_FAILOVER_HANDLER_CHAIN;
import static hera.client.ClientContextKeys.GRPC_REQUEST_TIMEOUT;
import static hera.client.ClientContextKeys.GRPC_VALUE_CHAIN_ID_HASH_HOLDER;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.ContextStorage;
import hera.EmptyContext;
import hera.Key;
import hera.WriteSynchronizedContextStorage;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.HostnameAndPort;
import hera.api.model.Time;
import hera.exception.HerajException;
import hera.strategy.NettyConnectStrategy;
import hera.strategy.OkHttpConnectStrategy;
import hera.strategy.PlainTextChannelStrategy;
import hera.strategy.TimeoutStrategy;
import hera.strategy.TlsChannelStrategy;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class AergoClientBuilder implements ClientConfiguer<AergoClientBuilder> {

  protected final transient Logger logger = getLogger(getClass());

  protected Map<Object, Object> key2Value = new HashMap<>();
  protected List<ComparableFailoverHandler> failoverHandlers = new ArrayList<>();

  {
    // add built-in ones
    key2Value.put(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, new ChainIdHashHolder());

    // add built-in ones
    failoverHandlers.add(new InvalidChainIdHashHandler());
  }

  @Override
  public AergoClientBuilder addConfiguration(final String key, final String value) {
    this.key2Value.put(Key.of(key, String.class), value);
    return this;
  }

  @Override
  public AergoClientBuilder withEndpoint(final String endpoint) {
    this.key2Value.put(GRPC_CONNECTION_ENDPOINT, HostnameAndPort.of(endpoint));
    return this;
  }

  @Override
  public AergoClientBuilder withNonBlockingConnect() {
    this.key2Value.put(GRPC_CONNECTION_STRATEGY, new NettyConnectStrategy());
    return this;
  }

  @Override
  public AergoClientBuilder withBlockingConnect() {
    this.key2Value.put(GRPC_CONNECTION_STRATEGY, new OkHttpConnectStrategy());
    return this;
  }

  @Override
  public AergoClientBuilder withPlainText() {
    this.key2Value.put(GRPC_CONNECTION_NEGOTIATION, new PlainTextChannelStrategy());
    return this;
  }

  @Override
  public AergoClientBuilder withTransportSecurity(String serverCommonName, String serverCertPath,
      String clientCertPath, String clientKeyPath) {
    try {
      withTransportSecurity(serverCommonName, new FileInputStream(serverCertPath),
          new FileInputStream(clientCertPath), new FileInputStream(clientKeyPath));
      return this;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public AergoClientBuilder withTransportSecurity(final String serverCommonName,
      final InputStream serverCertInputStream, final InputStream clientCertInputStream,
      final InputStream clientKeyInputStream) {
    this.key2Value.put(GRPC_CONNECTION_NEGOTIATION,
        new TlsChannelStrategy(serverCommonName, serverCertInputStream, clientCertInputStream,
            clientKeyInputStream));
    return this;
  }

  @Override
  public AergoClientBuilder withTimeout(final long timeout, final TimeUnit unit) {
    this.key2Value.put(GRPC_REQUEST_TIMEOUT, new TimeoutStrategy(Time.of(timeout, unit)));
    return this;
  }

  @Override
  public AergoClientBuilder withRetry(int count, long interval, TimeUnit unit) {
    this.failoverHandlers.add(new JustRetryFailoverHandler(count, Time.of(interval, unit)));
    return this;
  }

  /**
   * Build {@link AergoClient} with the current context.
   *
   * @return {@link AergoClient}
   */
  public AergoClient build() {
    final ContextStorage<Context> contextStorage = new WriteSynchronizedContextStorage<>();
    final Context initContext = initContext();
    logger.trace("Init context: {}", initContext);
    contextStorage.put(initContext);

    return new AergoClientImpl(contextStorage);
  }

  @SuppressWarnings("unchecked")
  protected Context initContext() {
    Context context = EmptyContext.getInstance();

    // general context values
    for (final Entry<Object, Object> entry : key2Value.entrySet()) {
      final Key<Object> key = (Key<Object>) entry.getKey();
      context = context.withValue(key, entry.getValue());
    }

    // failover handlers have priority
    final FailoverHandlerChain failoverHandlerChain = new FailoverHandlerChain(failoverHandlers);
    context = context.withValue(GRPC_FAILOVER_HANDLER_CHAIN, failoverHandlerChain);

    // init client provider
    final ClientProvider<GrpcClient> clientProvider = new GrpcClientProvider();
    context = context.withValue(GRPC_CLIENT_PROVIDER, clientProvider);

    return context;
  }

}
