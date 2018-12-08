/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Transaction;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class TransactionConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<Transaction, Blockchain.TxInBlock> transactionInBlockConverter =
      new TransactionInBlockConverterFactory().create();

  protected final Function<Transaction, Blockchain.Tx> domainConverter = domainTransaction -> {
    logger.trace("Domain transaction: {}", domainTransaction);
    final Blockchain.TxInBlock rpcTxInBlock =
        transactionInBlockConverter.convertToRpcModel(domainTransaction);
    return rpcTxInBlock.getTx();
  };

  protected final Function<Blockchain.Tx, Transaction> rpcConverter = rpcTransaction -> {
    logger.trace("Rpc transaction: {}", rpcTransaction);
    final Blockchain.TxInBlock rpcTxInBlock = Blockchain.TxInBlock.newBuilder()
        .setTxIdx(Blockchain.TxIdx.newBuilder().build())
        .setTx(rpcTransaction)
        .build();
    return transactionInBlockConverter.convertToDomainModel(rpcTxInBlock);
  };

  public ModelConverter<Transaction, Blockchain.Tx> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
