/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import java.util.function.Function;

public interface DangerousFunction<T, R> {
  R apply(T t) throws Exception;

  /**
   * Change DangerousFunction to Function.
   *
   * @return function
   */
  default Function<T, R> toSafe() {
    return t -> {
      try {
        return this.apply(t);
      } catch (final Throwable throwable) {
        throw new IllegalStateException(throwable);
      }
    };
  }
}
