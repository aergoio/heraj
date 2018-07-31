/*
 * @copyright defined in LICENSE.txt
 */

package hera.custom;

import java.util.Optional;

public interface Adaptor {

  /**
   * get Adaptee for adaptor.
   *
   * @param <AdapteeT> adaptee type
   * @param adapteeClass adaptee type class
   *
   * @return adaptee
   */
  <AdapteeT> Optional<AdapteeT> adapt(Class<AdapteeT> adapteeClass);
}
