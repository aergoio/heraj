/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction.internal;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import hera.api.model.ModuleStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Time;
import hera.util.ParsingUtils;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

public class NodeStatusDeserializer extends JsonDeserializer<NodeStatus> {

  protected final Logger logger = getLogger(getClass());

  @SuppressWarnings("unchecked")
  @Override
  public NodeStatus deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    final ObjectCodec objectCodec = parser.getCodec();
    final JsonNode jsonNode = objectCodec.readTree(parser);
    logger.trace("Deserialize {} as NodeStatus", jsonNode);

    final List<ModuleStatus> moduleStatusList = new ArrayList<ModuleStatus>();
    final Iterator<String> it = jsonNode.fieldNames();
    while (it.hasNext()) {
      final String moduleName = it.next();
      final JsonNode componentStatus = jsonNode.get(moduleName);
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

    final NodeStatus nodeStatus = NodeStatus.newBuilder()
        .moduleStatus(moduleStatusList)
        .build();
    logger.debug("Deserialized NodeStatus: {}", nodeStatus);

    return nodeStatus;
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


