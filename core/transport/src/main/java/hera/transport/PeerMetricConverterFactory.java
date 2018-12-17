/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.PeerMetric;
import hera.util.Base58Utils;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Metric;

public class PeerMetricConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<PeerMetric, Metric.PeerMetric> domainConverter = domainPeer -> {
    throw new UnsupportedOperationException();
  };

  protected final Function<Metric.PeerMetric, PeerMetric> rpcConverter = rpcPeerMetric -> {
    logger.trace("Rpc peer metric: {}", rpcPeerMetric);

    return new PeerMetric(Base58Utils.encode(rpcPeerMetric.getPeerID().toByteArray()),
        rpcPeerMetric.getSumIn(), rpcPeerMetric.getAvrIn(),
        rpcPeerMetric.getSumOut(), rpcPeerMetric.getAvrOut());
  };


  public ModelConverter<PeerMetric, Metric.PeerMetric> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
