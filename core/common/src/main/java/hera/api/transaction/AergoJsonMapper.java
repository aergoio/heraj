/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BigNumber;
import hera.api.model.BytesValue;
import hera.api.model.ModuleStatus;
import hera.api.model.NodeStatus;
import hera.api.model.internal.Time;
import hera.exception.HerajException;
import hera.util.ParsingUtils;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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

  protected static class BigNumberSerializer extends JsonSerializer<BigNumber> {

    protected final Logger logger = getLogger(getClass());

    @Override
    public void serialize(final BigNumber value, final JsonGenerator gen,
        final SerializerProvider serializers) throws IOException, JsonProcessingException {
      logger.trace("Serialize BigNumber: {}", value);
      gen.writeStartObject();
      gen.writeStringField(BigNumber.BIGNUM_JSON_KEY, value.getValue());
      gen.writeEndObject();
    }

  }

  protected static class BigNumberDeserializer extends JsonDeserializer<BigNumber> {

    protected final Logger logger = getLogger(getClass());

    @Override
    public BigNumber deserialize(final JsonParser parser, final DeserializationContext ctxt)
        throws IOException {
      if (logger.isTraceEnabled()) {
        logger.trace("Deserialize {} as BigNumber", new String(parser.getBinaryValue()));
      }
      final Map<String, String> map = parser.readValueAs(new TypeReference<Map<String, String>>() {
      });
      logger.debug("Parsed map: {}", map);
      return BigNumber.of(map);
    }

  }

  protected static class NodeStatusDeserializer extends JsonDeserializer<NodeStatus> {

    @SuppressWarnings("unchecked")
    @Override
    public NodeStatus deserialize(JsonParser parser, DeserializationContext context)
        throws IOException {
      final ObjectCodec objectCodec = parser.getCodec();
      final JsonNode nodeStatusNode = objectCodec.readTree(parser);

      final List<ModuleStatus> moduleStatusList = new ArrayList<ModuleStatus>();
      final Iterator<String> it = nodeStatusNode.fieldNames();
      while (it.hasNext()) {
        final String moduleName = it.next();
        final JsonNode componentStatus = nodeStatusNode.get(moduleName);
        final Map<String, Object> actor = componentStatus.get("actor")
            .traverse(objectCodec)
            .readValueAs(Map.class);
        final ModuleStatus moduleStatus = ModuleStatus.newBuilder()
            .moduleName(moduleName)
            .status(componentStatus.get("status").asText())
            .processedMessageCount(componentStatus.get("acc_processed_msg").asLong())
            .queuedMessageCount(componentStatus.get("msg_queue_len").asLong())
            .latency(convertToTime(componentStatus.get("msg_latency").asText()))
            .error(componentStatus.get("error").asText())
            .actor(null == actor ? new HashMap<String, Object>() : actor)
            .build();
        moduleStatusList.add(moduleStatus);
      }

      return NodeStatus.newBuilder().moduleStatus(moduleStatusList).build();
    }

    protected Time convertToTime(final String val) throws IOException {
      if (null == val) {
        throw new IOException("Can't parse " + val);
      }
      try {
        long time = ParsingUtils.convertToTime(val);
        return Time.of(time, TimeUnit.MICROSECONDS);
      } catch (ParseException e) {
        throw new IOException("Can't parse " + val);
      }
    }
  }

  protected static class MapDeserializer extends JsonDeserializer<Map<?, ?>> {

    protected final Logger logger = getLogger(getClass());

    @Override
    public Map<?, ?> deserialize(final JsonParser parser, final DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      final Map<String, Object> map = new LinkedHashMap<>();
      // TODO: not yet implemented. need for Recursive BigNumber processing
      return map;
    }

  }

}
