/*
 * @copyright defined in LICENSE.txt
 */

package hera.custom;

public interface Adaptor {

  /**
   * get Adaptee for adaptor.
   *
   * @param <AdapteeT> adaptee type
   * @param adapteeClass adaptee type class
   *
   * @return adaptee
   */
  <AdapteeT> AdapteeT adapt(Class<AdapteeT> adapteeClass);
}
