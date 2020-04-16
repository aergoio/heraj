/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface Context {

  /**
   * Create an new context with {@code key} and {@code value}.
   *
   * @param key   a key
   * @param value a value
   * @param <T>   a type of value
   * @return an new context with {@code key} and {@code value}.
   */
  <T> Context withValue(Key<T> key, T value);

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

  /**
   * Get a context with a new scope.
   *
   * @param scope a scope
   * @return a context with scope
   */
  Context withScope(String scope);

  /**
   * Get current scope.
   *
   * @return a scope
   */
  String getScope();

}
