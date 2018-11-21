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
import lombok.ToString;
import org.slf4j.Logger;

@ToString
@EqualsAndHashCode
public class ContextConc implements Context {

  protected final Logger logger = getLogger(getClass());

  protected Configuration configuration = DummyConfiguration.getInstance();

  protected final Set<Strategy> strategies = new LinkedHashSet<>();

  protected final Set<Strategy> usings = new LinkedHashSet<>();

  /**
   * Copy deep.
   *
   * @param source {@link ContextConc} to copy
   * @return copied Context
   */
  public static ContextConc copyOf(final Context source) {
    final ContextConc copy = new ContextConc();
    copy.configuration = ((ContextConc) source).configuration;
    copy.strategies.addAll(((ContextConc) source).strategies);
    copy.usings.addAll(((ContextConc) source).usings);
    return copy;
  }

  @Override
  public Context setConfiguration(final Configuration configuration) {
    this.configuration = configuration;
    return this;
  }

  @Override
  public ContextConc addStrategy(final Strategy strategy) {
    logger.debug("Add strategy: {}", strategy);
    strategies.add(strategy);
    return this;
  }

  @Override
  public <StrategyT extends Strategy> boolean hasStrategy(Class<StrategyT> strategyClass) {
    return strategies.stream().anyMatch(strategyClass::isInstance)
        || usings.stream().anyMatch(strategyClass::isInstance);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
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

  @Override
  public Stream<Strategy> listStrategies(final Predicate<? super Strategy> test) {
    return Stream.concat(strategies.stream(), usings.stream()).filter(test).peek(s -> {
      if (s instanceof Configurable) {
        ((Configurable) s).setConfiguration(configuration);
      }
      if (s instanceof ContextAware) {
        ((ContextAware) s).setContext(this);
      }
    });
  }

  protected List<Strategy> getReversedList() {
    final List<Strategy> list = new ArrayList<>(strategies);
    reverse(list);
    return list;
  }

}
