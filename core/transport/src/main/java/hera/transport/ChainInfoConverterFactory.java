/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.parseToAer;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.ChainId;
import hera.api.model.ChainInfo;
import org.slf4j.Logger;
import types.Rpc;

public class ChainInfoConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<ChainInfo, Rpc.ChainInfo> domainConverter =
      new Function1<ChainInfo, Rpc.ChainInfo>() {

        @Override
        public Rpc.ChainInfo apply(final ChainInfo domainChainInfo) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Rpc.ChainInfo, ChainInfo> rpcConverter =
      new Function1<Rpc.ChainInfo, ChainInfo>() {

        @Override
        public ChainInfo apply(final Rpc.ChainInfo rpcChainInfo) {
          logger.trace("Rpc chain info to convert: {}", rpcChainInfo);

          final Rpc.ChainId rpcChainId = rpcChainInfo.getChainid();
          final ChainId domainChainId = new ChainId(rpcChainId.getMagic(),
              rpcChainId.getPublic(),
              rpcChainId.getMainnet(),
              parseToAer(rpcChainId.getCoinbasefee()),
              rpcChainId.getConsensus());

          final ChainInfo domainChainInfo = new ChainInfo(
              domainChainId,
              rpcChainInfo.getBpnumber(),
              rpcChainInfo.getMaxblocksize(),
              parseToAer(rpcChainInfo.getMaxtokens()),
              parseToAer(rpcChainInfo.getStakingminimum()));
          logger.trace("Domain blockchain status converted: {}", domainChainInfo);
          return domainChainInfo;
        }
      };

  public ModelConverter<ChainInfo, Rpc.ChainInfo> create() {
    return new ModelConverter<ChainInfo, Rpc.ChainInfo>(domainConverter,
        rpcConverter);
  }

}
