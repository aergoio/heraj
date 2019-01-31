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
    final Rpc.BlockchainStatus rpcBlockchainStatus = Rpc.BlockchainStatus.newBuilder().build();
    final BlockchainStatus converted =
        converter.convertToDomainModel(rpcBlockchainStatus);
    assertNotNull(converted);
  }

}
