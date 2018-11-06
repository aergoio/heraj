/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import hera.exception.HerajException;

public interface WithError {

  /**
   * Get error.
   *
   * @return error
   */
  Throwable getError();

  /**
   * Return if error exists or not.
   *
   * @return error existence
   */
  default boolean hasError() {
    return null != getError();
  }

  /**
   * Throw an error. If an error isn't HerajException, wrap with {@code HerajException}.
   */
  default void throwError() {
    throw getError() instanceof HerajException ? (HerajException) getError()
        : new HerajException(getError());
  }

}
