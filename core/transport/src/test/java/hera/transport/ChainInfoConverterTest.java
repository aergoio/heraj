/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ChainInfo;
import org.junit.Test;
import types.Rpc;

public class ChainInfoConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<ChainInfo, Rpc.ChainInfo> converter =
        new ChainInfoConverterFactory().create();
    final Rpc.ChainInfo rpcChainInfo = Rpc.ChainInfo.newBuilder().build();
    final ChainInfo converted =
        converter.convertToDomainModel(rpcChainInfo);
    assertNotNull(converted);
  }

}
