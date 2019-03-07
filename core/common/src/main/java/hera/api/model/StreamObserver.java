/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface StreamObserver<T> {

  /**
   * Callback on next value have received.
   *
   * @param value a next value
   */
  void onNext(T value);

  /**
   * Callback on next error have received.
   *
   * @param t an error
   */
  void onError(Throwable t);

  /**
   * Callback on finished.
   */
  void onCompleted();

}
