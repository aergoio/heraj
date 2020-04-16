/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.TransportUtils.copyFrom;

import hera.RequestMethod;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.transport.BlockConverterFactory;
import hera.transport.BlockMetadataConverterFactory;
import hera.transport.ModelConverter;
import io.grpc.Context;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import types.Blockchain;
import types.Rpc;

class BlockMethods extends AbstractMethods {

  protected final ModelConverter<BlockMetadata, types.Rpc.BlockMetadata> blockMetadataConverter =
      new BlockMetadataConverterFactory().create();

  protected final ModelConverter<Block, Blockchain.Block> blockConverter =
      new BlockConverterFactory().create();

  @Getter
  protected final RequestMethod<BlockMetadata> blockMetadataByHash =
      new RequestMethod<BlockMetadata>() {

        @Getter
        protected final String name = "blockMetadataByHash";

        @Override
        protected void validate(final List<Object> parameters) {
          validateType(parameters, 0, BlockHash.class);
        }

        @Override
        protected BlockMetadata runInternal(final List<Object> parameters) {
          final BlockHash blockHash = (BlockHash) parameters.get(0);
          logger.debug("Get block metadata with hash: {}", blockHash);

          final Rpc.SingleBytes rpcHash = Rpc.SingleBytes.newBuilder()
              .setValue(copyFrom(blockHash.getBytesValue()))
              .build();
          logger.trace("AergoService getBlockMetadata arg: {}", rpcHash);

          final Rpc.BlockMetadata rpcBlockMetadata = getBlockingStub().getBlockMetadata(rpcHash);
          return blockMetadataConverter.convertToDomainModel(rpcBlockMetadata);
        }
      };

  @Getter
  protected final RequestMethod<BlockMetadata> blockMetadataByHeight =
      new RequestMethod<BlockMetadata>() {

        @Getter
        protected final String name = "blockMetadataByHeight";

        @Override
        protected void validate(final List<Object> parameters) {
          validateType(parameters, 0, Long.class);
          validateValue(((long) parameters.get(0)) >= 0, "Height must >= 0");
        }

        @Override
        protected BlockMetadata runInternal(final List<Object> parameters) throws Exception {
          final long height = (long) parameters.get(0);
          logger.debug("Get block metadata with height: {}", height);

          final Rpc.SingleBytes rpcHeight = Rpc.SingleBytes.newBuilder()
              .setValue(copyFrom(height))
              .build();
          logger.trace("AergoService getBlockMetadata arg: {}", rpcHeight);

          final Rpc.BlockMetadata rpcBlockMetadata = getBlockingStub().getBlockMetadata(rpcHeight);
          return blockMetadataConverter.convertToDomainModel(rpcBlockMetadata);
        }

      };

  @Getter
  protected final RequestMethod<List<BlockMetadata>> listBlockMetadatasByHash =
      new RequestMethod<List<BlockMetadata>>() {

        @Getter
        protected final String name = "listBlockMetadatasByHash";

        @Override
        protected void validate(final List<Object> parameters) {
          validateType(parameters, 0, BlockHash.class);
          validateType(parameters, 1, Integer.class);
          validateValue(((int) parameters.get(1)) > 1, "Size must >= 1");
        }

        @Override
        protected List<BlockMetadata> runInternal(final List<Object> parameters) throws Exception {
          final BlockHash blockHash = (BlockHash) parameters.get(0);
          final int size = (int) parameters.get(1);
          logger.debug("List block meta datas with hash: {}, size: {}", blockHash, size);

          final Rpc.ListParams rpcHashAndSize = Rpc.ListParams.newBuilder()
              .setHash(copyFrom(blockHash.getBytesValue()))
              .setSize(size)
              .build();
          logger.trace("AergoService listBlockMetadata arg: {}", rpcHashAndSize);

          final Rpc.BlockMetadataList rpcMetadatas = getBlockingStub()
              .listBlockMetadata(rpcHashAndSize);
          final List<BlockMetadata> blockMetadatas = new LinkedList<>();
          for (final Rpc.BlockMetadata rpcBlockMetadata : rpcMetadatas.getBlocksList()) {
            blockMetadatas.add(blockMetadataConverter.convertToDomainModel(rpcBlockMetadata));
          }
          return blockMetadatas;
        }
      };

