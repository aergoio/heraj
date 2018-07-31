/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import java.util.function.Consumer;

public interface DangerousConsumer<T> {
  void accept(T t) throws Exception;

  /**
   * Change DangerousConsumer to Consumer.
   *
   * @return consumer
   */
  default Consumer<T> toSafe() {
    return t -> {
      try {
        this.accept(t);
      } catch (final Throwable throwable) {
        throw new IllegalStateException(throwable);
      }
    };
  }
}
