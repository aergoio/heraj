/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.ChainIdHash;
import hera.util.Configuration;
import java.util.Set;

@ApiAudience.Public
@ApiStability.Unstable
public interface Context {

  /**
   * Make a context instance with {@code scope}.
   *
   * @param scope a scope
   * @return a context instance with {@code scope}
   */
  Context withScope(String scope);

  /**
   * Get scope of a context.
   * 
   * @return a scope
   */
  String getScope();

  /**
   * Get a context instance without current scope.
   * 
   * @return a context instance without current scope.
   */
  Context popScope();

  /**
   * Make a context instance with {@code chainIdHash}.
   *
   * @param chainIdHash a chain id hash
   * @return a context instance with {@code chainIdHash}
   */
  Context withChainIdHash(ChainIdHash chainIdHash);

  /**
   * Get chain id hash of a context. null if no one.
   * 
   * @return a scope
   */
  ChainIdHash getChainIdHash();

  /**
   * Make a context instance with corresponding {@code key} and a {@code value}.
   *
   * @param key a key
   * @param value a value
   * @return a context instance with a corresponding key-value
   */
  Context withKeyValue(String key, Object value);

  /**
   * Make a context instance with corresponding {@code configuration}.
   *
   * @param configuration a configuration
   * @return a context instance with a corresponding {@code configuration}
   */
  Context withConfiguration(Configuration configuration);

  /**
   * Get a configuration of a context.
   * 
   * @return a configuration
   */
  Configuration getConfiguration();

  /**
   * Get a context instance without configuration matching {@code key}.
   * 
   * @param key a key
   * @return a context instance
   */
  Context withoutKey(String key);

  /**
   * Make a context instance with corresponding {@code strategy}.
   *
   * @param <StrategyT> strategy type
   * @param strategy a strategy
   * @return a context instance with corresponding {@code Strategy}
   */
  <StrategyT extends Strategy> Context withStrategy(StrategyT strategy);

  /**
   * Make a context instance with corresponding {@code strategies}.
   *
   * @param strategies new strategies
   * @return a context instance with corresponding {@code strategies}
   */
  Context withStrategies(Set<Strategy> strategies);

  /**
   * Get a strategy whose matches type {@code strategyClass} from a context.
   *
   * @param <StrategyT> strategy type
   * @param strategyClass a strategy class
   * @return a strategy corresponding {@code strategyClass}. Otherwise, null
   */
  <StrategyT extends Strategy> StrategyT getStrategy(Class<StrategyT> strategyClass);

  /**
   * Get a strategies of a context.
   * 
   * @return a strategies
   */
  Set<Strategy> getStrategies();

  /**
   * Remove strategy from the context.
   *
   * @param <StrategyT> strategy type
   * @param strategy strategy to remove
   * @return a context without corresponding {@code Strategy}
   */
  <StrategyT extends Strategy> Context withoutStrategy(Class<StrategyT> strategy);

}
