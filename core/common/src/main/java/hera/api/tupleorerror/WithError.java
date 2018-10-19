/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import hera.exception.HerajException;
import hera.util.ExceptionUtils;

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
   * Append current stack trace to an error if error is instance of HerajException. After append,
   * throw it.
   */
  default void appendCurrentStackTraceToError() {
    if (getError() instanceof HerajException) {
      final HerajException cloneError = ((HerajException) getError()).clone();
      cloneError.setStackTrace(ExceptionUtils.concat(new Throwable(), cloneError));
      throw cloneError;
    } else {
      throw new HerajException(getError());
    }
  }

}
