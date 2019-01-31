/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ContractInterface;
import org.junit.Test;
import types.Blockchain;

public class ContractInterfaceConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<ContractInterface, Blockchain.ABI> converter =
        new ContractInterfaceConverterFactory().create();
    final Blockchain.ABI rpcContractInterface = Blockchain.ABI.newBuilder().build();
    final ContractInterface converted =
        converter.convertToDomainModel(rpcContractInterface);
    assertNotNull(converted);
  }

}
