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
import hera.api.function.Function1;
import hera.api.model.ModuleStatus;
import hera.api.model.NodeStatus;
import hera.api.model.internal.Time;
import hera.exception.HerajException;
import hera.util.ParsingUtils;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import types.Rpc;

public class NodeStatusConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ObjectMapper mapper = getObjectMapper();

  protected final Function1<NodeStatus, Rpc.SingleBytes> domainConverter =
      new Function1<NodeStatus, Rpc.SingleBytes>() {

        @Override
        public Rpc.SingleBytes apply(final NodeStatus domainNodeStatus) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Rpc.SingleBytes, NodeStatus> rpcConverter =
      new Function1<Rpc.SingleBytes, NodeStatus>() {

        @Override
        public NodeStatus apply(final Rpc.SingleBytes rpcNodeStatus) {
          logger.trace("Rpc node status: {}", rpcNodeStatus);
          try {
            final byte[] rawNodeStatus = rpcNodeStatus.getValue().toByteArray();
            return rawNodeStatus.length == 0 ? new NodeStatus(new ArrayList<ModuleStatus>())
                : mapper.readValue(rawNodeStatus, NodeStatus.class);
          } catch (Throwable e) {
            throw new HerajException(e);
          }
        }
      };

  public ModelConverter<NodeStatus, Rpc.SingleBytes> create() {
    return new ModelConverter<NodeStatus, Rpc.SingleBytes>(domainConverter, rpcConverter);
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
      ObjectCodec objectCodec = parser.getCodec();
      JsonNode nodeStatusNode = objectCodec.readTree(parser);

      final List<ModuleStatus> moduleStatusList = new ArrayList<ModuleStatus>();
      final Iterator<String> it = nodeStatusNode.fieldNames();
      while (it.hasNext()) {
        final String moduleName = it.next();
        final JsonNode componentStatus = nodeStatusNode.get(moduleName);
        final ModuleStatus moduleStatus = new ModuleStatus(
            moduleName,
            componentStatus.get("status").asText(),
            componentStatus.get("acc_processed_msg").asLong(),
            componentStatus.get("msg_queue_len").asLong(),
            convertToTime(componentStatus.get("msg_latency").asText()),
            componentStatus.get("error").asText(),
            mapper.convertValue(componentStatus.get("actor"), Map.class));
        moduleStatusList.add(moduleStatus);
      }

      return new NodeStatus(moduleStatusList);
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
