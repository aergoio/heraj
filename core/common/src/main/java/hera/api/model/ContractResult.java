/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

public interface ContractResult {

  /**
   * Bind contract result to class.
   *
   * @param <T> return type
   * @param clazz class to bind
   * @return bound class instance
   * @throws Exception when binding error occured
   */
  <T> T bind(Class<T> clazz) throws Exception;

  /**
   * Get contract result in a raw bytes.
   *
   * @return contract result in a raw bytes
   */
  BytesValue getResultInRawBytes();

}
