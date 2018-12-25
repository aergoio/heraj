/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.Collections.unmodifiableSet;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.Configuration;
import hera.util.conf.InMemoryConfiguration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
@ToString(exclude = {"logger", "parent"})
@EqualsAndHashCode(exclude = {"logger", "parent"})
public final class ContextConc implements Context {

  protected final Logger logger = getLogger(getClass());

  protected final Context parent;

  @Getter
  protected final String scope;

  @Getter
  protected final Configuration configuration;

  protected final Set<Strategy> strategies;

  protected ContextConc(final Context parent, final String scope,
      final Configuration configuration, final Set<Strategy> strageties) {
    this.parent = parent;
    this.scope = scope;
    this.configuration = (configuration instanceof InMemoryConfiguration
        && ((InMemoryConfiguration) configuration).isReadOnly()) ? configuration
            : new InMemoryConfiguration(true, configuration);
    this.strategies = unmodifiableSet(strageties);
  }

  @Override
  public Context withScope(final String scope) {
    logger.debug("New scope: {}", scope);
    Context newContext = null;
    if (isScopeBase()) {
      newContext = new ContextConc(this, scope, this.configuration, this.strategies);
    } else {
      final Context newScopeBase = getScopeBase().withScope(scope);
      newContext = new ContextConc(newScopeBase, scope, this.configuration, this.strategies);
    }
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public Context popScope() {
    logger.debug("Pop scope: {}", getScope());
    Context newContext = null;
    if (isScopeBase()) {
      newContext = this.parent;
    } else {
      final Context scopeParent = getScopeParent();
      newContext = new ContextConc(scopeParent,
          scopeParent.getScope(), this.configuration, this.strategies);
    }
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  protected Context getScopeBase() {
    return isScopeBase() ? this : this.parent;
  }

  protected Context getScopeParent() {
    return isScopeBase() ? this.parent : ((ContextConc) this.parent).parent;
  }

  protected boolean isScopeBase() {
    return this.configuration.asMap().isEmpty() && this.strategies.isEmpty();
  }

  @Override
  public Context withKeyValue(final String key, final Object value) {
    logger.debug("Define key: {}, value: {}", key, value);
    final Configuration newConfiguration = new InMemoryConfiguration(this.getConfiguration());
    newConfiguration.define(key, value);
    return withConfiguration(newConfiguration);
  }

  @Override
  public Context withConfiguration(final Configuration configuration) {
    logger.debug("New configuration: {}", configuration);
    final ContextConc newContext =
        new ContextConc(this.parent, this.scope, configuration, this.strategies);
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public Context withoutKey(final String key) {
    logger.debug("Remove key: {}", key);
    if (null == getConfiguration().get(key)) {
      return this;
    }
    final Configuration newConfiguration = new InMemoryConfiguration(this.getConfiguration());
    newConfiguration.remove(key);;
    final ContextConc newContext =
        new ContextConc(this.parent, this.scope, newConfiguration, this.strategies);
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public <StrategyT extends Strategy> Context withStrategy(final StrategyT strategy) {
    logger.debug("New strategy: {}", strategy);
    final Set<Strategy> newStrategies = new HashSet<>(this.strategies);
    if (newStrategies.contains(strategy)) {
      newStrategies.remove(strategy);
    }
    newStrategies.add(strategy);
    return withStrategies(newStrategies);
  }

  @Override
  public Context withStrategies(final Set<Strategy> strategies) {
    logger.debug("New strategies: {}", strategies);
    final ContextConc newContext =
        new ContextConc(this.parent, this.scope, this.configuration, strategies);
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <StrategyT extends Strategy> Optional<StrategyT> getStrategy(
      final Class<StrategyT> strategyClass) {
    StrategyT ret = null;
    for (final Strategy strategy : getStrategies()) {
      if (strategyClass.isInstance(strategy)) {
        ret = (StrategyT) strategy;
        break;
      }
    }
    return (Optional<StrategyT>) Optional.ofNullable(ret);
  }

  @Override
  public <StrategyT extends Strategy> Context withoutStrategy(final Class<StrategyT> strategy) {
    final Set<Strategy> newStrategies = new HashSet<>(this.strategies);
    this.strategies.stream().filter(strategy::isInstance).forEach(newStrategies::remove);
    return new ContextConc(this.parent, this.scope, this.configuration, newStrategies);
  }

  @Override
  public Set<Strategy> getStrategies() {
    final Set<Strategy> strategies = new HashSet<>(this.strategies);
    for (final Strategy strategy : strategies) {
      if (strategy instanceof ContextAware) {
        ((ContextAware) strategy).setContext(this);
      }
    }
    return strategies;
  }

}
