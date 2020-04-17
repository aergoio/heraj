/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;

// TODO: not yet implemented. need for Recursive BigNumber processing
class MapDeserializer extends JsonDeserializer<Map<?, ?>> {

  protected final Logger logger = getLogger(getClass());

  @Override
  public Map<?, ?> deserialize(final JsonParser parser, final DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    final ObjectCodec objectCodec = parser.getCodec();
    final JsonNode jsonNode = objectCodec.readTree(parser);
    logger.trace("Deserialize {} as Map", jsonNode);
    final Map<String, Object> map = new LinkedHashMap<>();
    return map;
  }

}

