/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import hera.api.model.BytesValue;
import hera.api.model.ContractResult;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class ContractResultImpl implements ContractResult {

  protected static final ObjectReader reader = new ObjectMapper().reader();

  protected final Logger logger = getLogger(getClass());

  protected final BytesValue result;

  @Override
  public <T> T bind(final Class<T> clazz) throws IOException {
    final byte[] rawBytes = this.result.getValue();
    final String stringFormat = new String(rawBytes);
    logger.debug("Raw result to bind: {}", stringFormat);
    if (stringFormat.isEmpty() || "{}".equals(stringFormat)) {
      return null;
    }
    return reader.forType(clazz).readValue(rawBytes);
  }

  @Override
  public BytesValue getResultInRawBytes() {
    return result;
  }

  @Override
  public String toString() {
    return new String(result.getValue());
  }

}
