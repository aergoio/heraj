/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

public interface ContractResult {
  /**
   * Bind contract result to class.
   *
   * @param clazz class to bind
   * @return binded class instance
   * @throws Exception when error occurred
   */
  public <T> T bind(Class<T> clazz) throws Exception;
}
