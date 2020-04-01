/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class NewContextConc implements NewContext {

  @ToString.Exclude
  protected final transient Logger logger = getLogger(getClass());

  protected final Map<Key<?>, Object> key2Value;

  <T> NewContextConc(final NewContext parent, final Key<T> key, final T value) {
    final Map<Key<?>, Object> map = new HashMap<>();
    if (parent instanceof NewContextConc) {
      final NewContextConc fromParent = (NewContextConc) parent;
      map.putAll(fromParent.key2Value);
    }
    map.put(key, value);
    this.key2Value = Collections.unmodifiableMap(map);
  }

  @Override
  public <T> NewContext withValue(final Key<T> key, final T value) {
    assertNotNull(key, "Key must not null");
    assertNotNull(value, "Value must not null");
    logger.trace("New context with current: {}, key: {}, value: {}", this, key, value);
    return new NewContextConc(this, key, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(final Key<T> key) {
    assertNotNull(key, "Key must not null");
    return (T) key2Value.get(key);
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> T getOrDefault(final Key<T> key, final T defaultValue) {
    assertNotNull(key, "Key must not null");
    final T value = (T) key2Value.get(key);
    return null != value ? value : defaultValue;
  }

}
