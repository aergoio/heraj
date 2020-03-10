/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction.internal;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import hera.api.model.BigNumber;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;

public class BigNumberDeserializer extends JsonDeserializer<BigNumber> {

  protected final Logger logger = getLogger(getClass());

  @Override
  public BigNumber deserialize(final JsonParser parser, final DeserializationContext ctxt)
      throws IOException {
    final ObjectCodec objectCodec = parser.getCodec();
    final JsonNode jsonNode = objectCodec.readTree(parser);
    logger.trace("Deserialize {} as BigNumber", jsonNode);
    final Map<String, String> map = jsonNode.traverse(objectCodec)
        .readValueAs(new TypeReference<Map<String, String>>() {
        });
    logger.debug("Parsed map: {}", map);
    return BigNumber.of(map);
  }

}

