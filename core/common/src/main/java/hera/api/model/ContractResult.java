/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.transaction.AergoJsonMapper;
import hera.api.transaction.JsonMapper;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class ContractResult {

  public static final ContractResult EMPTY = new ContractResult(BytesValue.EMPTY);

  protected static final JsonMapper mapper = new AergoJsonMapper();

  public static ContractResult of(final BytesValue result) {
    return new ContractResult(result);
  }

  @ToString.Exclude
  protected final transient Logger logger = getLogger(getClass());

  protected final BytesValue result;

  private ContractResult(final BytesValue result) {
    assertNotNull(result, "Result must not null");
    this.result = result;
  }

  /**
   * Bind contract result to class. It returns null if result is empty or json result is {}.
   *
   * @param <T>   return type
   * @param clazz class to bind
   * @return bound class instance. null if result is empty or empty json format
   * @throws IOException when binding error occurred
   */
  @SuppressWarnings("unchecked")
  public <T> T bind(final Class<T> clazz) throws IOException {
    final byte[] rawBytes = this.result.getValue();
    final String stringFormat = new String(rawBytes);
    logger.debug("Raw result to bind: {}", stringFormat);
    if (stringFormat.isEmpty() || "{}".equals(stringFormat)) {
      return null;
    }
    return mapper.unmarshal(result, clazz);
  }

  /**
   * Use {@link ContractResult#inBytesValue()} instead.
   *
   * @return contract result in a raw bytes
   */
  @Deprecated
  public BytesValue getResultInRawBytes() {
    return inBytesValue();
  }

  /**
   * Get contract result in a bytes value.
   *
   * @return contract result in a bytes value
   */
  public BytesValue inBytesValue() {
    return this.result;
  }

}
