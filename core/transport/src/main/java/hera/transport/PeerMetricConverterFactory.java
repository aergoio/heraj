/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.PeerMetric;
import hera.util.Base58Utils;
import org.slf4j.Logger;
import types.Metric;

public class PeerMetricConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<PeerMetric, Metric.PeerMetric> domainConverter =
      new Function1<PeerMetric, Metric.PeerMetric>() {

        @Override
        public Metric.PeerMetric apply(final PeerMetric domainPeer) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Metric.PeerMetric, PeerMetric> rpcConverter =
      new Function1<Metric.PeerMetric, PeerMetric>() {

        @Override
        public PeerMetric apply(final Metric.PeerMetric rpcPeerMetric) {
          logger.trace("Rpc peer metric to convert: {}", rpcPeerMetric);
          final PeerMetric domainPeerMetric =
              new PeerMetric(Base58Utils.encode(rpcPeerMetric.getPeerID().toByteArray()),
                  rpcPeerMetric.getSumIn(), rpcPeerMetric.getAvrIn(),
                  rpcPeerMetric.getSumOut(), rpcPeerMetric.getAvrOut());
          logger.trace("Domain peer metric converted: {}", domainPeerMetric);
          return domainPeerMetric;
        }
      };

  public ModelConverter<PeerMetric, Metric.PeerMetric> create() {
    return new ModelConverter<PeerMetric, Metric.PeerMetric>(domainConverter, rpcConverter);
  }

}
