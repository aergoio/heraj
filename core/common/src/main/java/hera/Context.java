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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
   * Return strategy if exists.
   *
   * @param <StrategyT> strategy type
   * @param strategyClass strategy class
   *
   * @return strategy matching type
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <StrategyT extends Strategy> Optional<StrategyT> getStrategy(
      final Class<StrategyT> strategyClass) {
    final Optional<StrategyT> using =
        (Optional<StrategyT>) usings.stream().filter(strategyClass::isInstance).findFirst();
    if (using.isPresent()) {
      logger.debug("The strategy: {} is already used", strategyClass);
      return using;
    }
    final Optional<StrategyT> unusedOptional = (Optional<StrategyT>) getReversedList().stream()
        .filter(strategyClass::isInstance).findFirst();
    if (unusedOptional.isPresent()) {
      final StrategyT unused = unusedOptional.get();
      strategies.remove(unused);
      if (unused instanceof Configurable) {
        ((Configurable) unused).setConfiguration(configuration);
      }
      if (unused instanceof ContextAware) {
        ((ContextAware) unused).setContext(this);
      }
      logger.debug("Use strategy: {}", strategyClass);
      usings.add(unused);
    }
    return unusedOptional;
  }

  protected List<Strategy> getReversedList() {
    final List<Strategy> list = new ArrayList<>(strategies);
    reverse(list);
    return list;
  }

}
