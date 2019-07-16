/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.BlockMetadata;
import org.slf4j.Logger;
import types.Blockchain;
import types.Rpc;

public class BlockMetadataConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockHeader, Blockchain.BlockHeader> blockHeaderConverter =
      new BlockHeaderConverterFactory().create();

  protected final Function1<BlockMetadata,
      Rpc.BlockMetadata> domainConverter = new Function1<BlockMetadata, Rpc.BlockMetadata>() {

        @Override
        public Rpc.BlockMetadata apply(BlockMetadata domainBlock) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Rpc.BlockMetadata, BlockMetadata> rpcConverter =
      new Function1<Rpc.BlockMetadata, BlockMetadata>() {

        @Override
        public BlockMetadata apply(final Rpc.BlockMetadata rpcBlockMetadata) {
          logger.trace("Rpc block metadata to convert: {}", rpcBlockMetadata);
          final BlockMetadata domainBlockMetadata = BlockMetadata.newBuilder()
              .blockHash(new BlockHash(of(rpcBlockMetadata.getHash().toByteArray())))
              .blockHeader(blockHeaderConverter.convertToDomainModel(rpcBlockMetadata.getHeader()))
              .txCount(rpcBlockMetadata.getTxcount())
              .blockSize(rpcBlockMetadata.getSize())
              .build();
          logger.trace("Domain block metadata converted: {}", domainBlockMetadata);
          return domainBlockMetadata;
        }
      };

  public ModelConverter<BlockMetadata, Rpc.BlockMetadata> create() {
    return new ModelConverter<BlockMetadata, Rpc.BlockMetadata>(domainConverter, rpcConverter);
  }

}
