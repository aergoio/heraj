/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountAddress;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.Hash;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class BlockConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, com.google.protobuf.ByteString> addressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      new TransactionConverterFactory().create();

  protected final ModelConverter<Transaction, Blockchain.TxInBlock> transactionInBlockConverter =
      new TransactionInBlockConverterFactory().create();

  protected final Function<Block, Blockchain.Block> domainConverter = domainBlock -> {
    logger.trace("Domain block: {}", domainBlock);

    final Blockchain.BlockHeader blockHeader = Blockchain.BlockHeader.newBuilder()
        .setChainID(copyFrom(domainBlock.getChainId().getBytesValue()))
        .setPrevBlockHash(copyFrom(domainBlock.getPreviousHash().getBytesValue()))
        .setBlockNo(domainBlock.getBlockNumber()).setTimestamp(domainBlock.getTimestamp())
        .setBlocksRootHash(copyFrom(domainBlock.getRootHash().getBytesValue()))
        .setTxsRootHash(copyFrom(domainBlock.getTxRootHash().getBytesValue()))
        .setReceiptsRootHash(copyFrom(domainBlock.getReceiptRootHash().getBytesValue()))
        .setConfirms(domainBlock.getConfirmsCount())
        .setPubKey(copyFrom(domainBlock.getPublicKey().getBytesValue()))
        .setSign(copyFrom(domainBlock.getSign().getBytesValue()))
        .setCoinbaseAccount(copyFrom(domainBlock.getCoinbaseAccount().getBytesValue()))
        .build();

    final List<Blockchain.Tx> rpcTransactions = new ArrayList<>();
    for (final Transaction domainTransaction : domainBlock.getTransactions()) {
      rpcTransactions.add(transactionConverter.convertToRpcModel(domainTransaction));
    }
    final Blockchain.BlockBody blockBody = Blockchain.BlockBody.newBuilder()
        .addAllTxs(rpcTransactions)
        .build();

    return Blockchain.Block.newBuilder()
        .setHash(copyFrom(domainBlock.getHash().getBytesValue()))
        .setHeader(blockHeader)
        .setBody(blockBody)
        .build();
  };

  protected final Function<Blockchain.Block, Block> rpcConverter = rpcBlock -> {
    logger.trace("Rpc block: {}", rpcBlock);

    final Blockchain.BlockHeader rpcBlockHeader = rpcBlock.getHeader();
    final Blockchain.BlockBody rpcBlockBody = rpcBlock.getBody();

    final BlockHash blockHash = new BlockHash(of(rpcBlock.getHash().toByteArray()));
    final AtomicInteger index = new AtomicInteger(0);
    final List<Transaction> transactions = new ArrayList<>();
    for (final Blockchain.Tx rpcTx : rpcBlockBody.getTxsList()) {
      final Blockchain.TxIdx rpcTxIdx = Blockchain.TxIdx.newBuilder()
          .setBlockHash(copyFrom(blockHash.getBytesValue()))
          .setIdx(index.getAndIncrement())
          .build();
      final Blockchain.TxInBlock rpcTxInBlock = Blockchain.TxInBlock.newBuilder()
          .setTxIdx(rpcTxIdx)
          .setTx(rpcTx)
          .build();
      transactions.add(transactionInBlockConverter.convertToDomainModel(rpcTxInBlock));
    }

    return new Block(
        new Hash(of(rpcBlockHeader.getChainID().toByteArray())),
        blockHash,
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
        transactions.size(),
        transactions);
  };

  public ModelConverter<Block, Blockchain.Block> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
