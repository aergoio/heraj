
/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.util.Configuration;
import hera.util.conf.InMemoryConfiguration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
@ToString(exclude = "logger")
@EqualsAndHashCode(exclude = "logger")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmptyContext implements Context {

  public static final String SCOPE = "empty";

  protected static final Context emptyContext = new EmptyContext();

  public static Context getInstance() {
    return emptyContext;
  }

  protected final Logger logger = getLogger(getClass());

  @Getter
  protected final String scope = EmptyContext.SCOPE;

  @Getter
  protected final ChainIdHash chainIdHash = new ChainIdHash(BytesValue.EMPTY);

  @Getter
  protected final Configuration configuration = new InMemoryConfiguration(true);

  @Getter
  protected final Set<Strategy> strategies = unmodifiableSet(Collections.<Strategy>emptySet());

  @Override
  public Context withChainIdHash(final ChainIdHash chainIdHash) {
    logger.debug("New chain id hash: {}", chainIdHash);
    final ContextConc newContext =
        new ContextConc(this, getScope(), chainIdHash, getConfiguration(), getStrategies());
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public Context withScope(final String scope) {
    logger.debug("New scope: {}", scope);
    final ContextConc newContext =
        new ContextConc(this, scope, getChainIdHash(), getConfiguration(), getStrategies());
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public Context popScope() {
    return this;
  }

  @Override
  public Context withKeyValue(final String key, final Object value) {
    logger.debug("Define key: {}, value: {}", key, value);
    final Configuration newConfiguration = new InMemoryConfiguration();
    newConfiguration.define(key, value);
    final ContextConc newContext =
        new ContextConc(this, getScope(), getChainIdHash(), newConfiguration, getStrategies());
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public Context withConfiguration(final Configuration configuration) {
    logger.debug("New configuration: {}", configuration);
    final ContextConc newContext =
        new ContextConc(this, getScope(), getChainIdHash(), configuration, getStrategies());
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public Context withoutKey(final String key) {
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <StrategyT extends Strategy> Context withStrategy(final StrategyT strategy) {
    logger.debug("New strategy: {}", strategy);
    final ContextConc newContext =
        new ContextConc(this, getScope(), getChainIdHash(), getConfiguration(),
            new HashSet<Strategy>(asList(strategy)));
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public Context withStrategies(final Set<Strategy> strategies) {
    logger.debug("New strategies: {}", strategies);
    final Set<Strategy> newStrategies = new HashSet<Strategy>(strategies);
    final ContextConc newContext =
        new ContextConc(this, getScope(), getChainIdHash(), getConfiguration(), newStrategies);
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public <StrategyT extends Strategy> StrategyT getStrategy(
      final Class<StrategyT> strategyClass) {
    return null;
  }

  @Override
  public <StrategyT extends Strategy> Context withoutStrategy(final Class<StrategyT> strategy) {
    return this;
  }

}
