/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.BytesValue;
import hera.api.model.ContractResult;
import hera.api.transaction.AergoJsonMapper;
import hera.api.transaction.JsonMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class ContractResultImpl implements ContractResult {

  protected static final JsonMapper mapper = new AergoJsonMapper();

  protected final Logger logger = getLogger(getClass());

  protected final BytesValue result;

  @SuppressWarnings("unchecked")
  @Override
  public <T> T bind(final Class<T> clazz) throws IOException {
    final byte[] rawBytes = this.result.getValue();
    final String stringFormat = new String(rawBytes);
    logger.debug("Raw result to bind: {}", stringFormat);
    if (stringFormat.isEmpty() || "{}".equals(stringFormat)) {
      return null;
    }
    return mapper.unmarshal(result, clazz);
  }

  @Override
  public BytesValue getResultInRawBytes() {
    return inBytesValue();
  }

  @Override
  public BytesValue inBytesValue() {
    return this.result;
  }

  @Override
  public String toString() {
    return result.toString();
  }

}
