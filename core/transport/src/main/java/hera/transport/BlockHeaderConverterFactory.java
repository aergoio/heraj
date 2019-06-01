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
import hera.api.model.Signature;
import org.slf4j.Logger;
import types.Blockchain;

public class BlockHeaderConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, com.google.protobuf.ByteString> addressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function1<BlockHeader, Blockchain.BlockHeader> domainConverter =
      new Function1<BlockHeader, Blockchain.BlockHeader>() {

        @Override
        public Blockchain.BlockHeader apply(BlockHeader domainBlock) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Blockchain.BlockHeader, BlockHeader> rpcConverter =
      new Function1<Blockchain.BlockHeader, BlockHeader>() {

        @Override
        public BlockHeader apply(final Blockchain.BlockHeader rpcBlockHeader) {
          logger.trace("Rpc block header to convert: {}", rpcBlockHeader);
          final BlockHeader domainBlockHeader = new BlockHeader(
              of(rpcBlockHeader.getChainID().toByteArray()),
              new BlockHash(of(rpcBlockHeader.getPrevBlockHash().toByteArray())),
              rpcBlockHeader.getBlockNo(),
              rpcBlockHeader.getTimestamp(),
              new BlockHash(of(rpcBlockHeader.getBlocksRootHash().toByteArray())),
              new Hash(of(rpcBlockHeader.getTxsRootHash().toByteArray())),
              new Hash(of(rpcBlockHeader.getReceiptsRootHash().toByteArray())),
              rpcBlockHeader.getConfirms(),
              of(rpcBlockHeader.getPubKey().toByteArray()),
              addressConverter.convertToDomainModel(rpcBlockHeader.getCoinbaseAccount()),
              new Signature(of(rpcBlockHeader.getSign().toByteArray())));
          logger.trace("Domain block header converted: {}", domainBlockHeader);
          return domainBlockHeader;
        }
      };

  public ModelConverter<BlockHeader, Blockchain.BlockHeader> create() {
    return new ModelConverter<BlockHeader, Blockchain.BlockHeader>(domainConverter, rpcConverter);
  }

}
