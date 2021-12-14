/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_CLIENT;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.*;
import hera.api.model.Block;
import hera.api.model.BlockMetadata;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;
import types.Blockchain;
import types.Rpc;

@PrepareForTest({AergoRPCServiceBlockingStub.class, AergoRPCServiceStub.class})
public class BlockMethodsTest extends AbstractTestCase {
  @BeforeClass
  public static void beforeClass() throws Exception {
    // powermock cannot mock java.security packages in jdk17 due to stricter security policies
    Assume.assumeTrue(TestUtils.getVersion() < 17 );
  }

  @Test
  public void testBlockMetadataByHash() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getBlockMetadata(any(Rpc.SingleBytes.class)))
              .thenReturn(Rpc.BlockMetadata.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyBlockHash);
          final BlockMetadata blockMetadata = blockMethods.getBlockMetadataByHash()
              .invoke(parameters);
          assertNotNull(blockMetadata);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void shouldBlockMetadataByHashReturnNullOnNotFoundError() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final StatusRuntimeException error = new StatusRuntimeException(
              Status.INTERNAL.withDescription("Block not found"));
          when(mockBlockingStub.getBlockMetadata(any(Rpc.SingleBytes.class))).thenThrow(error);
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyBlockHash);
          final BlockMetadata blockMetadata = blockMethods.getBlockMetadataByHash()
              .invoke(parameters);
          assertNull(blockMetadata);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testBlockMetadataByHeight() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getBlockMetadata(any(Rpc.SingleBytes.class)))
              .thenReturn(Rpc.BlockMetadata.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyHeight);
          final BlockMetadata blockMetadata = blockMethods.getBlockMetadataByHeight()
              .invoke(parameters);
          assertNotNull(blockMetadata);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void shouldBlockMetadataByHeightReturnNullOnNotFoundError() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final StatusRuntimeException error = new StatusRuntimeException(
              Status.INTERNAL.withDescription("Block not found"));
          when(mockBlockingStub.getBlockMetadata(any(Rpc.SingleBytes.class))).thenThrow(error);
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyHeight);
          final BlockMetadata blockMetadata = blockMethods.getBlockMetadataByHeight()
              .invoke(parameters);
          assertNull(blockMetadata);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testListBlockMetadatasByHash() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.listBlockMetadata(any(Rpc.ListParams.class)))
              .thenReturn(Rpc.BlockMetadataList.newBuilder()
                  .addBlocks(Rpc.BlockMetadata.newBuilder().build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyBlockHash, anySize);
          final List<BlockMetadata> blockMetadataList = blockMethods.getListBlockMetadatasByHash()
              .invoke(parameters);
          assertNotNull(blockMetadataList);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void shouldListBlockMetadatasByHashReturnEmptyListOnNotFoundError() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final StatusRuntimeException error = new StatusRuntimeException(
              Status.INTERNAL.withDescription("Block not found"));
          when(mockBlockingStub.listBlockMetadata(any(Rpc.ListParams.class))).thenThrow(error);
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyBlockHash, anySize);
          final List<BlockMetadata> blockMetadataList = blockMethods.getListBlockMetadatasByHash()
              .invoke(parameters);
          assertEquals(emptyList(), blockMetadataList);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testListBlockMetadatasByHeight() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.listBlockMetadata(any(Rpc.ListParams.class)))
              .thenReturn(Rpc.BlockMetadataList.newBuilder()
                  .addBlocks(Rpc.BlockMetadata.newBuilder().build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyHeight, anySize);
          final List<BlockMetadata> blockMetadataList = blockMethods.getListBlockMetadatasByHeight()
              .invoke(parameters);
          assertNotNull(blockMetadataList);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void shouldListBlockMetadatasByHeightReturnEmptyListOnNotFoundError() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final StatusRuntimeException error = new StatusRuntimeException(
              Status.INTERNAL.withDescription("Block not found"));
          when(mockBlockingStub.listBlockMetadata(any(Rpc.ListParams.class))).thenThrow(error);
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyHeight, anySize);
          final List<BlockMetadata> blockMetadataList = blockMethods.getListBlockMetadatasByHeight()
              .invoke(parameters);
          assertEquals(emptyList(), blockMetadataList);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testBlockByHash() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getBlock(any(Rpc.SingleBytes.class)))
              .thenReturn(Blockchain.Block.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyBlockHash);
          final Block block = blockMethods.getBlockByHash()
              .invoke(parameters);
          assertNotNull(block);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void shouldBlockByHashReturnEmptyListOnNotFoundError() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final StatusRuntimeException error = new StatusRuntimeException(
              Status.INTERNAL.withDescription("Block not found"));
          when(mockBlockingStub.getBlock(any(Rpc.SingleBytes.class))).thenThrow(error);
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyBlockHash);
          final Block block = blockMethods.getBlockByHash()
              .invoke(parameters);
          assertNull(block);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testBlockByHeight() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getBlock(any(Rpc.SingleBytes.class)))
              .thenReturn(Blockchain.Block.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyHeight);
          final Block block = blockMethods.getBlockByHeight()
              .invoke(parameters);
          assertNotNull(block);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void shouldBlockByHeightReturnNullOnNotFoundError() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final StatusRuntimeException error = new StatusRuntimeException(
              Status.INTERNAL.withDescription("Block not found"));
          when(mockBlockingStub.getBlock(any(Rpc.SingleBytes.class))).thenThrow(error);
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyHeight);
          final Block block = blockMethods.getBlockByHeight()
              .invoke(parameters);
          assertNull(block);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testSubscribeBlockMetadata() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceStub mockStreamStub = mock(AergoRPCServiceStub.class);
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getStreamStub()).thenReturn(mockStreamStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(
              new StreamObserver<BlockMetadata>() {
                @Override
                public void onNext(BlockMetadata value) {

                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {

                }
              });
          final Subscription<BlockMetadata> subscription = blockMethods.getSubscribeBlockMetadata()
              .invoke(parameters);
          assertNotNull(subscription);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testSubscribeBlock() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceStub mockStreamStub = mock(AergoRPCServiceStub.class);
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getStreamStub()).thenReturn(mockStreamStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final BlockMethods blockMethods = new BlockMethods();
          final List<Object> parameters = Arrays.<Object>asList(
              new StreamObserver<Block>() {
                @Override
                public void onNext(Block value) {

                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {

                }
              });
          final Subscription<Block> subscription = blockMethods.getSubscribeBlock()
              .invoke(parameters);
          assertNotNull(subscription);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

}
