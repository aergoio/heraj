/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Hash;
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
        logger.trace("Domain status: {}", domainTransaction);
        final Blockchain.TxIdx rpcTxIdx =
            Blockchain.TxIdx.newBuilder().setBlockHash(copyFrom(domainTransaction.getBlockHash()))
                .setIdx(domainTransaction.getIndexInBlock()).build();
        final Blockchain.Tx rpcTx = transactionConverter.convertToRpcModel(domainTransaction);
        return Blockchain.TxInBlock.newBuilder().setTxIdx(rpcTxIdx).setTx(rpcTx).build();
      };

  protected final Function<Blockchain.TxInBlock, Transaction> rpcConverter = rpcTransaction -> {
    logger.trace("Blockchain status: {}", rpcTransaction);
    final Blockchain.TxIdx rpcTxIdx = rpcTransaction.getTxIdx();
    final Blockchain.Tx rpcTx = rpcTransaction.getTx();
    final Transaction domainTransaction = transactionConverter.convertToDomainModel(rpcTx);
    domainTransaction.setBlockHash(Hash.of(rpcTxIdx.getBlockHash().toByteArray()));
    domainTransaction.setIndexInBlock(rpcTxIdx.getIdx());
    return domainTransaction;
  };

  public ModelConverter<Transaction, Blockchain.TxInBlock> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }
}
