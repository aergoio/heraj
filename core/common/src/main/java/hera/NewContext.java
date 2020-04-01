package hera;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface NewContext {

  /**
   * Create an new context with {@code key} and {@code value}.
   *
   * @param key   a key
   * @param value a value
   * @param <T>   a type of value
   * @return an new context with {@code key} and {@code value}.
   */
  <T> NewContext withValue(Key<T> key, T value);

  /**
   * Get a value of {@code key}.
   *
   * @param key a key
   * @param <T> a type of value
   * @return a value
   */
  <T> T get(Key<T> key);

  /**
   * Get a value of {@code key}. If not exists, return {@code defaultValue}.
   *
   * @param key          a key
   * @param defaultValue a default value
   * @param <T>          a type of value
   * @return a value
   */
  <T> T getOrDefault(Key<T> key, T defaultValue);

}
