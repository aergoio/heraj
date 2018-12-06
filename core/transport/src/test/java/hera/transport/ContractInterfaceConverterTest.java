/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import java.util.ArrayList;
import org.junit.Test;
import types.Blockchain;

public class ContractInterfaceConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<ContractInterface, Blockchain.ABI> converter =
        new ContractInterfaceConverterFactory().create();

    final ContractInterface domainContractInterface = new ContractInterface(
        new ContractAddress(BytesValue.EMPTY), randomUUID().toString(), randomUUID().toString(),
        new ArrayList<ContractFunction>());
    final Blockchain.ABI rpcContractInterface =
        converter.convertToRpcModel(domainContractInterface);
    final ContractInterface actualContractInterface =
        converter.convertToDomainModel(rpcContractInterface);
    assertNotNull(actualContractInterface);
  }

}
