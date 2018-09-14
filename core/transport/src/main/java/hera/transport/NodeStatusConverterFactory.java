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
import hera.api.model.Time;
import hera.exception.HerajException;
import hera.util.ParsingUtils;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
        moduleStatus.setLatency(convertToTime(componentStatus.get("msg_latency").asText()));
        moduleStatus.setError(componentStatus.get("error").asText());

        moduleStatusList.add(moduleStatus);
      }
      nodeStatus.setModuleStatus(moduleStatusList);

      return nodeStatus;
    }
  }

  /**
   * Parse {@code val} and convert time in microseconds.
   *
   * @param val string to parse
   *
   * @return time in microseconds
   *
   * @throws IOException Fail to parse
   *
   * @see ParsingUtils#convertToTime(String)
   */
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
