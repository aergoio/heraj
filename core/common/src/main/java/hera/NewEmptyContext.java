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
public class NewEmptyContext implements NewContext {

  protected static final NewContext instance = new NewEmptyContext();

  public static NewContext getInstance() {
    return instance;
  }

  @ToString.Exclude
  protected final transient Logger logger = getLogger(getClass());

  NewEmptyContext() {
  }

  @Override
  public <T> NewContext withValue(final Key<T> key, final T value) {
    assertNotNull(key, "Key must not null");
    assertNotNull(value, "Value must not null");
    logger.trace("New context with current: {}, key: {}, value: {}", this, key, value);
    return new NewContextConc(this, key, value);
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

}
