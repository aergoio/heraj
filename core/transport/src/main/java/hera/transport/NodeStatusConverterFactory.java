/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import hera.api.model.ModuleStatus;
import hera.api.model.NodeStatus;
import hera.exception.HerajException;
import hera.util.ParsingUtils;
import hera.util.ParsingUtils.ScaleUnit;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Rpc;

public class NodeStatusConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ObjectMapper mapper = getObjectMapper();

  protected final Function<NodeStatus, Rpc.SingleBytes> domainConverter = domainNodeStatus -> {
    throw new UnsupportedOperationException();
  };

  protected final Function<Rpc.SingleBytes, NodeStatus> rpcConverter = rpcNodeStatus -> {
    logger.trace("Blockchain status: {}", rpcNodeStatus);
    try {
      return mapper.readValue(rpcNodeStatus.getValue().toByteArray(), NodeStatus.class);
    } catch (Throwable e) {
      throw new HerajException(e);
    }
  };

  public ModelConverter<NodeStatus, Rpc.SingleBytes> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

  protected ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();

    SimpleModule simpleModule = new SimpleModule();
    simpleModule.addDeserializer(NodeStatus.class, new NodeStatusDeserializer());
    objectMapper.registerModule(simpleModule);

    return objectMapper;
  }

  private class NodeStatusDeserializer extends JsonDeserializer<NodeStatus> {

    @SuppressWarnings("unchecked")
    @Override
    public NodeStatus deserialize(JsonParser parser, DeserializationContext context)
        throws IOException {
      final NodeStatus nodeStatus = new NodeStatus();

      ObjectCodec objectCodec = parser.getCodec();
      JsonNode nodeStatusNode = objectCodec.readTree(parser);

      final List<ModuleStatus> moduleStatusList = new ArrayList<>();
      Iterator<String> it = nodeStatusNode.fieldNames();
      while (it.hasNext()) {
        final String moduleName = it.next();

        ModuleStatus moduleStatus = new ModuleStatus();
        moduleStatus.setModuleName(moduleName);

        JsonNode componentStatus = nodeStatusNode.get(moduleName);
        moduleStatus.setStatus(componentStatus.get("status").asText());
        moduleStatus.setProcessedMessageCount(componentStatus.get("acc_processed_msg").asLong());
        moduleStatus.setQueuedMessageCount(componentStatus.get("msg_queue_len").asLong());
        String latencyInStr = componentStatus.get("msg_latency").asText();
        double latency = convertToTime(latencyInStr);
        moduleStatus.setLatencyInMicroseconds(latency);
        moduleStatus.setError(componentStatus.get("error").asText());
        moduleStatus.setActor(mapper.convertValue(componentStatus.get("actor"), Map.class));

        moduleStatusList.add(moduleStatus);
      }
      nodeStatus.setModuleStatus(moduleStatusList);

      return nodeStatus;
    }
  }

  /**
   * Parse {@code val} and convert time in milliseconds.
   *
   * @param val string to parse
   *
   * @return time in milliseconds
   *
   * @throws ParseException Fail to parse
   *
   * @see ParsingUtils#convertToTime(String)
   */
  public static double convertToTime(final String val) throws IOException {
    if (null == val) {
      return 0;
    }

    final String lowerVal = val.toLowerCase();
    for (final ScaleUnit unit : ParsingUtils.INTERVALS) {
      final String units = unit.units;
      if (lowerVal.endsWith(units)) {
        final String digits = lowerVal.substring(0, lowerVal.length() - units.length()).trim();
        return (unit.scale * Double.parseDouble(digits));
      }
    }

    throw new IOException("Can't parse " + val);
  }

}
