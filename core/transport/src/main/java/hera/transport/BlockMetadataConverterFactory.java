/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.Hash;
import hera.api.model.TxHash;
import org.slf4j.Logger;
import types.Blockchain;
import types.Rpc;

public class BlockMetadataConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, com.google.protobuf.ByteString> addressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function1<BlockHeader, Rpc.BlockMetadata> domainConverter =
      new Function1<BlockHeader, Rpc.BlockMetadata>() {

        @Override
        public Rpc.BlockMetadata apply(BlockHeader domainBlock) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Rpc.BlockMetadata, BlockHeader> rpcConverter =
      new Function1<Rpc.BlockMetadata, BlockHeader>() {

        @Override
        public BlockHeader apply(final Rpc.BlockMetadata rpcBlockMetaData) {
          logger.trace("Rpc block header to convert: {}", rpcBlockMetaData);
          final Blockchain.BlockHeader rpcBlockHeader = rpcBlockMetaData.getHeader();
          final BlockHeader domainBlockHeader = new BlockHeader(
              new Hash(of(rpcBlockHeader.getChainID().toByteArray())),
              new BlockHash(of(rpcBlockMetaData.getHash().toByteArray())),
              new BlockHash(of(rpcBlockHeader.getPrevBlockHash().toByteArray())),
              rpcBlockHeader.getBlockNo(),
              rpcBlockHeader.getTimestamp(),
              new BlockHash(of(rpcBlockHeader.getBlocksRootHash().toByteArray())),
              new TxHash(of(rpcBlockHeader.getTxsRootHash().toByteArray())),
              new Hash(of(rpcBlockHeader.getReceiptsRootHash().toByteArray())),
              rpcBlockHeader.getConfirms(),
              new Hash(of(rpcBlockHeader.getPubKey().toByteArray())),
              new Hash(of(rpcBlockHeader.getSign().toByteArray())),
              addressConverter.convertToDomainModel(rpcBlockHeader.getCoinbaseAccount()),
              rpcBlockMetaData.getTxcount());
          logger.trace("Domain block header converted: {}", domainBlockHeader);
          return domainBlockHeader;
        }
      };

  public ModelConverter<BlockHeader, Rpc.BlockMetadata> create() {
    return new ModelConverter<BlockHeader, Rpc.BlockMetadata>(domainConverter, rpcConverter);
  }

}
