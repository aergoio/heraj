/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ContractFunction;
import org.junit.Test;
import types.Blockchain;

public class AbiConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<ContractFunction, Blockchain.Function> converter = new ContractFunctionConverterFactory().create();

    final ContractFunction domainAbi = new ContractFunction();
    final Blockchain.Function rpcAbi = converter.convertToRpcModel(domainAbi);
    final ContractFunction actualAbi = converter.convertToDomainModel(rpcAbi);
    assertNotNull(actualAbi);
  }

}
