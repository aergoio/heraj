/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.ChainIdHash;
import hera.util.Configuration;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
@ToString(exclude = {"logger"})
@EqualsAndHashCode(exclude = {"logger"})
@RequiredArgsConstructor
public final class ThreadLocalContext implements Context {

  protected final Logger logger = getLogger(getClass());

  protected final Object cabinetKey;

  @Override
  public Context withScope(final String scope) {
    final Context withScope = ContextHolder.get(cabinetKey).withScope(scope);
    ContextHolder.set(cabinetKey, withScope);
    return this;
  }

  @Override
  public String getScope() {
    return ContextHolder.get(cabinetKey).getScope();
  }

  @Override
  public Context popScope() {
    final Context scopePoped = ContextHolder.get(cabinetKey).popScope();
    ContextHolder.set(cabinetKey, scopePoped);
    return this;
  }

  @Override
  public Context withChainIdHash(final ChainIdHash chainIdHash) {
    final Context withChainIdHash = ContextHolder.get(cabinetKey).withChainIdHash(chainIdHash);
    ContextHolder.set(cabinetKey, withChainIdHash);
    return this;
  }

  @Override
  public ChainIdHash getChainIdHash() {
    return ContextHolder.get(cabinetKey).getChainIdHash();
  }

  @Override
  public Context withKeyValue(final String key, final Object value) {
    final Context withKeyValue = ContextHolder.get(cabinetKey).withKeyValue(key, value);
    ContextHolder.set(cabinetKey, withKeyValue);
    return this;
  }

  @Override
  public Context withConfiguration(final Configuration configuration) {
    final Context withConfiguration =
        ContextHolder.get(cabinetKey).withConfiguration(configuration);
    ContextHolder.set(cabinetKey, withConfiguration);
    return this;
  }

  @Override
  public Configuration getConfiguration() {
    return ContextHolder.get(cabinetKey).getConfiguration();
  }

  @Override
  public Context withoutKey(final String key) {
    final Context withKey = ContextHolder.get(cabinetKey).withoutKey(key);
    ContextHolder.set(cabinetKey, withKey);
    return this;
  }

  @Override
  public <StrategyT extends Strategy> Context withStrategy(final StrategyT strategy) {
    final Context withStrategy = ContextHolder.get(cabinetKey).withStrategy(strategy);
    ContextHolder.set(cabinetKey, withStrategy);
    return this;
  }

  @Override
  public Context withStrategies(final Set<Strategy> strategies) {
    final Context withStrategies = ContextHolder.get(cabinetKey).withStrategies(strategies);
    ContextHolder.set(cabinetKey, withStrategies);
    return this;
  }

  @Override
  public <StrategyT extends Strategy> StrategyT getStrategy(final Class<StrategyT> strategyClass) {
    return ContextHolder.get(cabinetKey).getStrategy(strategyClass);
  }

  @Override
  public Set<Strategy> getStrategies() {
    return ContextHolder.get(cabinetKey).getStrategies();
  }

  @Override
  public <StrategyT extends Strategy> Context withoutStrategy(final Class<StrategyT> strategy) {
    final Context withoutStrategies = ContextHolder.get(cabinetKey).withoutStrategy(strategy);
    ContextHolder.set(cabinetKey, withoutStrategies);
    return this;
  }
}
