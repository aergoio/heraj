/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.parseToBlockHash;
import static hera.util.TransportUtils.parseToTxHash;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.BigNumber;
import hera.api.model.ContractAddress;
import hera.api.model.Event;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import types.Blockchain;

public class EventConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ObjectMapper mapper = getObjectMapper();

  protected final ModelConverter<AccountAddress, com.google.protobuf.ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function1<Event, Blockchain.Event> domainConverter =
      new Function1<Event, Blockchain.Event>() {

        @Override
        public Blockchain.Event apply(final Event domainEvent) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Blockchain.Event, Event> rpcConverter =
      new Function1<Blockchain.Event, Event>() {

        @Override
        public Event apply(Blockchain.Event rpcEvent) {
          logger.trace("Rpc event: {}", rpcEvent);

          try {
            final ContractAddress contractAddress = accountAddressConverter
                .convertToDomainModel(rpcEvent.getContractAddress()).adapt(ContractAddress.class);
            final CollectionType listType =
                mapper.getTypeFactory().constructCollectionType(List.class, Object.class);
            final List<Object> deserializedArgs =
                mapper.readValue(rpcEvent.getJsonArgs(), listType);
            final Event domainEvent = Event.newBuilder()
                .from(contractAddress)
                .name(rpcEvent.getEventName())
                .args(deserializedArgs)
                .index(rpcEvent.getEventIdx())
                .txHash(parseToTxHash(rpcEvent.getTxHash()))
                .indexInBlock(rpcEvent.getTxIndex())
                .blockHash(parseToBlockHash(rpcEvent.getBlockHash()))
                .blockNumber(rpcEvent.getBlockNo())
                .build();
            logger.trace("Rpc event converted: {}", domainEvent);
            return domainEvent;
          } catch (Exception e) {
            throw new IllegalArgumentException(e);
          }
        }
      };

  public ModelConverter<Event, Blockchain.Event> create() {
    return new ModelConverter<Event, Blockchain.Event>(domainConverter,
        rpcConverter);
  }

  protected ObjectMapper getObjectMapper() {
    final ObjectMapper objectMapper = new ObjectMapper();

    final SimpleModule simpleModule = new SimpleModule();
    simpleModule.addDeserializer(List.class, new CustomDeserializer());
    objectMapper.registerModule(simpleModule);

    return objectMapper;
  }

  protected class CustomDeserializer extends JsonDeserializer<List<Object>> {

    @Override
    public List<Object> deserialize(JsonParser parser, DeserializationContext context)
        throws IOException {
      final List<Object> ret = new ArrayList<>();

      final JsonNode jsonNode = parser.getCodec().readTree(parser);
      logger.trace("Raw event args: {}", jsonNode);
      if (!(jsonNode instanceof ArrayNode)) {
        throw new IllegalStateException("Event args must be array but was " + jsonNode.getClass());
      }

      final Iterator<JsonNode> it = ((ArrayNode) jsonNode).elements();
      while (it.hasNext()) {
        final JsonNode next = it.next();
        ret.add(parseJsonNode(next));
      }
      logger.trace("Parsed event args: {}", ret);

      return ret;
    }

    protected Object parseJsonNode(final JsonNode jsonNode) {
      Object ret = null;

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
      final List<Object> ret = new ArrayList<>(arrayNode.size());

      final Iterator<JsonNode> it = arrayNode.elements();
      while (it.hasNext()) {
        final JsonNode next = it.next();
        ret.add(next);
      }

      return ret;
    }

    protected Object parseObjectNode(final ObjectNode objectNode) {
      final Map<String, Object> ret = new HashMap<>(objectNode.size());

      if (isAergoBigNum(objectNode)) {
        return parseBignumNode(objectNode);
      }

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
        return new BigNumber(bignumNode.asText());
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }

  }

}
