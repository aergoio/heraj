/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

public interface WithError {

  Throwable getError();

  default boolean hasError() {
    return null != getError();
  }

}
