/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static java.lang.System.getenv;
import static java.util.Collections.unmodifiableMap;

import hera.util.Configuration;
import java.util.HashMap;
import java.util.Map;

public class EnvironmentConfiguration extends AbstractConfiguration implements Configuration {

  @Override
  public Map<String, Object> asMap() {
    return unmodifiableMap(new HashMap<String, Object>(getenv()));
  }

  @Override
  public void define(final String key, final Object value) {
    throw new UnsupportedOperationException("OS Environment could NOT be changed");
  }

  @Override
  public Object getValue(final String key) {
    return getenv(key);
  }

  @Override
  public void remove(final String key) {
    throw new UnsupportedOperationException("OS Environment could NOT be changed");
  }

}
