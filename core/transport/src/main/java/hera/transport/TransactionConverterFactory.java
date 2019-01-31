/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.Transaction;
import org.slf4j.Logger;
import types.Blockchain;

public class TransactionConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<Transaction, Blockchain.TxInBlock> transactionInBlockConverter =
      new TransactionInBlockConverterFactory().create();

  protected final Function1<Transaction, Blockchain.Tx> domainConverter =
      new Function1<Transaction, Blockchain.Tx>() {

        @Override
        public Blockchain.Tx apply(final Transaction domainTransaction) {
          logger.trace("Domain transaction to convert: {}", domainTransaction);
          final Blockchain.TxInBlock rpcTxInBlock =
              transactionInBlockConverter.convertToRpcModel(domainTransaction);
          final Blockchain.Tx rpcTransaction = rpcTxInBlock.getTx();
          logger.trace("Rpc transaction converted: {}", rpcTransaction);
          return rpcTransaction;
        }
      };

  protected final Function1<Blockchain.Tx, Transaction> rpcConverter =
      new Function1<Blockchain.Tx, Transaction>() {

        @Override
        public Transaction apply(final Blockchain.Tx rpcTransaction) {
          logger.trace("Rpc transaction to convert: {}", rpcTransaction);
          final Blockchain.TxInBlock rpcTxInBlock = Blockchain.TxInBlock.newBuilder()
              .setTxIdx(Blockchain.TxIdx.newBuilder().build())
              .setTx(rpcTransaction)
              .build();
          final Transaction domainTransaction =
              transactionInBlockConverter.convertToDomainModel(rpcTxInBlock);
          logger.trace("Domain transaction converted: {}", domainTransaction);
          return domainTransaction;
        }
      };

  public ModelConverter<Transaction, Blockchain.Tx> create() {
    return new ModelConverter<Transaction, Blockchain.Tx>(domainConverter, rpcConverter);
  }

}
