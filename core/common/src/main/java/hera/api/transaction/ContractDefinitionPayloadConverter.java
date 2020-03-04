/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.ContractDefinition;
import hera.exception.HerajException;
import hera.util.LittleEndianDataOutputStream;
import java.io.ByteArrayOutputStream;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class ContractDefinitionPayloadConverter implements PayloadConverter<ContractDefinition> {

  protected final Logger logger = getLogger(getClass());

  @Override
  public BytesValue convertToPayload(final ContractDefinition contractDefinition) {
    try {
      logger.debug("Convert to payload from {}", contractDefinition);
      final byte[] rawContract = contractDefinition.getDecodedContract().getValue();
      final ByteArrayOutputStream rawStream = new ByteArrayOutputStream();
      final LittleEndianDataOutputStream dataOut = new LittleEndianDataOutputStream(rawStream);
      try {
        dataOut.writeInt(rawContract.length + 4);
        dataOut.write(rawContract);
        if (!contractDefinition.getConstructorArgs().isEmpty()) {
          final String constructorArgs =
              JsonResolver.asJsonArray(contractDefinition.getConstructorArgs());
          dataOut.write(constructorArgs.getBytes());
        }
      } finally {
        dataOut.close();
      }
      return BytesValue.of(rawStream.toByteArray());
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ContractDefinition parseToModel(final BytesValue payload) {
    throw new UnsupportedOperationException();
  }

}
