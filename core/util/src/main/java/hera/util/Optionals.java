/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import java.util.Optional;
import java.util.function.Function;

public class Optionals {
  /**
   * Conditional type cast.
   *
   * @param <T> Source type
   * @param <U> Target type to cast
   * @param target Target class to cast
   *
   * @return casting function
   */
  public static <T, U> Function<T, Optional<U>> castIf(final Class<U> target) {
    return obj -> {
      if (target.isInstance(obj)) {
        return Optional.of(target.cast(obj));
      } else {
        return Optional.empty();
      }
    };
  }
}
