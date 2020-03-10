/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction.internal;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import hera.api.model.BigNumber;
import java.io.IOException;
import org.slf4j.Logger;

public class BigNumberSerializer extends JsonSerializer<BigNumber> {

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

