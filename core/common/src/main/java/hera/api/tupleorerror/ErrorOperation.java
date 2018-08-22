/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public interface ErrorOperation<ResultT> {

  Throwable getError();

  /**
   * Get result or throws exception if error exists.
   *
   * @param exceptionSupplier exception supplier
   * @return result if no error
   * @throws X exception to throw when error exists
   */
  default <X extends Throwable> ResultT getOrThrows(Supplier<? extends X> exceptionSupplier)
      throws X {
    if (null == getError()) {
      throw exceptionSupplier.get();
    }
    return (ResultT) this;
  }

  /**
   * Get result or throws exception if error matches.
   *
   * @param exceptionMatcher exception matcher
   * @return result if no error
   * @throws X exception to throw when error exists
   */
  default <X extends Throwable> ResultT throwIfErrorMatch(
      Function<Throwable, ? extends X> exceptionMatcher) throws X {
    if (null == getError()) {
      X t = exceptionMatcher.apply(getError());
      if (null != t) {
        throw t;
      }
    }
    return (ResultT) this;
  }
}
