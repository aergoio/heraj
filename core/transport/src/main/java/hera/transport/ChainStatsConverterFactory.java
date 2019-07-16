/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.ChainStats;
import org.slf4j.Logger;
import types.Rpc;

public class ChainStatsConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<ChainStats, Rpc.ChainStats> domainConverter =
      new Function1<ChainStats, Rpc.ChainStats>() {

        @Override
        public Rpc.ChainStats apply(final ChainStats domainChainStats) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Rpc.ChainStats, ChainStats> rpcConverter =
      new Function1<Rpc.ChainStats, ChainStats>() {

        @Override
        public ChainStats apply(final Rpc.ChainStats rpcChainStats) {
          logger.trace("Rpc chain info to convert: {}", rpcChainStats);

          final ChainStats domainChainStats = ChainStats.newBuilder()
              .report(rpcChainStats.getReport())
              .build();
          logger.trace("Domain chain stats converted: {}", domainChainStats);
          return domainChainStats;
        }
      };

  public ModelConverter<ChainStats, Rpc.ChainStats> create() {
    return new ModelConverter<ChainStats, Rpc.ChainStats>(domainConverter,
        rpcConverter);
  }

}
