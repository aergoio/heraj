/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ChainStats;
import org.junit.Test;
import types.Rpc;

public class ChainStatsConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<ChainStats, Rpc.ChainStats> converter =
        new ChainStatsConverterFactory().create();
    final Rpc.ChainStats rpcChainStats = Rpc.ChainStats.newBuilder().build();
    final ChainStats converted = converter.convertToDomainModel(rpcChainStats);
    assertNotNull(converted);
  }

}
