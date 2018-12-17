/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.databind.ObjectMapper;
import hera.api.model.BytesValue;
import hera.api.model.ContractResult;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class ContractResultImpl implements ContractResult {

  protected final Logger logger = getLogger(getClass());

  protected final ObjectMapper mapper = new ObjectMapper();

  protected final byte[] result;

  @Override
  public <T> T bind(final Class<T> clazz) throws IOException {
    final String stringFormat = new String(result);
    logger.debug("Raw result: {}", stringFormat);
    if (stringFormat.isEmpty() || "{}".equals(stringFormat)) {
      return null;
    }
    return mapper.readValue(result, clazz);
  }

  @Override
  public BytesValue getResultInRawBytes() {
    return BytesValue.of(result);
  }

  @Override
  public String toString() {
    return new String(result);
  }

}
