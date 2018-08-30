/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Abi;
import org.junit.Test;
import types.Blockchain;

public class AbiConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Abi, Blockchain.Function> converter = new AbiConverterFactory().create();

    final Abi domainAbi = new Abi();
    final Blockchain.Function rpcAbi = converter.convertToRpcModel(domainAbi);
    final Abi actualAbi = converter.convertToDomainModel(rpcAbi);
    assertNotNull(actualAbi);
  }

}
