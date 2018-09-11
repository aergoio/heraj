/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import hera.api.model.ContractResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ContractResultImpl implements ContractResult {

  protected final ObjectMapper mapper = new ObjectMapper();

  protected final byte[] result;

  @Override
  public <T> T bind(final Class<T> clazz) throws Exception {
    return mapper.readValue(result, clazz);
  }

  @Override
  public String toString() {
    return new String(result);
  }

}
