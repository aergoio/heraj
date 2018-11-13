/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.DefaultConstants;
import hera.Strategy;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.strategy.ChannelConfigurationStrategy;
import hera.strategy.ConnectStrategy;
import hera.strategy.NettyConnectStrategy;
import hera.strategy.OkHttpConnectStrategy;
import hera.strategy.SimpleTimeoutStrategy;
import hera.strategy.TimeoutStrategy;
import hera.strategy.ZipkinTracingStrategy;
import hera.util.Configuration;
import hera.util.conf.InMemoryConfiguration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class AergoClientBuilder {

  @SuppressWarnings("rawtypes")
  protected static final Class[] necessaryStrageties =
      new Class[] {ConnectStrategy.class, TimeoutStrategy.class};

  protected static final Map<Class<?>, Strategy> defaultStrategyMap;

  static {
    defaultStrategyMap = new HashMap<>();
    defaultStrategyMap.put(ConnectStrategy.class, new NettyConnectStrategy());
    defaultStrategyMap.put(TimeoutStrategy.class,
        new SimpleTimeoutStrategy(DefaultConstants.DEFAULT_TIMEOUT));
  }

  protected final Logger logger = getLogger(getClass());

  protected final Map<Class<?>, Strategy> strategyMap = new HashMap<>();

  protected Configuration configuration = new InMemoryConfiguration();

  protected Optional<Context> injectedContext = empty();

  /**
   * Provide a context. If this method is invoked, all other builder setting will be ignored.
   *
   * @param context context to bind
   * @return an instance of this
   */
  public AergoClientBuilder withContext(final Context context) {
    logger.debug("Bind a new context: {}", context);
    this.injectedContext = ofNullable(context);
    return this;
  }

  /**
   * Provide an endpoint of aergo server. eg. {@code localhost:7845}
   *
   * @param endpoint aergo chain server endpoint
   * @return an instance of this
   */
  public AergoClientBuilder withEndpoint(final String endpoint) {
    configuration.define("endpoint", endpoint);
    return this;
  }

  /**
   * Use {@link NettyConnectStrategy}. If other {@link ConnectStrategy} is already set, that will be
   * overridden.
   *
   * @return an instance of this
   */
  public AergoClientBuilder withNettyConnect() {
    strategyMap.put(ConnectStrategy.class, new NettyConnectStrategy());
    return this;
  }

  /**
   * Use {@link OkHttpConnectStrategy}. If other {@link ConnectStrategy} is already set, that will
   * be overridden.
   *
   * @return an instance of this
   */
  public AergoClientBuilder withOkHttpConnect() {
    strategyMap.put(ConnectStrategy.class, new OkHttpConnectStrategy());
    return this;
  }

  /**
   * Use {@link ZipkinTracingStrategy}. If other {@link ChannelConfigurationStrategy} is already
   * set, that will be overridden.
   *
   * @return an instance of this
   */
  public AergoClientBuilder withZipkinTracking() {
    strategyMap.put(ChannelConfigurationStrategy.class, new ZipkinTracingStrategy());
    return this;
  }

  /**
   * Set timeout for each request with {@link SimpleTimeoutStrategy}. If timeout is already set,
   * that will be overridden.
   *
   * @return an instance of this
   */
  public AergoClientBuilder withTimeout(final long timeout, final TimeUnit unit) {
    strategyMap.put(TimeoutStrategy.class, new SimpleTimeoutStrategy(timeout, unit));
    return this;
  }

  /**
   * Build {@link AergoClient} with the current context. If necessary strategy is not provided,fill
   * necessary strategy.
   *
   * @return {@link AergoClient}
   */
  public AergoClient build() {
    return new AergoClient(buildContext());
  }

  /**
   * Build {@link AergoEitherClient} with the current context. If necessary strategy is not
   * provided, fill necessary strategy.
   *
   * @return {@link AergoEitherClient}
   */
  public AergoEitherClient buildEither() {
    return new AergoEitherClient(buildContext());
  }

  /**
   * Build {@link AergoAsyncClient} with the current context. If necessary strategy is not provided,
   * fill necessary strategy.
   *
   * @return {@link AergoAsyncClient}
   */
  public AergoAsyncClient buildAsync() {
    return new AergoAsyncClient(buildContext());
  }

  protected Context buildContext() {
    Context context = null;
    if (injectedContext.isPresent()) {
      context = buildContextWithInjected();
    } else {
      context = buildContextByStrategyMap();
    }
    logger.debug("Final context: {}", context);
    return context;
  }

  @SuppressWarnings("unchecked")
  protected Context buildContextWithInjected() {
    final Context context = injectedContext.get();
    Stream.of(necessaryStrageties).filter(s -> !context.isExists(s))
        .forEach(s -> context.addStrategy(defaultStrategyMap.get(s)));
    return context;
  }

  @SuppressWarnings("unchecked")
  protected Context buildContextByStrategyMap() {
    final Context context = new Context();
    strategyMap.forEach((s, i) -> context.addStrategy(i));
    Stream.of(necessaryStrageties).filter(s -> !context.isExists(s))
        .forEach(s -> context.addStrategy(defaultStrategyMap.get(s)));
    context.setConfiguration(configuration);
    return context;
  }

}
