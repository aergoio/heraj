/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.Strategy;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AergoApi;
import hera.api.AergoAsyncApi;
import hera.api.AergoEitherApi;
import hera.strategy.ConnectStrategy;
import hera.strategy.LocalSignStrategy;
import hera.strategy.NettyConnectStrategy;
import hera.strategy.SignStrategy;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class AergoClientBuilder {

  @Getter
  protected static Context defaultContext;

  static {
    defaultContext = new Context();
    defaultContext.addStrategy(new NettyConnectStrategy());
    defaultContext.addStrategy(new LocalSignStrategy());
  }

  @SuppressWarnings("rawtypes")
  @Getter(value = AccessLevel.PROTECTED)
  protected static final Class[] necessaries =
      new Class[] {ConnectStrategy.class, SignStrategy.class};

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
   * Build {@link AergoClient} with the current context.
   *
   * @return {@link AergoClient}
   */
  public AergoApi build() {
    ensureNecessaryStrategy();
    logger.debug("Build client, current context: {}", context);
    return new AergoClient(context);
  }

  /**
   * Build {@link AergoEitherClient} with the current context.
   *
   * @return {@link AergoEitherClient}
   */
  public AergoEitherApi buildEither() {
    ensureNecessaryStrategy();
    logger.debug("Build client, current context: {}", context);
    return new AergoEitherClient(context);
  }

  /**
   * Build {@link AergoAsyncClient} with the current context.
   *
   * @return {@link AergoAsyncClient}
   */
  public AergoAsyncApi buildAsync() {
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
