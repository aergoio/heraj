/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.DefaultConstants.DEFAULT_ENDPOINT;
import static hera.DefaultConstants.DEFAULT_TIMEOUT;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.Strategy;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.HostnameAndPort;
import hera.strategy.ConnectStrategy;
import hera.strategy.NettyConnectStrategy;
import hera.strategy.SimpleTimeoutStrategy;
import hera.strategy.TimeoutStrategy;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;

/**
 * <p>
 * Build aergo client.
 * </p>
 * Usage
 * <pre>
 *   AergoClient aergoClient = new AergoClientBuilder()
 *           .addStrategy(new NettyConnectStrategy("localhost:7845"))
 *           .addStrategy(new SimpleTimeoutStrategy(10000L))
 *           .build();
 *
 *   AergoEitherClient aergoEitherClient = new AergoClientBuilder()
 *           .addStrategy(new NettyConnectStrategy("localhost:7845"))
 *           .addStrategy(new SimpleTimeoutStrategy(10000L))
 *           .buildEither();
 *
 *   AergoAsyncClient aergoAsyncClient = new AergoClientBuilder()
 *           .addStrategy(new NettyConnectStrategy("localhost:7845"))
 *           .addStrategy(new SimpleTimeoutStrategy(10000L))
 *           .buildAsync();
 * </pre>
 *
 * @author taeiklim
 *
 */
@ApiAudience.Public
@ApiStability.Unstable
public class AergoClientBuilder {

  /**
   * A default context is holding {@link hera.strategy.NettyConnectStrategy} with a endpoint
   * {@code localhost:7845} and holding {@link hera.strategy.SimpleTimeoutStrategy} with 5 seconds
   * timeout.
   */
  @Getter
  protected static Context defaultContext;

  static {
    defaultContext = new Context();
    defaultContext.addStrategy(new NettyConnectStrategy(HostnameAndPort.of(DEFAULT_ENDPOINT)));
    defaultContext.addStrategy(new SimpleTimeoutStrategy(DEFAULT_TIMEOUT));
  }

  @SuppressWarnings("rawtypes")
  @Getter(value = AccessLevel.PROTECTED)
  protected static final Class[] necessaries =
      new Class[] {ConnectStrategy.class, TimeoutStrategy.class};

  protected final Logger logger = getLogger(getClass());

  protected Context context = Context.copyOf(defaultContext);

  public AergoClientBuilder addStrategy(final Strategy strategy) {
    context.addStrategy(strategy);
    return this;
  }

  /**
   * Bind a context.
   *
   * @param context context to bind
   * @return an instance of this
   */
  public AergoClientBuilder bind(final Context context) {
    logger.debug("Bind a new context: {}", context);
    this.context = context;
    return this;
  }

  /**
   * Build {@link AergoClient} with the current context. If necessary strategy is not provided, add
   * necessary strategy from the {@link #defaultContext}.
   *
   * @return {@link AergoClient}
   */
  public AergoClient build() {
    ensureNecessaryStrategy();
    logger.debug("Build client, current context: {}", context);
    return new AergoClient(context);
  }

  /**
   * Build {@link AergoEitherClient} with the current context. If necessary strategy is not
   * provided, add necessary strategy from the {@link #defaultContext}.
   *
   * @return {@link AergoEitherClient}
   */
  public AergoEitherClient buildEither() {
    ensureNecessaryStrategy();
    logger.debug("Build client, current context: {}", context);
    return new AergoEitherClient(context);
  }

  /**
   * Build {@link AergoAsyncClient} with the current context. If necessary strategy is not provided,
   * add necessary strategy from the {@link #defaultContext}.
   *
   * @return {@link AergoAsyncClient}
   */
  public AergoAsyncClient buildAsync() {
    ensureNecessaryStrategy();
    logger.debug("Build client, current context: {}", context);
    return new AergoAsyncClient(context);
  }

  @SuppressWarnings("unchecked")
  protected void ensureNecessaryStrategy() {
    Stream.of(getNecessaries()).filter(s -> !context.getStrategy(s).isPresent()).forEach(s -> {
      final Strategy defaultNessaryStrategy = (Strategy) getDefaultContext().getStrategy(s).get();
      logger.debug("No {} in the context. Add default {}", s, defaultNessaryStrategy);
      addStrategy(defaultNessaryStrategy);
    });
  }

}
