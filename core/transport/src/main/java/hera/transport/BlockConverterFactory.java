/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.Hash;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;
import types.Blockchain.BlockBody;
import types.Blockchain.BlockHeader;
import types.Blockchain.Tx;

public class BlockConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<Transaction, Tx> transactionConverter =
      new TransactionConverterFactory().create();

  protected final Function<Block, Blockchain.Block> domainConverter = domainBlock -> {
    logger.trace("Domain block: {}", domainBlock);

    final BlockHeader blockHeader = BlockHeader.newBuilder()
        .setPrevBlockHash(copyFrom(domainBlock.getPreviousHash().getBytesValue()))
        .setBlockNo(domainBlock.getBlockNumber()).setTimestamp(domainBlock.getTimestamp())
        .setBlocksRootHash(copyFrom(domainBlock.getRootHash().getBytesValue()))
        .setTxsRootHash(copyFrom(domainBlock.getTxRootHash().getBytesValue()))
        .setPubKey(copyFrom(domainBlock.getPublicKey().getBytesValue()))
        .setSign(copyFrom(domainBlock.getSign().getBytesValue())).build();

    final List<Blockchain.Tx> rpcTransactions = domainBlock.getTransactions().stream()
        .map(transactionConverter::convertToRpcModel).collect(toList());
    final BlockBody blockBody = BlockBody.newBuilder().addAllTxs(rpcTransactions).build();

    return Blockchain.Block.newBuilder().setHash(copyFrom(domainBlock.getHash().getBytesValue()))
        .setHeader(blockHeader).setBody(blockBody).build();
  };

  protected final Function<Blockchain.Block, Block> rpcConverter = rpcBlock -> {
    logger.trace("Rpc block: {}", rpcBlock);

    final BlockHeader rpcBlockHeader = rpcBlock.getHeader();
    final BlockBody rpcBlockBody = rpcBlock.getBody();

    final Block domainBlock = new Block();
    domainBlock.setHash(new BlockHash(of(rpcBlock.getHash().toByteArray())));
    domainBlock.setBlockNumber(rpcBlockHeader.getBlockNo());
    domainBlock.setRootHash(new BlockHash(of(rpcBlockHeader.getBlocksRootHash().toByteArray())));
    domainBlock.setTimestamp(rpcBlockHeader.getTimestamp());
    domainBlock.setPreviousHash(new BlockHash(of(rpcBlockHeader.getPrevBlockHash().toByteArray())));
    domainBlock.setTxRootHash(new TxHash(of(rpcBlockHeader.getTxsRootHash().toByteArray())));
    domainBlock.setPublicKey(new Hash(of(rpcBlockHeader.getPubKey().toByteArray())));
    domainBlock.setSign(new Hash(of(rpcBlockHeader.getSign().toByteArray())));

    final AtomicInteger index = new AtomicInteger(0);
    final List<Transaction> transactions = rpcBlockBody.getTxsList().stream()
        .map(transactionConverter::convertToDomainModel).map(t -> {
          t.setBlockHash(domainBlock.getHash());
          t.setIndexInBlock(index.getAndIncrement());
          t.setConfirmed(true);
          return t;
        }).collect(toList());
    domainBlock.setTransactions(transactions);
    return domainBlock;
  };

  public ModelConverter<Block, Blockchain.Block> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
