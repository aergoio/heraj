/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountAddress;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.Hash;
import hera.api.model.TxHash;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;
import types.Rpc;

public class BlockMetadataConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, com.google.protobuf.ByteString> addressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function<BlockHeader, Rpc.BlockMetadata> domainConverter = domainBlock -> {
    throw new UnsupportedOperationException();
  };

  protected final Function<Rpc.BlockMetadata, BlockHeader> rpcConverter = rpcBlockMetaData -> {
    logger.trace("Rpc block metadata: {}", rpcBlockMetaData);

    final Blockchain.BlockHeader rpcBlockHeader = rpcBlockMetaData.getHeader();

    return new BlockHeader(
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
  };

  public ModelConverter<BlockHeader, Rpc.BlockMetadata> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
