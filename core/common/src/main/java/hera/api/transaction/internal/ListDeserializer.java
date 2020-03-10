/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction.internal;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import hera.api.model.BigNumber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;

public class ListDeserializer extends JsonDeserializer<List<?>> {

  protected final Logger logger = getLogger(getClass());

  @Override
  public List<?> deserialize(final JsonParser parser, final DeserializationContext ctxt)
      throws IOException {
    final List<Object> ret = new LinkedList<>();
    final ObjectCodec objectCodec = parser.getCodec();
    final JsonNode jsonNode = objectCodec.readTree(parser);
    logger.trace("Deserialize {} as List", jsonNode);
    final Iterator<JsonNode> it = jsonNode.iterator();
    while (it.hasNext()) {
      final JsonNode next = it.next();
      ret.add(parseJsonNode(next));
    }
    return ret;
  }


  protected Object parseJsonNode(final JsonNode jsonNode) {
    Object ret;

    // null, boolean, string, number
    if (jsonNode.isValueNode()) {
      ret = parseValueNode((ValueNode) jsonNode);
    } else if (jsonNode.isArray()) {
      ret = parseArrayNode((ArrayNode) jsonNode);
    } else if (jsonNode.isObject()) {
      ret = parseObjectNode((ObjectNode) jsonNode);
    } else {
      throw new IllegalArgumentException("Can't process json node " + jsonNode);
    }

    return ret;
  }

  protected Object parseValueNode(final ValueNode valueNode) {
    Object ret;
    logger.trace("Parse ValueNode: {}", valueNode);

    if (valueNode.isNull()) {
      ret = null;
    } else if (valueNode.isBoolean()) {
      ret = valueNode.asBoolean();
    } else if (valueNode.isTextual()) {
      ret = valueNode.asText();
    } else if (valueNode.isNumber()) {
      ret = valueNode.numberValue();
    } else {
      throw new IllegalArgumentException("Can't process " + valueNode);
    }

    return ret;
  }

  protected List<Object> parseArrayNode(final ArrayNode arrayNode) {
    final int size = arrayNode.size();
    logger.trace("Parse ArrayNode (size: {}}", size);

    final List<Object> ret = new ArrayList<>(size);
    final Iterator<JsonNode> it = arrayNode.elements();
    while (it.hasNext()) {
      ret.add(parseJsonNode(it.next()));
    }
    return ret;
  }

  protected Object parseObjectNode(final ObjectNode objectNode) {
    logger.trace("Parse ObjectNode");

    if (isAergoBigNum(objectNode)) {
      logger.trace("ObjectNode is BigNumber: {}", objectNode);
      return parseBignumNode(objectNode);
    }

    final Map<String, Object> ret = new HashMap<>(objectNode.size());
    final Iterator<Entry<String, JsonNode>> it = objectNode.fields();
    while (it.hasNext()) {
      final Entry<String, JsonNode> next = it.next();
      ret.put(next.getKey(), parseJsonNode(next.getValue()));
    }
    return ret;
  }

  protected boolean isAergoBigNum(final ObjectNode objectNode) {
    final JsonNode possiblyBignum = objectNode.get(BigNumber.BIGNUM_JSON_KEY);
    return objectNode.size() == 1 && null != possiblyBignum && possiblyBignum.isTextual();
  }

  protected BigNumber parseBignumNode(final ObjectNode objectNode) {
    try {
      final JsonNode bignumNode = objectNode.get(BigNumber.BIGNUM_JSON_KEY);
      return BigNumber.of(bignumNode.textValue());
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

}
