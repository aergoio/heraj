/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface Subscription<T> {

  /**
   * UnSubscribe current subscription.
   */
  void unsubscribe();

  /**
   * Return whether it's been unsubscribed or not.
   *
   * @return whether it's been unsubscribed or not
   */
  boolean isUnsubscribed();

}
