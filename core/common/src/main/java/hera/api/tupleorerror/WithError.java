/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

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

}
