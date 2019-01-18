/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.util.TransportUtils.longToByteArray;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.function.Function3;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.BlockProducer;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerId;
import hera.api.model.PeerMetric;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.VotingInfo;
import hera.api.model.internal.GovernanceRecipient;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.BlockchainStatusConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.NodeStatusConverterFactory;
import hera.transport.PeerConverterFactory;
import hera.transport.PeerMetricConverterFactory;
import hera.transport.VotingInfoConverterFactory;
import io.grpc.ManagedChannel;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Metric;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockchainBaseTemplate implements ChannelInjectable, ContextProviderInjectable {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainConverter =
      new BlockchainStatusConverterFactory().create();

  protected final ModelConverter<Peer, Rpc.Peer> peerConverter =
      new PeerConverterFactory().create();

  protected final ModelConverter<PeerMetric, Metric.PeerMetric> peerMetricConverter =
      new PeerMetricConverterFactory().create();

  protected final ModelConverter<NodeStatus, Rpc.SingleBytes> nodeStatusConverter =
      new NodeStatusConverterFactory().create();

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<VotingInfo, Rpc.Vote> voteConverter =
      new VotingInfoConverterFactory().create();

  protected AccountBaseTemplate accountBaseTemplate = new AccountBaseTemplate();

  protected TransactionBaseTemplate transactionBaseTemplate = new TransactionBaseTemplate();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
    this.accountBaseTemplate.setChannel(channel);
    this.transactionBaseTemplate.setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    this.accountBaseTemplate.setContextProvider(contextProvider);
    this.transactionBaseTemplate.setContextProvider(contextProvider);
  }

  @Getter
  private final Function0<FinishableFuture<BlockchainStatus>> blockchainStatusFunction =
      new Function0<FinishableFuture<BlockchainStatus>>() {

        @Override
        public FinishableFuture<BlockchainStatus> apply() {
          if (logger.isDebugEnabled()) {
            logger.debug("Get blockchain status, Context: {}", contextProvider.get());
          }

          FinishableFuture<BlockchainStatus> nextFuture = new FinishableFuture<BlockchainStatus>();
          try {
            final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
            ListenableFuture<Rpc.BlockchainStatus> listenableFuture =
                aergoService.blockchain(empty);

            FutureChain<Rpc.BlockchainStatus, BlockchainStatus> callback =
                new FutureChain<Rpc.BlockchainStatus, BlockchainStatus>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<Rpc.BlockchainStatus, BlockchainStatus>() {

              @Override
              public BlockchainStatus apply(Rpc.BlockchainStatus s) {
                return blockchainConverter.convertToDomainModel(s);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function0<FinishableFuture<List<Peer>>> listPeersFunction =
      new Function0<FinishableFuture<List<Peer>>>() {

        @Override
        public FinishableFuture<List<Peer>> apply() {
          if (logger.isDebugEnabled()) {
            logger.debug("List peers, Context: {}", contextProvider.get());
          }

          FinishableFuture<List<Peer>> nextFuture = new FinishableFuture<List<Peer>>();
          try {
            final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
            ListenableFuture<Rpc.PeerList> listenableFuture = aergoService.getPeers(empty);

            FutureChain<Rpc.PeerList, List<Peer>> callback =
                new FutureChain<Rpc.PeerList, List<Peer>>(nextFuture, contextProvider.get());
            callback.setSuccessHandler(new Function1<Rpc.PeerList, List<Peer>>() {

              @Override
              public List<Peer> apply(final Rpc.PeerList peerlist) {
                final List<Peer> domainPeerList = new ArrayList<Peer>();
                for (final Rpc.Peer rpcPeer : peerlist.getPeersList()) {
                  domainPeerList.add(peerConverter.convertToDomainModel(rpcPeer));
                }
                return domainPeerList;
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function0<FinishableFuture<List<PeerMetric>>> listPeersMetricsFunction =
      new Function0<FinishableFuture<List<PeerMetric>>>() {

        @Override
        public FinishableFuture<List<PeerMetric>> apply() {
          if (logger.isDebugEnabled()) {
            logger.debug("List peer metrics, Context: {}", contextProvider.get());
          }
          FinishableFuture<List<PeerMetric>> nextFuture = new FinishableFuture<List<PeerMetric>>();
          try {
            final Metric.MetricsRequest request =
                Metric.MetricsRequest.newBuilder().addTypes(Metric.MetricType.P2P_NETWORK).build();
            ListenableFuture<Metric.Metrics> listenableFuture = aergoService.metric(request);

            FutureChain<Metric.Metrics, List<PeerMetric>> callback =
                new FutureChain<Metric.Metrics, List<PeerMetric>>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<Metric.Metrics, List<PeerMetric>>() {

              @Override
              public List<PeerMetric> apply(final Metric.Metrics rpcMetrics) {
                final List<PeerMetric> domainMetrics = new ArrayList<PeerMetric>();
                for (final Metric.PeerMetric rpcMetric : rpcMetrics.getPeersList()) {
                  domainMetrics.add(peerMetricConverter.convertToDomainModel(rpcMetric));
                }
                return domainMetrics;
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function0<FinishableFuture<NodeStatus>> nodeStatusFunction =
      new Function0<FinishableFuture<NodeStatus>>() {

        @Override
        public FinishableFuture<NodeStatus> apply() {
          if (logger.isDebugEnabled()) {
            logger.debug("Get node status, Context: {}", contextProvider.get());
          }

          FinishableFuture<NodeStatus> nextFuture = new FinishableFuture<NodeStatus>();
          try {
            final ByteString rawTimeout = ByteString.copyFrom(longToByteArray(3000L));
            final Rpc.NodeReq nodeRequest = Rpc.NodeReq.newBuilder()
                .setTimeout(rawTimeout)
                .build();
            ListenableFuture<Rpc.SingleBytes> listenableFuture =
                aergoService.nodeState(nodeRequest);

            FutureChain<Rpc.SingleBytes, NodeStatus> callback =
                new FutureChain<Rpc.SingleBytes, NodeStatus>(nextFuture, contextProvider.get());
            callback.setSuccessHandler(new Function1<Rpc.SingleBytes, NodeStatus>() {

              @Override
              public NodeStatus apply(final Rpc.SingleBytes status) {
                return nodeStatusConverter.convertToDomainModel(status);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function3<Account, PeerId, Long, FinishableFuture<TxHash>> voteFunction =
      new Function3<Account, PeerId, Long, FinishableFuture<TxHash>>() {

        @Override
        public FinishableFuture<TxHash> apply(final Account account, final PeerId peerId,
            final Long nonce) {
          if (logger.isDebugEnabled()) {
            logger.debug("Voting with account: {}, PeerId: {}, nonce: {}",
                account.getAddress(), peerId, nonce);
          }

          final byte[] rawPeerId = peerId.getBytesValue().getValue();
          final byte[] rawPayload = new byte[1 + rawPeerId.length];
          rawPayload[0] = (byte) 'v';
          System.arraycopy(rawPeerId, 0, rawPayload, 1, rawPeerId.length);
          final RawTransaction rawTransaction = new RawTransaction(account.getAddress(),
              GovernanceRecipient.AERGO_SYSTEM,
              null,
              nonce,
              Fee.ZERO,
              new BytesValue(rawPayload),
              Transaction.TxType.GOVERNANCE);
          final Transaction signed =
              accountBaseTemplate.getSignFunction().apply(account, rawTransaction).get();
          return transactionBaseTemplate.getCommitFunction().apply(signed);
        }
      };

  @Getter
  private final Function1<Long,
      FinishableFuture<List<BlockProducer>>> listElectedBlockProducersFunction = new Function1<
          Long, FinishableFuture<List<BlockProducer>>>() {

        @Override
        public FinishableFuture<List<BlockProducer>> apply(final Long showCount) {
          if (logger.isDebugEnabled()) {
            logger.debug("Get votes status, ShowCount: {}, Context: {}", showCount,
                contextProvider.get());
          }

          FinishableFuture<List<BlockProducer>> nextFuture =
              new FinishableFuture<List<BlockProducer>>();
          try {
            final Rpc.SingleBytes request = Rpc.SingleBytes.newBuilder()
                .setValue(ByteString.copyFrom(longToByteArray(showCount.longValue())))
                .build();
            ListenableFuture<Rpc.VoteList> listenableFuture = aergoService.getVotes(request);

            FutureChain<Rpc.VoteList, List<BlockProducer>> callback =
                new FutureChain<Rpc.VoteList, List<BlockProducer>>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<Rpc.VoteList, List<BlockProducer>>() {

              @Override
              public List<BlockProducer> apply(final Rpc.VoteList rpcVoteList) {
                final List<BlockProducer> votes = new ArrayList<BlockProducer>();
                for (final Rpc.Vote rpcVote : rpcVoteList.getVotesList()) {
                  final VotingInfo rpcVotingInfo = voteConverter.convertToDomainModel(rpcVote);
                  votes.add(new BlockProducer(
                      rpcVotingInfo.getPeerId(), rpcVotingInfo.getAmount()));
                }
                return votes;
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function1<AccountAddress, FinishableFuture<List<VotingInfo>>> listVotesOfFunction =
      new Function1<AccountAddress, FinishableFuture<List<VotingInfo>>>() {

        @Override
        public FinishableFuture<List<VotingInfo>> apply(final AccountAddress accountAddress) {
          if (logger.isDebugEnabled()) {
            logger.debug("Get votes of, AccountAddress: {}, Context: {}", accountAddress,
                contextProvider.get());
          }

          FinishableFuture<List<VotingInfo>> nextFuture = new FinishableFuture<List<VotingInfo>>();
          try {
            final Rpc.SingleBytes request = Rpc.SingleBytes.newBuilder()
                .setValue(accountAddressConverter.convertToRpcModel(accountAddress))
                .build();
            ListenableFuture<Rpc.VoteList> listenableFuture = aergoService.getVotes(request);

            FutureChain<Rpc.VoteList, List<VotingInfo>> callback =
                new FutureChain<Rpc.VoteList, List<VotingInfo>>(nextFuture, contextProvider.get());
            callback.setSuccessHandler(new Function1<Rpc.VoteList, List<VotingInfo>>() {

              @Override
              public List<VotingInfo> apply(final Rpc.VoteList rpcVoteList) {
                final List<VotingInfo> votes = new ArrayList<VotingInfo>();
                for (final Rpc.Vote rpcVote : rpcVoteList.getVotesList()) {
                  votes.add(voteConverter.convertToDomainModel(rpcVote));
                }
                return votes;
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

}
