/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.api.AergoEitherApi;
import hera.strategy.EitherApiStrategy;
import hera.strategy.FailoverStrategy;
import hera.strategy.TransactionStrategy;
import hera.util.Configurable;
import hera.util.Configuration;
import hera.util.conf.DummyConfiguration;
import java.util.HashSet;
import java.util.Optional;
import lombok.Setter;

public class Context {

  @Setter
  protected Configuration configuration = DummyConfiguration.getInstance();

  protected TransactionStrategy transactionStrategy;

  protected FailoverStrategy failoverStrategy;

  protected final HashSet<Strategy> strategies = new HashSet<>();

  protected final HashSet<Strategy> usings = new HashSet<>();

  /**
   * Return {@link AergoEitherApi}.
   *
   * @return AergoApi in context
   */
  public AergoEitherApi api() {
    return getStrategy(EitherApiStrategy.class)
        .map(EitherApiStrategy::getEitherApi)
        .orElseThrow(IllegalStateException::new);
  }

  public Context addStrategy(final Strategy strategy) {
    strategies.add(strategy);
    return this;
  }

  /**
   * Return strategy if exists.
   *
   * @param <StrategyT>   strategy type
   * @param strategyClass strategy class
   *
   * @return strategy matching type
   */
  @SuppressWarnings("unchecked")
  public <StrategyT extends Strategy> Optional<StrategyT> getStrategy(
      final Class<StrategyT> strategyClass) {
    final Optional<StrategyT> using =
        (Optional<StrategyT>) usings.stream().filter(strategyClass::isInstance).findFirst();
    if (using.isPresent()) {
      return using;
    }
    final Optional<StrategyT> unusedOptional =
        (Optional<StrategyT>) strategies.stream().filter(strategyClass::isInstance).findFirst();
    if (unusedOptional.isPresent()) {
      final StrategyT unused = unusedOptional.get();
      strategies.remove(unused);
      if (unused instanceof Configurable) {
        ((Configurable) unused).setConfiguration(configuration);
      }
      if (unused instanceof ContextAware) {
        ((ContextAware) unused).setContext(this);
      }
      usings.add(unused);
    }
    return unusedOptional;
  }

}
