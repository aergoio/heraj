package hera.util;

public interface Adaptor {
  /**
   * Convert to other resource.
   *
   * @param <T> type to convert to
   *
   * @param adaptor class to convert to
   *
   * @return object to be converted if exists. null if isn't
   */
  <T> T adapt(final Class<T> adaptor);
}
