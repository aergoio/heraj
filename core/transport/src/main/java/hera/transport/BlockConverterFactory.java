/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.Hash;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import types.Blockchain;

public class BlockConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, com.google.protobuf.ByteString> addressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<Transaction, Blockchain.TxInBlock> transactionInBlockConverter =
      new TransactionInBlockConverterFactory().create();

  protected final Function1<Block, Blockchain.Block> domainConverter =
      new Function1<Block, Blockchain.Block>() {

        @Override
        public Blockchain.Block apply(final Block domainBlock) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Blockchain.Block, Block> rpcConverter =
      new Function1<Blockchain.Block, Block>() {

        @Override
        public Block apply(final Blockchain.Block rpcBlock) {
          logger.trace("Rpc block to convert: {}", rpcBlock);

          final Blockchain.BlockHeader rpcBlockHeader = rpcBlock.getHeader();
          final Blockchain.BlockBody rpcBlockBody = rpcBlock.getBody();

          final BlockHash blockHash = new BlockHash(of(rpcBlock.getHash().toByteArray()));
          final AtomicInteger index = new AtomicInteger(0);
          final List<Transaction> transactions = new ArrayList<Transaction>();
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

          final Block domainBlock = new Block(
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
          logger.trace("Domain block converted: {}", domainBlock);
          return domainBlock;
        }
      };

  public ModelConverter<Block, Blockchain.Block> create() {
    return new ModelConverter<Block, Blockchain.Block>(domainConverter, rpcConverter);
  }

}
