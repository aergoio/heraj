/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import hera.api.model.BlockHash;
import hera.api.model.Transaction;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class TransactionInBlockConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      new TransactionConverterFactory().create();

  protected final Function<Transaction, Blockchain.TxInBlock> domainConverter =
      domainTransaction -> {
        logger.trace("Domain transaction in block: {}", domainTransaction);
        final Blockchain.TxIdx rpcTxIdx = Blockchain.TxIdx.newBuilder()
            .setBlockHash(copyFrom(domainTransaction.getBlockHash().getBytesValue()))
            .setIdx(domainTransaction.getIndexInBlock()).build();
        final Blockchain.Tx rpcTx = transactionConverter.convertToRpcModel(domainTransaction);
        return Blockchain.TxInBlock.newBuilder().setTxIdx(rpcTxIdx).setTx(rpcTx).build();
      };

  protected final Function<Blockchain.TxInBlock, Transaction> rpcConverter = rpcTransaction -> {
    logger.trace("Rpc transaction in block: {}", rpcTransaction);
    final Blockchain.TxIdx rpcTxIdx = rpcTransaction.getTxIdx();
    final Blockchain.Tx rpcTx = rpcTransaction.getTx();
    final Transaction domainTransaction = transactionConverter.convertToDomainModel(rpcTx);
    domainTransaction.setBlockHash(new BlockHash(of(rpcTxIdx.getBlockHash().toByteArray())));
    domainTransaction.setIndexInBlock(rpcTxIdx.getIdx());
    if (!domainTransaction.getBlockHash().getBytesValue().isEmpty()) {
      domainTransaction.setConfirmed(true);
    }
    return domainTransaction;
  };

  public ModelConverter<Transaction, Blockchain.TxInBlock> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }
}
