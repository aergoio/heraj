/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class EmptyContext implements Context {

  protected static final Context instance = new EmptyContext();

  public static Context getInstance() {
    return instance;
  }

  @ToString.Exclude
  protected final transient Logger logger = getLogger(getClass());

  protected final String scope = "<<empty>>";

  EmptyContext() {
  }

  @Override
  public <T> Context withValue(final Key<T> key, final T value) {
    assertNotNull(key, "Key must not null");
    assertNotNull(value, "Value must not null");
    logger.trace("New context with parent: {}, key: {}, value: {}", this, key, value);
    return new ContextConc(this, key, value);
  }

  @Override
  public <T> T get(final Key<T> key) {
    assertNotNull(key, "Key must not null");
    return null;
  }

  @Override
  public <T> T getOrDefault(final Key<T> key, final T defaultValue) {
    assertNotNull(key, "Key must not null");
    return defaultValue;
  }

  @Override
  public Context withScope(final String scope) {
    assertNotNull(scope, "Scope must not null");
    logger.trace("New context with parent: {}, scope: {}", this, scope);
    return new ContextConc(this, scope);
  }

  @Override
  public String getScope() {
    return scope;
  }

}
