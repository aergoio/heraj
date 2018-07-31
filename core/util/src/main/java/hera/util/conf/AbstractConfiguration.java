/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static hera.util.ValidationUtils.assertFalse;
import static java.util.Arrays.stream;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.util.Configuration;
import hera.util.ParsingUtils;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.slf4j.Logger;

public abstract class AbstractConfiguration implements Configuration {

  protected final Logger logger = getLogger(getClass());

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
    assertFalse(stream(splits).anyMatch(String::isEmpty));
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
      return ofNullable(getSubconfiguration(subconfigurationName))
          .map(subconfiguration -> subconfiguration.get(remainder)).orElse(null);
    }
  }

  @Override
  public Optional<Object> getAsOptional(final String key) {
    return ofNullable(get(key));
  }

  @Override
  public String getAsString(final String key, final String defaultValue) {
    return (String) getAsOptional(key).map(value -> {
      logger.trace("Value type: {}", value.getClass());
      if (value instanceof String) {
        return value;
      } else if (value instanceof CharSequence) {
        return value.toString();
      } else {
        return value.toString();
      }
    }).orElse(defaultValue);
  }

  @Override
  public boolean getAsBoolean(final String key, final boolean defaultValue) {
    return ofNullable(getAsString(key, null))
        .map(ParsingUtils::convertToBoolean)
        .orElse(defaultValue);
  }

  @Override
  public int getAsInt(final String key, final int defaultValue) {
    return ofNullable(getAsString(key, null))
        .map(ParsingUtils::convertToInt)
        .orElse(defaultValue);
  }

  @Override
  public long getAsLong(final String key, final long defaultValue) {
    return ofNullable(getAsString(key, null))
        .map(ParsingUtils::convertToLong)
        .orElse(defaultValue);
  }

  @Override
  public double getAsDouble(final String key, final double defaultValue) {
    return ofNullable(getAsString(key, null))
        .map(ParsingUtils::convertToDouble).orElse(defaultValue);
  }

  @Override
  public List<String> getAsList(final String key) {
    return parse(getAsString(key, null));
  }

  @SuppressWarnings("unchecked")
  protected List<String> parse(final String value) {
    return ofNullable(value)
        .map(v -> stream(v.split(",")).flatMap(v1 -> stream(v1.split("\\r?\\n")))
            .map(String::trim).filter(v2 -> !v2.isEmpty()).collect(toList()))
        .orElse(EMPTY_LIST);
  }
}
