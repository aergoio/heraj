package hera.util;

import java.util.Optional;

public interface Adaptor {
  /**
   * Convert to other resource.
   *
   * @param <T> type to convert to
   *
   * @param adaptor class to convert to
   *
   * @return object to be converted if exists
   */
  <T> Optional<T> adapt(final Class<T> adaptor);
}
