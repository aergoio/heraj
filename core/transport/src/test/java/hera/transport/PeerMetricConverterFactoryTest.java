/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertEquals;

import com.google.protobuf.ByteString;
import hera.AbstractTestCase;
import hera.api.model.PeerMetric;
import hera.util.Base58Utils;
import org.junit.Test;
import types.Metric;

public class PeerMetricConverterFactoryTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<PeerMetric, types.Metric.PeerMetric> converter =
        new PeerMetricConverterFactory().create();

    final Metric.PeerMetric rpcPeerMetric = Metric.PeerMetric.newBuilder()
        .setPeerID(ByteString.EMPTY)
        .build();
    final PeerMetric actual =
        PeerMetric.newBuilder().peerId(Base58Utils.encode(new byte[0])).build();
    final PeerMetric expected = converter.convertToDomainModel(rpcPeerMetric);
    assertEquals(expected, actual);
  }

}
