/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BlockchainStatus;
import org.junit.Test;
import types.Rpc;

public class BlockchainConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> converter = new BlockchainConverterFactory()
        .create();

    final BlockchainStatus domainBlockchainStatus = new BlockchainStatus();
    final Rpc.BlockchainStatus rpcBlockchainStatus = converter
        .convertToRpcModel(domainBlockchainStatus);
    final BlockchainStatus actualDomainBlockchainStatus = converter
        .convertToDomainModel(rpcBlockchainStatus);
    assertNotNull(actualDomainBlockchainStatus);
  }

}
