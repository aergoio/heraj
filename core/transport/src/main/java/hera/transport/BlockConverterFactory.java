/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static com.google.protobuf.ByteString.copyFrom;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Block;
import hera.api.model.Hash;
import hera.api.model.Transaction;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;
import types.Blockchain.BlockBody;
import types.Blockchain.BlockHeader;
import types.Blockchain.Tx;

public class BlockConverterFactory {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<Transaction, Tx> transactionConverter =
      new TransactionConverterFactory().create();

  protected final Function<Block, Blockchain.Block> domainConverter = domainBlock -> {
    logger.trace("Domain status: {}", domainBlock);

    final BlockHeader blockHeader = BlockHeader.newBuilder()
        .setPrevBlockHash(copyFrom(domainBlock.getPreviousBlockHash().getBytesValue()))
        .setBlockNo(domainBlock.getBlockNumber())
        .setTimestamp(domainBlock.getTimestamp())
        .setBlocksRootHash(copyFrom(domainBlock.getRootHash().getBytesValue()))
        .setTxsRootHash(copyFrom(domainBlock.getTransactionsRootHash().getBytesValue()))
        .setPubKey(copyFrom(domainBlock.getPublicKey().getBytesValue()))
        .setSign(copyFrom(domainBlock.getSign().getBytesValue()))
        .build();

    final List<Blockchain.Tx> rpcTransactions = domainBlock.getTransactions().stream()
        .map(transactionConverter::convertToRpcModel)
        .collect(toList());
    final BlockBody blockBody = BlockBody.newBuilder()
        .addAllTxs(rpcTransactions)
        .build();

    return Blockchain.Block.newBuilder()
        .setHash(copyFrom(domainBlock.getHash().getBytesValue()))
        .setHeader(blockHeader)
        .setBody(blockBody)
        .build();
  };

  protected final Function<Blockchain.Block, Block> rpcConverter = rpcBlock -> {
    logger.trace("Blockchain status: {}", rpcBlock);

    final BlockHeader rpcBlockHeader = rpcBlock.getHeader();
    final BlockBody rpcBlockBody = rpcBlock.getBody();

    final Block domainBlock = new Block();
    domainBlock.setHash(new Hash(rpcBlock.getHash().toByteArray()));
    domainBlock.setBlockNumber(rpcBlockHeader.getBlockNo());
    domainBlock.setRootHash(new Hash(rpcBlockHeader.getBlocksRootHash().toByteArray()));
    domainBlock.setTimestamp(rpcBlockHeader.getTimestamp());
    domainBlock.setPreviousBlockHash(new Hash(rpcBlockHeader.getPrevBlockHash().toByteArray()));
    domainBlock.setTransactionsRootHash(new Hash(rpcBlockHeader.getTxsRootHash().toByteArray()));
    domainBlock.setPublicKey(new Hash(rpcBlockHeader.getPubKey().toByteArray()));
    domainBlock.setSign(new Hash(rpcBlockHeader.getSign().toByteArray()));

    final List<Transaction> transactions = rpcBlockBody.getTxsList().stream()
        .map(transactionConverter::convertToDomainModel).collect(toList());
    domainBlock.setTransactions(transactions);
    return domainBlock;
  };

  public ModelConverter<Block, Blockchain.Block> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
