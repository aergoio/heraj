/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static hera.util.TransportUtils.copyFrom;
import static hera.util.ValidationUtils.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;
import static types.AergoRPCServiceGrpc.newStub;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.api.model.Subscription;
import hera.client.ChannelInjectable;
import hera.client.stream.GrpcStreamObserverAdaptor;
import hera.client.stream.GrpcStreamSubscription;
import hera.transport.BlockConverterFactory;
import hera.transport.BlockMetadataConverterFactory;
import hera.transport.ModelConverter;
import io.grpc.Context;
import io.grpc.ManagedChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockBaseTemplate implements ChannelInjectable, ContextProviderInjectable {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockMetadata, types.Rpc.BlockMetadata> blockMetadataConverter =
      new BlockMetadataConverterFactory().create();

  protected final ModelConverter<Block, Blockchain.Block> blockConverter =
      new BlockConverterFactory().create();

  protected AergoRPCServiceFutureStub aergoService;
  protected AergoRPCServiceStub streamService;

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
    this.streamService = newStub(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Getter
  private final Function1<BlockHash, Future<BlockMetadata>> blockMetatdataByHashFunction =
      new Function1<BlockHash, Future<BlockMetadata>>() {

        @Override
        public Future<BlockMetadata> apply(final BlockHash hash) {
          logger.debug("Get block metadata with hash: {}", hash);

          final Rpc.SingleBytes rpcHash = Rpc.SingleBytes.newBuilder()
              .setValue(copyFrom(hash.getBytesValue()))
              .build();
          logger.trace("AergoService getBlockMetadata arg: {}", rpcHash);

          final Future<Rpc.BlockMetadata> rawFuture = aergoService.getBlockMetadata(rpcHash);
          final Future<BlockMetadata> convertedFuture = HerajFutures.transform(rawFuture,
              new Function1<Rpc.BlockMetadata, BlockMetadata>() {

                @Override
                public BlockMetadata apply(final Rpc.BlockMetadata metadata) {
                  return blockMetadataConverter.convertToDomainModel(metadata);
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function1<Long, Future<BlockMetadata>> blockMetadataByHeightFunction =
      new Function1<Long, Future<BlockMetadata>>() {

        @Override
        public Future<BlockMetadata> apply(final Long height) {
          logger.debug("Get block metadata with height: {}", height);
          assertTrue(height >= 0, "Height must >= 0");

          final Rpc.SingleBytes rpcHeight = Rpc.SingleBytes.newBuilder()
              .setValue(copyFrom(height.longValue()))
              .build();
          logger.trace("AergoService getBlockMetadata arg: {}", rpcHeight);

          final Future<Rpc.BlockMetadata> rawFuture = aergoService.getBlockMetadata(rpcHeight);
          final Future<BlockMetadata> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Rpc.BlockMetadata, BlockMetadata>() {

                @Override
                public BlockMetadata apply(final Rpc.BlockMetadata metadata) {
                  return blockMetadataConverter.convertToDomainModel(metadata);
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function2<BlockHash, Integer,
      Future<List<BlockMetadata>>> listBlockMetadatasByHashFunction = new Function2<
          BlockHash, Integer, Future<List<BlockMetadata>>>() {

        @Override
        public Future<List<BlockMetadata>> apply(final BlockHash hash,
            final Integer size) {
          logger.debug("List block meta datas with hash: {}, size: {}", hash, size);
          assertTrue(size > 0, "Block list size must be postive");

          final Rpc.ListParams rpcHashAndSize = Rpc.ListParams.newBuilder()
              .setHash(copyFrom(hash.getBytesValue()))
              .setSize(size)
              .build();
          logger.trace("AergoService listBlockMetadata arg: {}", rpcHashAndSize);

          final Future<Rpc.BlockMetadataList> rawFuture =
              aergoService.listBlockMetadata(rpcHashAndSize);
          final Future<List<BlockMetadata>> convertedFuture = HerajFutures.transform(rawFuture,
              new Function1<Rpc.BlockMetadataList, List<BlockMetadata>>() {

                @Override
                public List<BlockMetadata> apply(final Rpc.BlockMetadataList rpcMetadatas) {
                  final List<BlockMetadata> blockMetadatas = new ArrayList<>();
                  for (final Rpc.BlockMetadata rpcBlockMetadata : rpcMetadatas.getBlocksList()) {
                    blockMetadatas
                        .add(blockMetadataConverter.convertToDomainModel(rpcBlockMetadata));
                  }
                  return blockMetadatas;
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function2<Long, Integer,
      Future<List<BlockMetadata>>> listBlockMetadatasByHeightFunction = new Function2<Long, Integer,
          Future<List<BlockMetadata>>>() {

        @Override
        public Future<List<BlockMetadata>> apply(final Long height,
            final Integer size) {
          logger.debug("List block meta datas with height: {}, size: {}", height, size);
          assertTrue(height >= 0, "Height must >= 0");
          assertTrue(size > 0, "Block list size must be postive");

          final Rpc.ListParams rpcHeightAndSize = Rpc.ListParams.newBuilder()
              .setHeight(height)
              .setSize(size)
              .build();
          logger.trace("AergoService listBlockMetadata arg: {}", rpcHeightAndSize);

          final Future<Rpc.BlockMetadataList> rawFuture =
              aergoService.listBlockMetadata(rpcHeightAndSize);
          final Future<List<BlockMetadata>> convertedFuture = HerajFutures.transform(rawFuture,
              new Function1<Rpc.BlockMetadataList, List<BlockMetadata>>() {
                @Override
                public List<BlockMetadata> apply(final Rpc.BlockMetadataList rpcMetadatas) {
                  final List<BlockMetadata> blockHeaders = new ArrayList<>();
                  for (final Rpc.BlockMetadata rpcBlockMetadata : rpcMetadatas
                      .getBlocksList()) {
                    blockHeaders
                        .add(blockMetadataConverter.convertToDomainModel(rpcBlockMetadata));
                  }
                  return blockHeaders;
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function1<BlockHash, Future<Block>> blockByHashFunction =
      new Function1<BlockHash, Future<Block>>() {

        @Override
        public Future<Block> apply(final BlockHash hash) {
          logger.debug("Get block with hash: {}", hash);

          final Rpc.SingleBytes rpcHash = Rpc.SingleBytes.newBuilder()
              .setValue(copyFrom(hash.getBytesValue()))
              .build();
          logger.trace("AergoService getBlock arg: {}", rpcHash);

          final Future<Blockchain.Block> rawFuture = aergoService.getBlock(rpcHash);
          final Future<Block> convertedFuture = HerajFutures.transform(rawFuture,
              new Function1<Blockchain.Block, Block>() {
                @Override
                public Block apply(final Blockchain.Block block) {
                  return blockConverter.convertToDomainModel(block);
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function1<Long, Future<Block>> blockByHeightFunction =
      new Function1<Long, Future<Block>>() {

        @Override
        public Future<Block> apply(final Long height) {
          logger.debug("Get block with height: {}", height);
          assertTrue(height >= 0, "Height must be >= 0");

          final Rpc.SingleBytes rpcHeight = Rpc.SingleBytes.newBuilder()
              .setValue(copyFrom(height.longValue()))
              .build();
          logger.trace("AergoService getBlock arg: {}", rpcHeight);

          final Future<Blockchain.Block> rawFuture = aergoService.getBlock(rpcHeight);
          final Future<Block> convertedFuture = HerajFutures.transform(rawFuture,
              new Function1<Blockchain.Block, Block>() {
                @Override
                public Block apply(final Blockchain.Block block) {
                  return blockConverter.convertToDomainModel(block);
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function1<hera.api.model.StreamObserver<BlockMetadata>,
      Future<Subscription<BlockMetadata>>> subscribeBlockMetadataFunction = new Function1<
          hera.api.model.StreamObserver<BlockMetadata>,
          Future<Subscription<BlockMetadata>>>() {

        @Override
        public Future<Subscription<BlockMetadata>> apply(
            final hera.api.model.StreamObserver<BlockMetadata> observer) {

          logger.debug("Subscribe block metadata stream with observer {}", observer);

          final Rpc.Empty blockMetadataStreamRequest = Rpc.Empty.newBuilder().build();
          Context.CancellableContext cancellableContext = Context.current().withCancellation();
          final io.grpc.stub.StreamObserver<Rpc.BlockMetadata> adaptor =
              new GrpcStreamObserverAdaptor<Rpc.BlockMetadata, BlockMetadata>(cancellableContext,
                  observer, blockMetadataConverter);
          cancellableContext.run(new Runnable() {
            @Override
            public void run() {
              streamService.listBlockMetadataStream(blockMetadataStreamRequest, adaptor);
            }
          });

          final Subscription<BlockMetadata> subscription =
              new GrpcStreamSubscription<>(cancellableContext);
          return HerajFutures.success(subscription);
        }
      };

  @Getter
  private final Function1<hera.api.model.StreamObserver<Block>,
      Future<Subscription<Block>>> subscribeBlockFunction = new Function1<
          hera.api.model.StreamObserver<Block>, Future<Subscription<Block>>>() {

        @Override
        public Future<Subscription<Block>> apply(
            final hera.api.model.StreamObserver<Block> observer) {
          logger.debug("Subscribe block metadata stream with observer {}", observer);

          final Rpc.Empty blockStreamRequest = Rpc.Empty.newBuilder().build();
          Context.CancellableContext cancellableContext = Context.current().withCancellation();
          final io.grpc.stub.StreamObserver<Blockchain.Block> adaptor =
              new GrpcStreamObserverAdaptor<Blockchain.Block, Block>(cancellableContext,
                  observer, blockConverter);
          cancellableContext.run(new Runnable() {
            @Override
            public void run() {
              streamService.listBlockStream(blockStreamRequest, adaptor);
            }
          });

          final Subscription<Block> subscription = new GrpcStreamSubscription<>(cancellableContext);
          return HerajFutures.success(subscription);
        }
      };

}
