/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import hera.api.model.ContractDefinition;
import hera.util.Base58Utils;
import org.junit.Test;

public class ContractDefinitionPayloadConverterTest extends AbstractTestCase {

  protected final String encodedContract =
      Base58Utils.encodeWithCheck(new byte[] {ContractDefinition.PAYLOAD_VERSION});

  @Test
  public void testConvertToPayload() {
    final PayloadConverter<ContractDefinition> converter = new ContractDefinitionPayloadConverter();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(encodedContract)
        .constructorArgs("1", "2")
        .build();
    final BytesValue payload = converter.convertToPayload(definition);
    assertNotNull(payload);
  }

}
