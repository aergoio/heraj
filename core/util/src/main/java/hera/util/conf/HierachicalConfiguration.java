/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import hera.util.Configuration;
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
    int i = 0;
    while (i < configurations.length && null == configurations[i]) {
      ++i;
    }
    if (i >= configurations.length) {
      return new DummyConfiguration();
    }

    Configuration hirarchicalConf = configurations[i];
    while (i < configurations.length) {
      if (null != configurations[i]) {
        hirarchicalConf = new HierachicalConfiguration(hirarchicalConf, configurations[i]);
      }
      ++i;
    }
    return hirarchicalConf;
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
        null != parent ? parent.asMap() : new HashMap<String, Object>();
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
