
/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.Configuration;
import hera.util.conf.InMemoryConfiguration;
import java.util.HashSet;
import java.util.Optional;
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

  protected final Logger logger = getLogger(getClass());

  public static final String SCOPE = "empty";

  protected static Context emptyContext = new EmptyContext();

  public static Context getInstance() {
    return emptyContext;
  }

  @Getter
  protected String scope = EmptyContext.SCOPE;

  @Getter
  protected Configuration configuration = new InMemoryConfiguration(true);

  @Getter
  protected Set<Strategy> strategies = unmodifiableSet(emptySet());

  @Override
  public Context withScope(final String scope) {
    logger.debug("New scope: {}", scope);
    final ContextConc newContext =
        new ContextConc(this, scope, getConfiguration(), getStrategies());
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
        new ContextConc(this, getScope(), newConfiguration, getStrategies());
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public Context withConfiguration(final Configuration configuration) {
    logger.debug("New configuration: {}", configuration);
    final ContextConc newContext =
        new ContextConc(this, getScope(), configuration, getStrategies());
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public Context withoutKey(final String key) {
    return this;
  }

  @Override
  public <StrategyT extends Strategy> Context withStrategy(final StrategyT strategy) {
    logger.debug("New strategy: {}", strategy);
    final ContextConc newContext =
        new ContextConc(this, getScope(), getConfiguration(), new HashSet<>(asList(strategy)));
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public Context withStrategies(final Set<Strategy> strategies) {
    logger.debug("New strategies: {}", strategies);
    final Set<Strategy> newStrategies = new HashSet<>(strategies);
    final ContextConc newContext =
        new ContextConc(this, getScope(), getConfiguration(), newStrategies);
    logger.debug("New context: {}", newContext);
    return newContext;
  }

  @Override
  public <StrategyT extends Strategy> Optional<StrategyT> getStrategy(
      final Class<StrategyT> strategyClass) {
    return Optional.empty();
  }

  @Override
  public <StrategyT extends Strategy> Context withoutStrategy(final Class<StrategyT> strategy) {
    return this;
  }

}
