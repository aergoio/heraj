/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import hera.util.Configuration;
import java.util.Collections;
import java.util.Map;

public class DummyConfiguration extends AbstractConfiguration {

  public static Configuration getInstance() {
    return new DummyConfiguration();
  }
  
  @Override
  protected Object getValue(String key) {
    return null;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> asMap() {
    return Collections.EMPTY_MAP;
  }

  @Override
  public void define(final String key, final Object value) {
    throw new UnsupportedOperationException();
  }
}
