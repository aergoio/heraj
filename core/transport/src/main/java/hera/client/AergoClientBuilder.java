/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.ContextHolder;
import hera.ContextProvider;
import hera.DefaultConstants;
import hera.Strategy;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Time;
import hera.strategy.ConnectStrategy;
import hera.strategy.NettyConnectStrategy;
import hera.strategy.OkHttpConnectStrategy;
import hera.strategy.RetryStrategy;
import hera.strategy.SimpleTimeoutStrategy;
import hera.strategy.TimeoutStrategy;
import hera.strategy.ZipkinTracingStrategy;
import hera.util.Configuration;
import hera.util.conf.InMemoryConfiguration;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class AergoClientBuilder {

  public static final String SCOPE = "global";

  protected static final Map<Class<?>, Strategy> necessaryStrategyMap;

  static {
    final Map<Class<?>, Strategy> map = new HashMap<>();
    map.put(ConnectStrategy.class, new NettyConnectStrategy());
    map.put(TimeoutStrategy.class, new SimpleTimeoutStrategy(DefaultConstants.DEFAULT_TIMEOUT));
    necessaryStrategyMap = Collections.unmodifiableMap(map);
  }

  protected final Logger logger = getLogger(getClass());

  protected final Map<Class<?>, Strategy> strategyMap = new LinkedHashMap<>();

  protected Configuration configuration = new InMemoryConfiguration();

  public AergoClientBuilder addConfiguration(final String key, final String value) {
    configuration.define(key, value);
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
   * Use {@link ZipkinTracingStrategy}.
   *
   * @return an instance of this
   */
  public AergoClientBuilder withTracking() {
    strategyMap.put(ZipkinTracingStrategy.class, new ZipkinTracingStrategy());
    return this;
  }

  /**
   * Set timeout for each request with {@link SimpleTimeoutStrategy}. If timeout is already set,
   * that will be overridden.
   *
   * @param timeout time to time out
   * @param unit time's unit
   *
   * @return an instance of this
   */
  public AergoClientBuilder withTimeout(final long timeout, final TimeUnit unit) {
    strategyMap.put(TimeoutStrategy.class, new SimpleTimeoutStrategy(timeout, unit));
    return this;
  }

  /**
   * Use {@link RetryStrategy}. Default retry count : 0, default retry interval : 5000 milliseconds.
   *
   * @param count retry count. If it is less than 0, set as 0
   * @param interval interval value. If it's less than 0, set as 0
   * @param unit interval unit
   *
   * @return an instance of this
   */
  public AergoClientBuilder withRetry(final int count, final long interval, final TimeUnit unit) {
    strategyMap.put(RetryStrategy.class, new RetryStrategy(count, Time.of(interval, unit)));
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
    necessaryStrategyMap.keySet().stream().filter(c -> !this.strategyMap.containsKey(c))
        .forEach(c -> this.strategyMap.put(c, necessaryStrategyMap.get(c)));
    final Context context = ContextProvider.defaultProvider.get()
        .withStrategies(new HashSet<>(this.strategyMap.values()))
        .withConfiguration(configuration)
        .withScope(AergoClientBuilder.SCOPE);
    logger.info("Global context: {}", context);
    // TODO : remove exporting
    ContextHolder.set(context);
    return context;
  }

}
