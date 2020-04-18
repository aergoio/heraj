/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.Methods.TRANSACTION_TX;

import hera.Context;
import hera.ContextStorage;
import hera.RequestMethod;
import hera.api.TransactionOperation;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

class TransactionTemplate extends AbstractTemplate implements TransactionOperation {

  protected final TransactionMethods transactionMethods = new TransactionMethods();

  protected final RequestMethod<Transaction> convertedTransactionMethod =
      new RequestMethod<Transaction>() {

        @Getter
        protected final String name = TRANSACTION_TX;

        @Override
        protected Transaction runInternal(final List<Object> parameters) throws Exception {
          try {
            return transactionMethods.getTransactionInBlock().invoke(parameters);
          } catch (Exception noTxInBlock) {
            try {
              return transactionMethods.getTransactionInMemPool().invoke(parameters);
            } catch (Exception noTx) {
              throw noTx;
            }
          }
        }
      };

  TransactionTemplate(final ContextStorage<Context> contextStorage) {
    super(contextStorage);
  }

  @Override
  public Transaction getTransaction(final TxHash txHash) {
    return request(convertedTransactionMethod, Arrays.<Object>asList(txHash));
  }

  @Override
  public TxHash commit(final Transaction transaction) {
    return request(transactionMethods.getCommit(), Arrays.<Object>asList(transaction));
  }

}
