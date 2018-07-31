/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Configuration {
  Map<String, Object> asMap();
  
  Configuration getSubconfiguration(String key);

  void define(String key, Object value);

  Object get(String key);

  Optional<Object> getAsOptional(String key);

  String getAsString(String key, String defaultValue);

  boolean getAsBoolean(String key, boolean defaultValue);

  int getAsInt(String key, int defaultValue);

  long getAsLong(String key, long defaultValue);

  double getAsDouble(String key, double defaultValue);

  List<String> getAsList(String key);
}
