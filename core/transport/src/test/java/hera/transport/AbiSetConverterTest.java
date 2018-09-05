/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ContractInferface;
import org.junit.Test;
import types.Blockchain;

public class AbiSetConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<ContractInferface, Blockchain.ABI> converter = new ContractInterfaceConverterFactory().create();

    final ContractInferface domainAbiSet = new ContractInferface();
    final Blockchain.ABI rpcAbiSet = converter.convertToRpcModel(domainAbiSet);
    final ContractInferface actualAbiSet = converter.convertToDomainModel(rpcAbiSet);
    assertNotNull(actualAbiSet);
  }

}
