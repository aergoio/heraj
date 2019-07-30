/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.io.IOException;

@ApiAudience.Public
@ApiStability.Unstable
public interface ContractResult {

  /**
   * Bind contract result to class. It returns null if result is empty or json result is {}.
   *
   * @param <T> return type
   * @param clazz class to bind
   * @return bound class instance. null if result is empty or empty json format
   * @throws IOException when binding error occurred
   */
  <T> T bind(Class<T> clazz) throws IOException;

  /**
   * Use {@link ContractResult#inBytesValue()} instead.
   *
   * @return contract result in a raw bytes
   */
  @Deprecated
  BytesValue getResultInRawBytes();

  /**
   * Get contract result in a bytes value.
   *
   * @return contract result in a bytes value
   */
  BytesValue inBytesValue();

}
