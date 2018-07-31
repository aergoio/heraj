/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import java.util.function.Supplier;

public interface DangerousSupplier<T> {
  T get() throws Exception;

  /**
   * Change DangerousSupplier to Supplier.
   *
   * @return supplier
   */
  default Supplier<T> toSafe() {
    return () -> {
      try {
        return this.get();
      } catch (final Throwable throwable) {
        throw new IllegalStateException(throwable);
      }
    };
  }
}
