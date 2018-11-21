/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.util.Configuration;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Context {

  /**
   * Set configuration to the context.
   *
   * @param configuration configuration to set
   * @return a context with corresponding {@code Configuration}
   */
  Context setConfiguration(Configuration configuration);

  /**
   * Add strategy to the context.
   *
   * @param strategy strategy to add
   * @return a context with corresponding {@code Strategy}
   */
  Context addStrategy(Strategy strategy);

  /**
   * Find a strategy from the context.
   *
   * @param <StrategyT> strategy type
   * @param strategyClass strategy class to find
   * @return true if found. Otherwise, false
   */
  <StrategyT extends Strategy> boolean hasStrategy(Class<StrategyT> strategyClass);

  /**
   * Return strategy if exists.
   *
   * @param <StrategyT> strategy type
   * @param strategyClass strategy class
   * @return strategy matching type
   */
  <StrategyT extends Strategy> Optional<StrategyT> getStrategy(Class<StrategyT> strategyClass);

  /**
   * List all the strategies satisfying test from the context.
   *
   * @param test test predicate
   * @return stream of {@code Strategy} satisfying predicate
   */
  Stream<Strategy> listStrategies(Predicate<? super Strategy> test);

}
