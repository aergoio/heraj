/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.RequestMethod;
import hera.api.model.AccountAddress;
import hera.api.model.AccountTotalVote;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.ElectedCandidate;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.AccountTotalVoteConverterFactory;
import hera.transport.BlockchainStatusConverterFactory;
import hera.transport.ChainInfoConverterFactory;
import hera.transport.ChainStatsConverterFactory;
import hera.transport.ElectedCandidateConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.NodeStatusConverterFactory;
import hera.transport.PeerConverterFactory;
import hera.transport.PeerMetricConverterFactory;
import hera.transport.ServerInfoConverterFactory;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.Metric;
import types.Rpc;

class BlockchainMethods extends AbstractMethods {

  protected static final long NODE_STATUS_TIMEOUT = 3000L;

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainConverter =
      new BlockchainStatusConverterFactory().create();

  protected final ModelConverter<ChainInfo, Rpc.ChainInfo> chainInfoConverter =
      new ChainInfoConverterFactory().create();

  protected final ModelConverter<ChainStats, Rpc.ChainStats> chainStatsConverter =
      new ChainStatsConverterFactory().create();

  protected final ModelConverter<Peer, Rpc.Peer> peerConverter =
      new PeerConverterFactory().create();

  protected final ModelConverter<PeerMetric, Metric.PeerMetric> peerMetricConverter =
      new PeerMetricConverterFactory().create();

  protected final ModelConverter<ServerInfo, Rpc.ServerInfo> serverInfoConverter =
      new ServerInfoConverterFactory().create();

  protected final ModelConverter<NodeStatus, Rpc.SingleBytes> nodeStatusConverter =
      new NodeStatusConverterFactory().create();

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<ElectedCandidate, Rpc.Vote> electedCandidateConverter =
      new ElectedCandidateConverterFactory().create();

  protected final ModelConverter<AccountTotalVote, Rpc.AccountVoteInfo> accountTotalVoteConverter =
      new AccountTotalVoteConverterFactory().create();

  @Getter
  protected final RequestMethod<BlockchainStatus> blockchainStatus =
      new RequestMethod<BlockchainStatus>() {

        @Getter
        protected final String name = "blockchainStatus";

        @Override
        protected BlockchainStatus runInternal(final List<Object> parameters) throws Exception {
          logger.debug("Get blockchain status");

          final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
          logger.trace("AergoService blockchain arg: {}", empty);

          final Rpc.BlockchainStatus rpcBlockchainStatus = getBlockingStub().blockchain(empty);
          return blockchainConverter.convertToDomainModel(rpcBlockchainStatus);
        }
      };

  @Getter
  protected final RequestMethod<ChainInfo> chainInfo = new RequestMethod<ChainInfo>() {

    @Getter
    protected final String name = "chainInfo";

    @Override
    protected ChainInfo runInternal(final List<Object> parameters) throws Exception {
      logger.debug("Get chain info");

      final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
      logger.trace("AergoService getChainInfo arg: {}", empty);

      final Rpc.ChainInfo rpcChainInfo = getBlockingStub().getChainInfo(empty);
      return chainInfoConverter.convertToDomainModel(rpcChainInfo);
    }
  };

  @Getter
  protected final RequestMethod<ChainStats> chainStats = new RequestMethod<ChainStats>() {

    @Getter
    protected final String name = "chainStats";

    @Override
    protected ChainStats runInternal(final List<Object> parameters) throws Exception {
      logger.debug("Get chain info");

      final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
      logger.trace("AergoService getChainStats arg: {}", empty);

      final Rpc.ChainStats rpcChainStats = getBlockingStub().chainStat(empty);
      return chainStatsConverter.convertToDomainModel(rpcChainStats);
    }
  };

  @Getter
  protected final RequestMethod<List<Peer>> listPeers = new RequestMethod<List<Peer>>() {

    @Getter
    protected final String name = "listPeers";

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Boolean.class);
      validateType(parameters, 1, Boolean.class);
    }

    @Override
    protected List<Peer> runInternal(final List<Object> parameters) throws Exception {
      final boolean showHidden = (boolean) parameters.get(0);
      final boolean showSelf = (boolean) parameters.get(1);
      logger.debug("List peers with showHidden: {}, showSelf: {}", showHidden, showSelf);

      final Rpc.PeersParams peersParams = Rpc.PeersParams.newBuilder()
          .setNoHidden(!showHidden)
          .setShowSelf(showSelf)
          .build();
      logger.trace("AergoService getPeers arg: {}", peersParams);

      final Rpc.PeerList rpcPeerList = getBlockingStub().getPeers(peersParams);
      final List<Peer> domainPeers = new LinkedList<>();
      for (final Rpc.Peer rpcPeer : rpcPeerList.getPeersList()) {
        domainPeers.add(peerConverter.convertToDomainModel(rpcPeer));
      }
      return domainPeers;
    }

  };

  @Getter
  protected final RequestMethod<List<PeerMetric>> listPeersMetrics =
      new RequestMethod<List<PeerMetric>>() {

        @Getter
        protected final String name = "listPeersMetrics";

        @Override
        protected List<PeerMetric> runInternal(final List<Object> parameters) throws Exception {
          logger.debug("List peer metrics");

          final Metric.MetricsRequest rpcMetricRequest = Metric.MetricsRequest.newBuilder()
              .addTypes(Metric.MetricType.P2P_NETWORK)
              .build();
          logger.trace("AergoService metric arg: {}", rpcMetricRequest);

          final Metric.Metrics rpcPeerMetrics = getBlockingStub().metric(rpcMetricRequest);
          final List<PeerMetric> domainPeerMetrics = new LinkedList<>();
          for (final Metric.PeerMetric rpcPeerMetric : rpcPeerMetrics.getPeersList()) {
            domainPeerMetrics.add(peerMetricConverter.convertToDomainModel(rpcPeerMetric));
          }
          return domainPeerMetrics;
        }
      };

  @Getter
  protected final RequestMethod<ServerInfo> serverInfo = new RequestMethod<ServerInfo>() {

    @Getter
    protected final String name = "serverInfo";

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, List.class);
    }

    @Override
    protected ServerInfo runInternal(final List<Object> parameters) throws Exception {
      final List<String> keys = (List<String>) parameters.get(0);
      logger.debug("Get node status");

      final Rpc.KeyParams rpcServerInfoRequest = Rpc.KeyParams.newBuilder()
          .addAllKey(keys)
          .build();
      logger.trace("AergoService getServerInfo arg: {}", rpcServerInfoRequest);

      final Rpc.ServerInfo rpcServerInfo = getBlockingStub().getServerInfo(rpcServerInfoRequest);
      return serverInfoConverter.convertToDomainModel(rpcServerInfo);
    }
  };

  @Getter
  protected final RequestMethod<NodeStatus> nodeStatus = new RequestMethod<NodeStatus>() {

    @Getter
    protected final String name = "nodeStatus";

    @Override
    protected NodeStatus runInternal(final List<Object> parameters) throws Exception {
      logger.debug("Get node status");

      final Rpc.NodeReq rpcNodeRequest = Rpc.NodeReq.newBuilder()
          .setTimeout(copyFrom(NODE_STATUS_TIMEOUT))
          .build();
      logger.trace("AergoService nodeState arg: {}", rpcNodeRequest);

      final Rpc.SingleBytes rpcNodeStatus = getBlockingStub().nodeState(rpcNodeRequest);
      return nodeStatusConverter.convertToDomainModel(rpcNodeStatus);
    }
  };

}
