/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static hera.util.ValidationUtils.assertFalse;
import static java.util.Collections.EMPTY_LIST;
import static org.slf4j.LoggerFactory.getLogger;

import hera.util.Configuration;
import hera.util.ParsingUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;

public abstract class AbstractConfiguration implements Configuration {

  protected final transient Logger logger = getLogger(getClass());

  @Getter
  protected final boolean readOnly;

  public AbstractConfiguration() {
    this(true);
  }

  public AbstractConfiguration(final boolean readOnly) {
    this.readOnly = readOnly;
  }

  protected String[] getFragments(final String qualifiedName) {
    final String[] splits = qualifiedName.split("\\.");
    for (final String split : splits) {
      if (split.isEmpty()) {
        assertFalse(true, "Value must not empty");
      }
    }
    return splits;
  }

  public Configuration getSubconfiguration(String key) {
    return null;
  }

  protected abstract Object getValue(String key);

  /**
   * get configuration value.
   */
  public Object get(final String key) {
    int dotIndex = key.indexOf('.');
    if (dotIndex < 0) {
      return getValue(key);
    } else {
      final String subconfigurationName = key.substring(0, dotIndex);
      final String remainder = key.substring(dotIndex + 1);
      final Configuration subConfiguration = getSubconfiguration(subconfigurationName);
      if (null != subConfiguration) {
        return subConfiguration.get(remainder);
      }
      return null;
    }
  }

  @Override
  public String getAsString(final String key, final String defaultValue) {
    Object value = get(key);
    if (null == value) {
      return defaultValue;
    }
    logger.trace("Value type: {}", value.getClass());
    if (value instanceof String) {
      return (String) value;
    } else if (value instanceof CharSequence) {
      return value.toString();
    } else {
      return value.toString();
    }
  }

  @Override
  public boolean getAsBoolean(final String key, final boolean defaultValue) {
    final String value = getAsString(key, null);
    return null != value ? ParsingUtils.convertToBoolean(value) : defaultValue;
  }

  @Override
  public int getAsInt(final String key, final int defaultValue) {
    final String value = getAsString(key, null);
    return null != value ? ParsingUtils.convertToInt(value) : defaultValue;
  }

  @Override
  public long getAsLong(final String key, final long defaultValue) {
    final String value = getAsString(key, null);
    return null != value ? ParsingUtils.convertToLong(value) : defaultValue;
  }

  @Override
  public double getAsDouble(final String key, final double defaultValue) {
    final String value = getAsString(key, null);
    return null != value ? ParsingUtils.convertToDouble(value) : defaultValue;
  }

  @Override
  public List<String> getAsList(final String key) {
    return parse(getAsString(key, null));
  }

  @SuppressWarnings("unchecked")
  protected List<String> parse(final String value) {
    if (null == value) {
      return EMPTY_LIST;
    }
    final List<String> list = new ArrayList<String>();
    final String[] splits = value.split("[,|\\r|\\n]");
    for (final String split : splits) {
      final String target = split.trim();
      if (null != target && !target.isEmpty()) {
        list.add(target);
      }
    }
    return list;
  }

  protected void checkReadOnly() {
    if (isReadOnly()) {
      throw new UnsupportedOperationException("Configuration is read-only");
    }
  }

}
