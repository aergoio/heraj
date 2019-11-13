/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.transaction;

import static hera.util.ValidationUtils.assertEquals;
import static hera.util.ValidationUtils.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import hera.api.model.BigNumber;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInvocation;
import hera.api.model.Transaction;
import hera.exception.HerajException;
import hera.spec.AergoSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ContractInvocationInfoExtractor
    implements TransactionInfoExtractor<ContractInvocation> {

  protected static final ObjectReader reader = new ObjectMapper().reader();

  protected final Logger logger = getLogger(getClass());

  @Override
  public ContractInvocation extract(final Transaction transaction) {
    try {
      final JsonNode jsonNode = reader.readTree(transaction.getPayload().getInputStream());
      final JsonNode nameNode = jsonNode.findValue("Name");
      final JsonNode argsNode = jsonNode.findValue("Args");

      final ContractFunction function = parseToContractFunction(nameNode);
      final List<Object> args = parseToList(argsNode);
      final ContractInvocation recovered = ContractInvocation.newBuilder()
          .address(transaction.getRecipient().adapt(ContractAddress.class))
          .function(function)
          .amount(transaction.getAmount())
          .args(args)
          .build();
      return recovered;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  protected ContractFunction parseToContractFunction(final JsonNode jsonNode) {
    final String functionName = jsonNode.asText();
    return new ContractFunction(functionName);
  }

  protected List<Object> parseToList(final JsonNode jsonNode) {
    assertTrue(jsonNode instanceof ArrayNode);

    final List<Object> ret = new ArrayList<>();
    final int size = jsonNode.size();
    for (int i = 0; i < size; ++i) {
      final JsonNode next = jsonNode.get(i);
      Object parsedValue = null;
      if (next instanceof ArrayNode) {
        parsedValue = parseToList((ArrayNode) next);
      } else if (next instanceof ObjectNode) {
        if (null != next.get(AergoSpec.BIGNUM_JSON_KEY)) {
          parsedValue = parseToBigNumber((ObjectNode) next);
        } else {
          parsedValue = parseToMap((ObjectNode) next);
        }
      } else {
        parsedValue = parseValue((ValueNode) next);
      }
      ret.add(parsedValue);
    }
    return ret;
  }

  protected Map<String, Object> parseToMap(final ObjectNode jsonNode) {
    assertTrue(jsonNode instanceof ObjectNode);

    final Map<String, Object> ret = new HashMap<>();
    final Iterator<Entry<String, JsonNode>> it = jsonNode.fields();
    while (it.hasNext()) {
      final Entry<String, JsonNode> nextObjectNode = it.next();
      final String key = nextObjectNode.getKey();
      final JsonNode value = nextObjectNode.getValue();
      Object parsedValue = null;
      if (value instanceof ArrayNode) {
        parsedValue = parseToList((ArrayNode) value);
      } else if (value instanceof ObjectNode) {
        if (null != value.get(AergoSpec.BIGNUM_JSON_KEY)) {
          parsedValue = parseToBigNumber((ObjectNode) value);
        } else {
          parsedValue = parseToMap((ObjectNode) value);
        }
      } else {
        parsedValue = parseValue((ValueNode) value);
      }
      ret.put(key, parsedValue);
    }
    return ret;
  }

  protected BigNumber parseToBigNumber(final ObjectNode jsonNode) {
    assertTrue(jsonNode instanceof ObjectNode);
    assertEquals(1, jsonNode.size());

    final JsonNode bigNumberValueNode = jsonNode.get(AergoSpec.BIGNUM_JSON_KEY);
    if (null == bigNumberValueNode) {
      throw new IllegalArgumentException("JsonNode is not bignum value: " + jsonNode.toString());
    }

    if (!(bigNumberValueNode instanceof TextNode)) {
      throw new IllegalArgumentException(
          "JsonNode value is not bignum value: " + bigNumberValueNode.toString());
    }

    return new BigNumber(bigNumberValueNode.asText());
  }

  protected Object parseValue(final ValueNode valueNode) {
    if (valueNode instanceof BooleanNode) {
      return parseToBoolean(valueNode);
    } else if (valueNode instanceof NullNode) {
      return parseToNull(valueNode);
    } else if (valueNode instanceof NumericNode) {
      return parseToNumber(valueNode);
    } else if (valueNode instanceof TextNode) {
      return parseToString(valueNode);
    } else {
      throw new UnsupportedOperationException(
          "Unsupported node type: " + valueNode.getClass().getName());
    }
  }

  protected String parseToString(final JsonNode jsonNode) {
    return jsonNode.asText();
  }

  protected Number parseToNumber(final JsonNode jsonNode) {
    return ((NumericNode) jsonNode).numberValue();
  }

  protected Boolean parseToBoolean(final JsonNode jsonNode) {
    return jsonNode.asBoolean();
  }

  protected String parseToNull(final JsonNode jsonNode) {
    return null;
  }

}
