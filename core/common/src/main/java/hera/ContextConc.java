/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.Collections.unmodifiableSet;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.ChainIdHash;
import hera.util.Configuration;
import hera.util.conf.InMemoryConfiguration;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
@ToString(exclude = {"logger", "parent"})
public final class ContextConc implements Context {

  protected final Logger logger = getLogger(getClass());

  protected final Context parent;

  @Getter
  protected final String scope;

  @Getter
  protected final ChainIdHash chainIdHash;

  @Getter
  protected final Configuration configuration;

  protected final Set<Strategy> strategies;

  protected ContextConc(final Context parent, final String scope, final ChainIdHash chainIdHash,
      final Configuration configuration, final Set<Strategy> strageties) {
    this.parent = parent;
    this.scope = scope;
    this.chainIdHash = chainIdHash;
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
      newContext =
          new ContextConc(this, scope, this.chainIdHash, this.configuration, this.strategies);
    } else {
      final Context newScopeBase = getScopeBase().withScope(scope);
      newContext = new ContextConc(newScopeBase, scope, this.chainIdHash, this.configuration,
          this.strategies);
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
          scopeParent.getScope(), this.chainIdHash, this.configuration, this.strategies);
    }
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public Context withChainIdHash(final ChainIdHash chainIdHash) {
    logger.debug("New chain id hash: {}", chainIdHash);
    final ContextConc newContext =
        new ContextConc(this.parent, this.scope, chainIdHash, this.configuration, this.strategies);
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
    final Configuration newConfiguration = new InMemoryConfiguration(getConfiguration());
    newConfiguration.define(key, value);
    return withConfiguration(newConfiguration);
  }

  @Override
  public Context withConfiguration(final Configuration configuration) {
    logger.debug("New configuration: {}", configuration);
    final ContextConc newContext =
        new ContextConc(this.parent, this.scope, this.chainIdHash, configuration, this.strategies);
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public Context withoutKey(final String key) {
    logger.debug("Remove key: {}", key);
    if (null == getConfiguration().get(key)) {
      return this;
    }
    final Configuration newConfiguration = new InMemoryConfiguration(getConfiguration());
    newConfiguration.remove(key);;
    final ContextConc newContext =
        new ContextConc(this.parent, this.scope, this.chainIdHash, newConfiguration,
            this.strategies);
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public <StrategyT extends Strategy> Context withStrategy(final StrategyT strategy) {
    logger.debug("New strategy: {}", strategy);
    final Set<Strategy> newStrategies = new HashSet<Strategy>(this.strategies);
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
        new ContextConc(this.parent, this.scope, this.chainIdHash, this.configuration, strategies);
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <StrategyT extends Strategy> StrategyT getStrategy(
      final Class<StrategyT> strategyClass) {
    StrategyT ret = null;
    for (final Strategy strategy : getStrategies()) {
      if (strategyClass.isInstance(strategy)) {
        ret = (StrategyT) strategy;
        break;
      }
    }
    return ret;
  }

  @Override
  public <StrategyT extends Strategy> Context withoutStrategy(final Class<StrategyT> strategy) {
    final Set<Strategy> newStrategies = new HashSet<Strategy>(this.strategies);
    for (final Strategy oldStrategy : this.strategies) {
      if (strategy.isInstance(oldStrategy)) {
        newStrategies.remove(oldStrategy);
      }
    }
    return new ContextConc(this.parent, this.scope, this.chainIdHash, this.configuration,
        newStrategies);
  }

  @Override
  public Set<Strategy> getStrategies() {
    final Set<Strategy> strategies = new HashSet<Strategy>(this.strategies);
    for (final Strategy strategy : strategies) {
      if (strategy instanceof ContextAware) {
        ((ContextAware) strategy).setContext(this);
      }
    }
    return strategies;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((parent == null) ? 0 : parent.hashCode());
    result = prime * result + ((scope == null) ? 0 : scope.hashCode());
    result = prime * result + ((chainIdHash == null) ? 0 : chainIdHash.hashCode());
    result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
    result = prime * result + ((strategies == null) ? 0 : strategies.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (null == obj) {
      return false;
    }
    if (!obj.getClass().equals(getClass())) {
      return false;
    }

    final ContextConc other = (ContextConc) obj;
    // don't use lombok because of it
    if (this.parent != other.parent) {
      return false;
    }

    return ((null == this.chainIdHash) ? (null == other.chainIdHash)
        : (this.chainIdHash.equals(other.chainIdHash)))
        && this.scope.equals(other.scope)
        && this.configuration.equals(other.configuration)
        && this.strategies.equals(other.strategies);
  }

}
