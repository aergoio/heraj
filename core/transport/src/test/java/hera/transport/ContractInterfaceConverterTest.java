/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ContractInferface;
import org.junit.Test;
import types.Blockchain;

public class ContractInterfaceConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<ContractInferface, Blockchain.ABI> converter =
        new ContractInterfaceConverterFactory().create();

    final ContractInferface domainContractInterface = new ContractInferface();
    final Blockchain.ABI rpcContractInterface =
        converter.convertToRpcModel(domainContractInterface);
    final ContractInferface actualContractInterface =
        converter.convertToDomainModel(rpcContractInterface);
    assertNotNull(actualContractInterface);
  }

}
