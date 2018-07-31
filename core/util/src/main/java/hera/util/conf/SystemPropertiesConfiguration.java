/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static hera.util.ValidationUtils.assertFalse;
import static java.lang.System.getProperties;
import static java.lang.System.getProperty;
import static java.util.Collections.unmodifiableMap;

import hera.util.Configuration;
import java.util.Map;

public class SystemPropertiesConfiguration extends AbstractConfiguration implements Configuration {

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> asMap() {
    final Map<?, ?> map = getProperties();
    return unmodifiableMap((Map<String, String>) map);
  }

  @Override
  public void define(final String key, final Object value) {
    throw new UnsupportedOperationException("JVM Properties could NOT be changed");
  }

  @Override
  public Configuration getSubconfiguration(final String key) {
    final String prefix = key + ".";
    final InMemoryConfiguration subconfiguration = new InMemoryConfiguration();

    getProperties().stringPropertyNames().stream().filter(name -> name.startsWith(prefix))
        .forEach(name -> {
          final String subname = name.substring(prefix.length());
          subconfiguration.define(subname, getProperty(name));
        });
    return subconfiguration;
  }

  @Override
  public Object getValue(final String key) {
    assertFalse(key.contains("."));
    return getProperty(key);
  }
}
