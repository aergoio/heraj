/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static java.util.Optional.ofNullable;

import hera.util.Configuration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HierachicalConfiguration extends AbstractConfiguration {

  protected final Configuration parent;

  @NonNull
  protected final Configuration configuration;

  public HierachicalConfiguration(final Configuration configuration) {
    this(null, configuration);
  }

  /**
   * Create hierachical configuration.
   * 
   * @param configurations to be composed
   * 
   * @return {@link HierachicalConfiguration}
   */
  public static Configuration create(final Configuration... configurations) {
    return Arrays.stream(configurations).filter(configuration -> null != configuration)
        .reduce((l, r) -> new HierachicalConfiguration(l, r))
        .orElseGet(() -> new DummyConfiguration());
  }

  @Override
  public Configuration getSubconfiguration(final String key) {
    if (null == parent) {
      return configuration.getSubconfiguration(key);
    }
    return HierachicalConfiguration.create(parent.getSubconfiguration(key),
        configuration.getSubconfiguration(key));
  }

  @Override
  public Object getValue(final String key) {
    final Object value = configuration.get(key);
    if (null != value) {
      return value;
    }
    if (null == parent) {
      return null;
    }
    return parent.get(key);
  }

  @Override
  public Map<String, Object> asMap() {
    final Map<String, Object> parentMap =
        ofNullable(parent).map(Configuration::asMap).orElseGet(() -> new HashMap<>());
    parentMap.putAll(configuration.asMap());
    return parentMap;
  }

  @Override
  public void define(final String key, final Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void remove(String key) {
    throw new UnsupportedOperationException();
  }

}
