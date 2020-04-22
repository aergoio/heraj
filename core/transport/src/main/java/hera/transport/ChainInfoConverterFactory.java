/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.parseToAer;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.model.ChainId;
import hera.api.model.ChainInfo;
import org.slf4j.Logger;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
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

          final Rpc.ChainId rpcChainId = rpcChainInfo.getId();
          final ChainId domainChainId = ChainId.newBuilder()
              .magic(rpcChainId.getMagic())
              .isPublic(rpcChainId.getPublic())
              .isMainNet(rpcChainId.getMainnet())
              .consensus(rpcChainId.getConsensus())
              .build();

          final ChainInfo domainChainInfo = ChainInfo.newBuilder()
              .chainId(domainChainId)
              .blockProducerCount(rpcChainInfo.getBpNumber())
              .maxBlockSize(rpcChainInfo.getMaxblocksize())
              .totalTokenAmount(parseToAer(rpcChainInfo.getMaxtokens()))
              .minimumStakingAmount(parseToAer(rpcChainInfo.getStakingminimum()))
              .totalStaked(parseToAer(rpcChainInfo.getTotalstaking()))
              .gasPrice(parseToAer(rpcChainInfo.getGasprice()))
              .namingPrice(parseToAer(rpcChainInfo.getNameprice()))
              .totalVotingPower(parseToAer(rpcChainInfo.getTotalvotingpower()))
              .votingReward(parseToAer(rpcChainInfo.getVotingreward()))
              .build();
          logger.trace("Domain chain info converted: {}", domainChainInfo);
          return domainChainInfo;
        }
      };

  public ModelConverter<ChainInfo, Rpc.ChainInfo> create() {
    return new ModelConverter<>(domainConverter,
        rpcConverter);
  }

}
