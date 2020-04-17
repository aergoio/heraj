/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;

@ToString
@EqualsAndHashCode
class ContextConc implements Context {

  protected static final Map<Key<?>, Object> EMPTY_KEY_VALUE_MAP = unmodifiableMap(
      new HashMap<Key<?>, Object>());

  @ToString.Exclude
  protected final transient Logger logger = getLogger(getClass());

  protected final String scope;

  protected final Map<Key<?>, Object> key2Value;

  <T> ContextConc(final Context parent, final String scope) {
    this.scope = scope;
    if (parent instanceof ContextConc) {
      final ContextConc fromParent = (ContextConc) parent;
      this.key2Value = fromParent.key2Value;
    } else {
      this.key2Value = EMPTY_KEY_VALUE_MAP;
    }
  }

  <T> ContextConc(final Context parent, final Key<T> key, final T value) {
    this.scope = parent.getScope();
    final Map<Key<?>, Object> map = new HashMap<>();
    if (parent instanceof ContextConc) {
      final ContextConc fromParent = (ContextConc) parent;
      map.putAll(fromParent.key2Value);
    }
    map.put(key, value);
    this.key2Value = unmodifiableMap(map);
  }

  @Override
  public <T> Context withValue(final Key<T> key, final T value) {
    assertNotNull(key, "Key must not null");
    assertNotNull(value, "Value must not null");
    logger.trace("New context with parent: {}, key: {}, value: {}", this, key, value);
    return new ContextConc(this, key, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(final Key<T> key) {
    assertNotNull(key, "Key must not null");
    final T value = (T) key2Value.get(key);
    return value;
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> T getOrDefault(final Key<T> key, final T defaultValue) {
    assertNotNull(key, "Key must not null");
    final T value = (T) this.key2Value.get(key);
    return null != value ? value : defaultValue;
  }

  @Override
  public Context withScope(final String scope) {
    assertNotNull(scope, "Scope must not null");
    logger.trace("New context with parent: {}, scope: {}", this, scope);
    return new ContextConc(this, scope);
  }

  @Override
  public String getScope() {
    return this.scope;
  }

}
