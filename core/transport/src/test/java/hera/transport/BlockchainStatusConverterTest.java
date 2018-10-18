/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BlockchainStatus;
import org.junit.Test;
import types.Rpc;

public class BlockchainStatusConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> converter =
        new BlockchainStatusConverterFactory().create();

    final BlockchainStatus domainBlockchainStatus = new BlockchainStatus();
    final Rpc.BlockchainStatus rpcBlockchainStatus =
        converter.convertToRpcModel(domainBlockchainStatus);
    final BlockchainStatus actualDomainBlockchainStatus =
        converter.convertToDomainModel(rpcBlockchainStatus);
    assertNotNull(actualDomainBlockchainStatus);
  }

}
