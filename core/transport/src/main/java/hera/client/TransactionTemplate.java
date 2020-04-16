/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.ContextStorage;
import hera.RequestMethod;
import hera.api.TransactionOperation;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.Getter;

class TransactionTemplate extends AbstractTemplate implements TransactionOperation {

  protected final TransactionMethods transactionMethods = new TransactionMethods();

  protected final RequestMethod<Transaction> convertedTransactionMethod =
      new RequestMethod<Transaction>() {

        @Getter
        protected final String name = "convertedTx";

        @Override
        protected Transaction runInternal(final List<Object> parameters) throws Exception {
          try {
            return transactionMethods.getTransactionInBlock().invoke(parameters);
          } catch (Exception noTxInBlock) {
            try {
              return transactionMethods.getTransaction().invoke(parameters);
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
    return request(new Callable<Transaction>() {
      @Override
      public Transaction call() throws Exception {
        return requester.request(convertedTransactionMethod
            .toInvocation(Arrays.<Object>asList(txHash)));
      }
    });
  }

  @Override
  public TxHash commit(final Transaction transaction) {
    return request(new Callable<TxHash>() {
      @Override
      public TxHash call() throws Exception {
        return requester.request(transactionMethods
            .getCommit()
            .toInvocation(Arrays.<Object>asList(transaction)));
      }
    });
  }

}
