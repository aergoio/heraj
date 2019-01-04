/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static java.util.Collections.unmodifiableMap;

import hera.util.Configuration;
import java.util.HashMap;
import java.util.Map;

public class InMemoryConfiguration extends AbstractConfiguration {

  protected Map<String, Configuration> subconfigurations = new HashMap<String, Configuration>();

  protected Map<String, Object> key2value = new HashMap<String, Object>();

  public InMemoryConfiguration() {
    this(false);
  }

  public InMemoryConfiguration(final boolean readOnly) {
    this(readOnly, DummyConfiguration.getInstance());
  }

  public InMemoryConfiguration(final Configuration configuration) {
    this(false, configuration);
  }

  public InMemoryConfiguration(final boolean readOnly, final Configuration configuration) {
    this(readOnly, configuration.asMap());
  }

  /**
   * InMemoryConfiguration constructor. If it's set as read-only, {@link #define(String, Object)}
   * would throw {@link UnsupportedOperationException}.
   *
   * @param readOnly whether configuration is read-only or not
   * @param map configuration to copy
   */
  public InMemoryConfiguration(final boolean readOnly, final Map<String, Object> map) {
    super(readOnly);
    this.key2value.putAll(map);
    if (readOnly) {
      this.key2value = unmodifiableMap(this.key2value);
      this.subconfigurations = unmodifiableMap(this.subconfigurations);
    }
  }

  @Override
  public Map<String, Object> asMap() {
    return unmodifiableMap(key2value);
  }

  @Override
  public void define(final String key, final Object value) {
    checkReadOnly();

    logger.debug("{}: {}", key, value);

    final int dotIndex = key.indexOf('.');
    if (dotIndex < 0) {
      key2value.put(key, value);
    } else {
      final String subconfigurationName = key.substring(0, dotIndex);
      final String remainder = key.substring(dotIndex + 1);
      final Configuration subconfiguration = subconfigurations.get(subconfigurationName);
      if (null == subconfiguration) {
        final Configuration newSubconfiguration = new InMemoryConfiguration();
        newSubconfiguration.define(remainder, value);
        subconfigurations.put(subconfigurationName, newSubconfiguration);
      } else {
        subconfiguration.define(remainder, value);
      }
    }
  }

  @Override
  public Configuration getSubconfiguration(final String key) {
    logger.trace("Key: {}", key);
    final Configuration subconfiguration = subconfigurations.get(key);
    if (null == subconfiguration) {
      logger.info("The subconfiguration for {} is null", key);
      logger.debug("Subconfigurations: {}", subconfigurations);
    }
    return subconfiguration;
  }

  @Override
  protected Object getValue(final String key) {
    logger.trace("Key: {}", key);
    return key2value.get(key);
  }

  @Override
  public void remove(final String key) {
    checkReadOnly();
    key2value.remove(key);
  }

  @Override
  public String toString() {
    Map<String, Object> merged = new HashMap<String, Object>();
    merged.putAll(subconfigurations);
    merged.putAll(key2value);
    return merged.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (null == obj || !(obj instanceof InMemoryConfiguration)) {
      return false;
    }
    final InMemoryConfiguration other = (InMemoryConfiguration) obj;
    return this.isReadOnly() == other.isReadOnly() && this.key2value.equals(other.key2value)
        && this.subconfigurations.equals(other.subconfigurations);
  }

}
