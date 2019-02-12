/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCKCHAIN_BLOCKCHAINSTATUS;
import static hera.TransportConstants.BLOCKCHAIN_CHAININFO;
import static hera.TransportConstants.BLOCKCHAIN_LIST_ELECTED_BPS;
import static hera.TransportConstants.BLOCKCHAIN_LIST_PEERS;
import static hera.TransportConstants.BLOCKCHAIN_LIST_VOTESOF;
import static hera.TransportConstants.BLOCKCHAIN_NODESTATUS;
import static hera.TransportConstants.BLOCKCHAIN_PEERMETRICS;
import static hera.TransportConstants.BLOCKCHAIN_VOTE;
import static hera.api.function.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockchainOperation;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.function.Function3;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.BlockProducer;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainInfo;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerId;
import hera.api.model.PeerMetric;
import hera.api.model.TxHash;
import hera.api.model.VotingInfo;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class BlockchainTemplate
    implements BlockchainOperation, ChannelInjectable, ContextProviderInjectable {

  @Getter
  protected BlockchainBaseTemplate blockchainBaseTemplate = new BlockchainBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    getBlockchainBaseTemplate().setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    getBlockchainBaseTemplate().setContextProvider(contextProvider);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<FinishableFuture<BlockchainStatus>> blockchainStatusFunction =
      getStrategyChain().apply(identify(getBlockchainBaseTemplate().getBlockchainStatusFunction(),
          BLOCKCHAIN_BLOCKCHAINSTATUS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<FinishableFuture<ChainInfo>> chainInfoFunction =
      getStrategyChain().apply(identify(getBlockchainBaseTemplate().getChainInfoFunction(),
          BLOCKCHAIN_CHAININFO));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<FinishableFuture<List<Peer>>> listPeersFunction =
      getStrategyChain()
          .apply(
              identify(getBlockchainBaseTemplate().getListPeersFunction(), BLOCKCHAIN_LIST_PEERS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<FinishableFuture<List<PeerMetric>>> listPeerMetricsFunction =
      getStrategyChain().apply(
          identify(getBlockchainBaseTemplate().getListPeersMetricsFunction(),
              BLOCKCHAIN_PEERMETRICS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<FinishableFuture<NodeStatus>> nodeStatusFunction =
      getStrategyChain().apply(
          identify(getBlockchainBaseTemplate().getNodeStatusFunction(), BLOCKCHAIN_NODESTATUS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<Account, PeerId, Long, FinishableFuture<TxHash>> voteFunction =
      getStrategyChain().apply(
          identify(getBlockchainBaseTemplate().getVoteFunction(), BLOCKCHAIN_VOTE));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Long,
      FinishableFuture<List<BlockProducer>>> listElectedBlockProducersFunction =
          getStrategyChain()
              .apply(identify(getBlockchainBaseTemplate().getListElectedBlockProducersFunction(),
                  BLOCKCHAIN_LIST_ELECTED_BPS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<AccountAddress, FinishableFuture<List<VotingInfo>>> listVotesOfFunction =
      getStrategyChain().apply(
          identify(getBlockchainBaseTemplate().getListVotesOfFunction(), BLOCKCHAIN_LIST_VOTESOF));

  @Override
  public BlockchainStatus getBlockchainStatus() {
    return getBlockchainStatusFunction().apply().get();
  }

  @Override
  public ChainInfo getChainInfo() {
    return getChainInfoFunction().apply().get();
  }

  @Override
  public List<Peer> listPeers() {
    return getListPeersFunction().apply().get();
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    return getListPeerMetricsFunction().apply().get();
  }

  @Override
  public NodeStatus getNodeStatus() {
    return getNodeStatusFunction().apply().get();
  }

  @Override
  public TxHash vote(final Account account, final PeerId peerId, final long nonce) {
    return getVoteFunction().apply(account, peerId, nonce).get();
  }

  @Override
  public List<BlockProducer> listElectedBlockProducers(long showCount) {
    return getListElectedBlockProducersFunction().apply(showCount).get();
  }

  @Override
  public List<VotingInfo> listVotesOf(final AccountAddress accountAddress) {
    return getListVotesOfFunction().apply(accountAddress).get();
  }
}
