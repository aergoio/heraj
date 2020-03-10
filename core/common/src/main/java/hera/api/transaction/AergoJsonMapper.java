/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BigNumber;
import hera.api.model.BytesValue;
import hera.api.model.NodeStatus;
import hera.api.transaction.internal.BigNumberDeserializer;
import hera.api.transaction.internal.BigNumberSerializer;
import hera.api.transaction.internal.ListDeserializer;
import hera.api.transaction.internal.NodeStatusDeserializer;
import hera.exception.HerajException;
import java.util.List;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class AergoJsonMapper implements JsonMapper {

  protected static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    final SimpleModule module = new SimpleModule();
    module.addSerializer(BigNumber.class, new BigNumberSerializer());
    module.addDeserializer(BigNumber.class, new BigNumberDeserializer());
    module.addDeserializer(NodeStatus.class, new NodeStatusDeserializer());
    module.addDeserializer(List.class, new ListDeserializer());
    objectMapper.registerModule(module);
  }

  protected final Logger logger = getLogger(getClass());

  @Override
  public BytesValue marshal(final Object value) {
    try {
      logger.debug("Marshal: {}", value);
      final byte[] rawBytes = objectMapper.writer().writeValueAsBytes(value);
      return BytesValue.of(rawBytes);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public <T> T unmarshal(final BytesValue bytesValue, final Class<T> clazz) {
    try {
      logger.debug("Unmarshal {} as '{}'", bytesValue, clazz);
      return objectMapper.readValue(bytesValue.getValue(), clazz);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

}
