/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BlockHash;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import org.junit.Test;
import types.Rpc;

public class BlockchainStatusConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> converter =
        new BlockchainStatusConverterFactory().create();

    final BlockchainStatus domainBlockchainStatus =
        new BlockchainStatus(10L, BlockHash.of(BytesValue.EMPTY));
    final Rpc.BlockchainStatus rpcBlockchainStatus =
        converter.convertToRpcModel(domainBlockchainStatus);
    final BlockchainStatus actualDomainBlockchainStatus =
        converter.convertToDomainModel(rpcBlockchainStatus);
    assertNotNull(actualDomainBlockchainStatus);
  }

}
