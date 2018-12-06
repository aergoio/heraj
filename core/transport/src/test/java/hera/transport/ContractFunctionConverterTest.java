/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ContractFunction;
import org.junit.Test;
import types.Blockchain;

public class ContractFunctionConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<ContractFunction, Blockchain.Function> converter =
        new ContractFunctionConverterFactory().create();

    final ContractFunction domainContractFunction = new ContractFunction(randomUUID().toString());
    final Blockchain.Function rpcContractFunction =
        converter.convertToRpcModel(domainContractFunction);
    final ContractFunction actualContractFunction =
        converter.convertToDomainModel(rpcContractFunction);
    assertNotNull(actualContractFunction);
  }

}
