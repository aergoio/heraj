/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static hera.util.ValidationUtils.assertFalse;
import static java.lang.System.getProperties;
import static java.lang.System.getProperty;
import static java.util.Collections.unmodifiableMap;

import hera.util.Configuration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SystemPropertiesConfiguration extends AbstractConfiguration implements Configuration {

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> asMap() {
    final Map<?, ?> map = getProperties();
    return unmodifiableMap(new HashMap<String, Object>((Map<String, String>) map));
  }

  @Override
  public void define(final String key, final Object value) {
    throw new UnsupportedOperationException("JVM Properties could NOT be changed");
  }

  @Override
  public Configuration getSubconfiguration(final String key) {
    final String prefix = key + ".";
    final InMemoryConfiguration subconfiguration = new InMemoryConfiguration();

    Set<String> stringProperties = getProperties().stringPropertyNames();
    for (final String name : stringProperties) {
      if (name.startsWith(prefix)) {
        final String subname = name.substring(prefix.length());
        subconfiguration.define(subname, getProperty(name));
      }
    }
    return subconfiguration;
  }

  @Override
  public Object getValue(final String key) {
    assertFalse(key.contains("."));
    return getProperty(key);
  }

  @Override
  public void remove(final String key) {
    throw new UnsupportedOperationException("JVM Properties could NOT be changed");
  }

}
