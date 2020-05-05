/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.Methods.TRANSACTION_TX;

import hera.Context;
import hera.ContextStorage;
import hera.RequestMethod;
import hera.api.TransactionOperation;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.Name;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.TxReceipt;
import hera.key.Signer;
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
          final Transaction inBlock = transactionMethods.getTransactionInBlock().invoke(parameters);
          if (null != inBlock) {
            return inBlock;
          }

          return transactionMethods.getTransactionInMemPool().invoke(parameters);
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
  public TxReceipt getTxReceipt(TxHash txHash) {
    return request(transactionMethods.getTxReceipt(), Arrays.<Object>asList(txHash));
  }

  @Override
  public TxHash commit(final Transaction transaction) {
    return request(transactionMethods.getCommit(), Arrays.<Object>asList(transaction));
  }

  @Override
  public TxHash sendTx(final Signer signer, final AccountAddress recipient, final Aer amount,
      final long nonce, final Fee fee, final BytesValue payload) {
    return request(transactionMethods.getSendTxByAddress(),
        Arrays.asList(signer, recipient, amount, nonce, fee, payload));
  }

  @Override
  public TxHash sendTx(final Signer signer, final Name recipient, final Aer amount,
      final long nonce, final Fee fee, final BytesValue payload) {
    return request(transactionMethods.getSendTxByName(),
        Arrays.asList(signer, recipient, amount, nonce, fee, payload));
  }

}