  @Getter
  protected final RequestMethod<List<BlockMetadata>> listBlockMetadatasByHeight =
      new RequestMethod<List<BlockMetadata>>() {

        @Getter
        protected final String name = "listBlockMetadatasByHeight";

        @Override
        protected void validate(final List<Object> parameters) {
          validateType(parameters, 0, Long.class);
          validateType(parameters, 1, Integer.class);
          validateValue(((long) parameters.get(0)) >= 0, "Height must >= 0");
          validateValue(((int) parameters.get(1)) > 1, "Size must >= 1");
        }

        @Override
        protected List<BlockMetadata> runInternal(final List<Object> parameters) throws Exception {
          final long height = (long) parameters.get(0);
          final int size = (int) parameters.get(1);
          logger.debug("List block meta datas with height: {}, size: {}", height, size);

          final Rpc.ListParams rpcHeightAndSize = Rpc.ListParams.newBuilder()
              .setHeight(height)
              .setSize(size)
              .build();
          logger.trace("AergoService listBlockMetadata arg: {}", rpcHeightAndSize);

          final Rpc.BlockMetadataList rpcMetadatas = getBlockingStub()
              .listBlockMetadata(rpcHeightAndSize);
          final List<BlockMetadata> blockHeaders = new LinkedList<>();
          for (final Rpc.BlockMetadata rpcBlockMetadata : rpcMetadatas.getBlocksList()) {
            blockHeaders.add(blockMetadataConverter.convertToDomainModel(rpcBlockMetadata));
          }
          return blockHeaders;
        }
      };

  @Getter
  protected final RequestMethod<Block> blockByHash = new RequestMethod<Block>() {

    @Getter
    protected final String name = "blockByHash";

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, BlockHash.class);
    }

    @Override
    protected Block runInternal(final List<Object> parameters) throws Exception {
      final BlockHash blockHash = (BlockHash) parameters.get(0);
      logger.debug("Get block with hash: {}", blockHash);

      final Rpc.SingleBytes rpcHeight = Rpc.SingleBytes.newBuilder()
          .setValue(copyFrom(blockHash.getBytesValue()))
          .build();
      logger.trace("AergoService getBlock arg: {}", rpcHeight);

      final Blockchain.Block rpcBlock = getBlockingStub().getBlock(rpcHeight);
      return blockConverter.convertToDomainModel(rpcBlock);
    }

  };

  @Getter
  protected final RequestMethod<Block> blockByHeight = new RequestMethod<Block>() {

    @Getter
    protected final String name = "blockByHeight";

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Long.class);
      validateValue(((long) parameters.get(0)) >= 0, "Height must >= 0");
    }

    @Override
    protected Block runInternal(final List<Object> parameters) throws Exception {
      final long height = (long) parameters.get(0);
      logger.debug("Get block with height: {}", height);

      final Rpc.SingleBytes rpcHeight = Rpc.SingleBytes.newBuilder()
          .setValue(copyFrom(height))
          .build();
      logger.trace("AergoService getBlock arg: {}", rpcHeight);

      final Blockchain.Block rpcBlock = getBlockingStub().getBlock(rpcHeight);
      return blockConverter.convertToDomainModel(rpcBlock);
    }

  };


  @Getter
  protected final RequestMethod<Subscription<BlockMetadata>> subscribeBlockMetadata =
      new RequestMethod<Subscription<BlockMetadata>>() {

        @Getter
        protected final String name = "subscribeBlockMetadata";

        @Override
        protected void validate(final List<Object> parameters) {
          validateType(parameters, 0, StreamObserver.class);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Subscription<BlockMetadata> runInternal(final List<Object> parameters)
            throws Exception {
          final StreamObserver<BlockMetadata> observer = (StreamObserver<BlockMetadata>) parameters
              .get(0);
          logger.debug("Subscribe block metadata stream with observer: {}", observer);

          final Context.CancellableContext cancellableContext = Context.current()
              .withCancellation();
          final Rpc.Empty blockMetadataStreamRequest = Rpc.Empty.newBuilder().build();
          final io.grpc.stub.StreamObserver<Rpc.BlockMetadata> adaptor =
              new GrpcStreamObserverAdaptor<>(cancellableContext, observer, blockMetadataConverter);
          cancellableContext.run(new Runnable() {
            @Override
            public void run() {
              getStreamStub().listBlockMetadataStream(blockMetadataStreamRequest, adaptor);
            }
          });
          return new GrpcStreamSubscription<>(cancellableContext);
        }
      };

  @Getter
  protected final RequestMethod<Subscription<Block>> subscribeBlock =
      new RequestMethod<Subscription<Block>>() {

        @Getter
        protected final String name = "subscribeBlock";

        @Override
        protected void validate(final List<Object> parameters) {
          validateType(parameters, 0, StreamObserver.class);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Subscription<Block> runInternal(final List<Object> parameters)
            throws Exception {
          final StreamObserver<Block> observer = (StreamObserver<Block>) parameters
              .get(0);
          logger.debug("Subscribe block stream with observer {}", observer);

          final Context.CancellableContext cancellableContext = Context.current()
              .withCancellation();
          final Rpc.Empty blockStreamRequest = Rpc.Empty.newBuilder().build();
          final io.grpc.stub.StreamObserver<Blockchain.Block> adaptor =
              new GrpcStreamObserverAdaptor<>(cancellableContext, observer, blockConverter);
          cancellableContext.run(new Runnable() {
            @Override
            public void run() {
              getStreamStub().listBlockStream(blockStreamRequest, adaptor);
            }
          });
          return new GrpcStreamSubscription<>(cancellableContext);
        }
      };

}
