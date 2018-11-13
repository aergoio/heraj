/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.Collections.reverse;
import static org.slf4j.LoggerFactory.getLogger;

import hera.util.Configurable;
import hera.util.Configuration;
import hera.util.conf.DummyConfiguration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;

@ToString
@EqualsAndHashCode
public class Context {

  protected final Logger logger = getLogger(getClass());

  @Setter
  protected Configuration configuration = DummyConfiguration.getInstance();

  protected final Set<Strategy> strategies = new LinkedHashSet<>();

  protected final Set<Strategy> usings = new LinkedHashSet<>();

  /**
   * Copy deep.
   *
   * @param source {@link Context} to copy
   * @return copied Context
   */
  public static Context copyOf(final Context source) {
    final Context copy = new Context();
    copy.configuration = source.configuration;
    copy.strategies.addAll(source.strategies);
    copy.usings.addAll(source.usings);
    return copy;
  }

  /**
   * Add strategy to the context.
   *
   * @param strategy strategy to add
   * @return a reference to this object.
   */
  public Context addStrategy(final Strategy strategy) {
    logger.debug("Add strategy: {}", strategy);
    strategies.add(strategy);
    return this;
  }

  /**
   * Find a strategy.
   *
   * @param strategyClass strategy class to find
   * @return true if found. Otherwise, false
   */
  public boolean isExists(final Class<? extends Strategy> strategyClass) {
    return strategies.stream().filter(strategyClass::isInstance).findFirst().isPresent()
        || usings.stream().filter(strategyClass::isInstance).findFirst().isPresent();
  }

  /**
   * Return strategy if exists.
   *
   * @param strategyClass strategy class
   *
   * @return strategy matching type
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <StrategyT extends Strategy> Optional<StrategyT> getStrategy(
      final Class<StrategyT> strategyClass) {
    final Optional<Strategy> using = usings.stream().filter(strategyClass::isInstance).findFirst();
    if (using.isPresent()) {
      logger.debug("The strategy: {} is already used", strategyClass);
      return (Optional<StrategyT>) using;
    }
    final Optional<Strategy> unusedOptional = getReversedList().stream()
        .filter(strategyClass::isInstance).findFirst();
    if (unusedOptional.isPresent()) {
      final Strategy unused = unusedOptional.get();
      strategies.remove(unused);
      if (unused instanceof Configurable) {
        ((Configurable) unused).setConfiguration(configuration);
      }
      if (unused instanceof ContextAware) {
        ((ContextAware) unused).setContext(this);
      }
      logger.debug("Use strategy: {}", unused.getClass());
      usings.add(unused);
    }
    return (Optional<StrategyT>) unusedOptional;
  }

  public Stream<Strategy> listStrategies(final Predicate<? super Strategy> test) {
    return strategies.stream().filter(test);
  }

  protected List<Strategy> getReversedList() {
    final List<Strategy> list = new ArrayList<>(strategies);
    reverse(list);
    return list;
  }

}
