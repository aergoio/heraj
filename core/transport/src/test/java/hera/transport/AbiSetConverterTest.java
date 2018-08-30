/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.AbiSet;
import org.junit.Test;
import types.Blockchain;

public class AbiSetConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<AbiSet, Blockchain.ABI> converter = new AbiSetConverterFactory().create();

    final AbiSet domainAbiSet = new AbiSet();
    final Blockchain.ABI rpcAbiSet = converter.convertToRpcModel(domainAbiSet);
    final AbiSet actualAbiSet = converter.convertToDomainModel(rpcAbiSet);
    assertNotNull(actualAbiSet);
  }

}
