/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_CLIENT;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.*;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import java.util.Arrays;
import java.util.List;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;
import types.Metric;
import types.Rpc;

@PrepareForTest({AergoRPCServiceBlockingStub.class, AergoRPCServiceStub.class})
public class BlockchainMethodsTest extends AbstractTestCase {
  @BeforeClass
  public static void beforeClass() throws Exception {
    // powermock cannot mock java.security packages in jdk17 due to stricter security policies
    Assume.assumeTrue(TestUtils.getVersion() < 17 );
  }

  @Test
  public void testBlockchainStatus() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.blockchain(any(Rpc.Empty.class)))
              .thenReturn(Rpc.BlockchainStatus.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockchainMethods blockchainMethods = new BlockchainMethods();
          final List<Object> parameters = Arrays.<Object>asList();
          final BlockchainStatus blockchainStatus = blockchainMethods.getBlockchainStatus()
              .invoke(parameters);
          assertNotNull(blockchainStatus);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testChainInfo() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getChainInfo(any(Rpc.Empty.class)))
              .thenReturn(Rpc.ChainInfo.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockchainMethods blockchainMethods = new BlockchainMethods();
          final List<Object> parameters = Arrays.<Object>asList();
          final ChainInfo chainInfo = blockchainMethods.getChainInfo()
              .invoke(parameters);
          assertNotNull(chainInfo);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testChainStats() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.chainStat(any(Rpc.Empty.class)))
              .thenReturn(Rpc.ChainStats.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockchainMethods blockchainMethods = new BlockchainMethods();
          final List<Object> parameters = Arrays.<Object>asList();
          final ChainStats chainStats = blockchainMethods.getChainStats()
              .invoke(parameters);
          assertNotNull(chainStats);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testListPeers() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getPeers(any(Rpc.PeersParams.class)))
              .thenReturn(Rpc.PeerList.newBuilder()
                  .addPeers(Rpc.Peer.newBuilder().build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockchainMethods blockchainMethods = new BlockchainMethods();
          final List<Object> parameters = Arrays.<Object>asList(true, true);
          final List<Peer> peers = blockchainMethods.getListPeers()
              .invoke(parameters);
          assertNotNull(peers);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testListPeerMetrics() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.metric(any(Metric.MetricsRequest.class)))
              .thenReturn(Metric.Metrics.newBuilder()
                  .addPeers(Metric.PeerMetric.newBuilder().build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockchainMethods blockchainMethods = new BlockchainMethods();
          final List<Object> parameters = Arrays.<Object>asList();
          final List<PeerMetric> peerMetrics = blockchainMethods.getListPeersMetrics()
              .invoke(parameters);
          assertNotNull(peerMetrics);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testServerInfo() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getServerInfo(any(Rpc.KeyParams.class)))
              .thenReturn(Rpc.ServerInfo.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockchainMethods blockchainMethods = new BlockchainMethods();
          final List<Object> parameters = Arrays.<Object>asList(emptyList());
          final ServerInfo serverInfo = blockchainMethods.getServerInfo()
              .invoke(parameters);
          assertNotNull(serverInfo);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testNodeStatus() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.nodeState(any(Rpc.NodeReq.class)))
              .thenReturn(Rpc.SingleBytes.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockchainMethods blockchainMethods = new BlockchainMethods();
          final List<Object> parameters = Arrays.<Object>asList();
          final NodeStatus nodeStatus = blockchainMethods.getNodeStatus()
              .invoke(parameters);
          assertNotNull(nodeStatus);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

}
