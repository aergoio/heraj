/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static hera.client.Methods.TRANSACTION_COMMIT;
import static hera.client.Methods.TRANSACTION_IN_BLOCK;
import static hera.client.Methods.TRANSACTION_IN_MEMPOOL;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import hera.RequestMethod;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.RpcCommitException;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import hera.transport.TransactionInBlockConverterFactory;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.Blockchain;
import types.Rpc;

class TransactionMethods extends AbstractMethods {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      new TransactionConverterFactory().create();

  protected final ModelConverter<Transaction, Blockchain.TxInBlock> transactionInBlockConverter =
      new TransactionInBlockConverterFactory().create();

  @Getter
  private final RequestMethod<Transaction> transactionInMemPool = new RequestMethod<Transaction>() {

    @Getter
    protected final String name = TRANSACTION_IN_MEMPOOL;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, TxHash.class);
    }

    @Override
    protected Transaction runInternal(final List<Object> parameters) throws Exception {
      logger.debug("Get transaction with {}", parameters);
      final TxHash txHash = (TxHash) parameters.get(0);

      final Rpc.SingleBytes rpcTxHash = Rpc.SingleBytes.newBuilder()
          .setValue(copyFrom(txHash.getBytesValue()))
          .build();
      logger.trace("AergoService getTX arg: {}", rpcTxHash);

      final Blockchain.Tx rpcTx = getBlockingStub().getTX(rpcTxHash);
      return transactionConverter.convertToDomainModel(rpcTx);
    }

  };

  @Getter
  private final RequestMethod<Transaction> transactionInBlock = new RequestMethod<Transaction>() {

    @Getter
    protected final String name = TRANSACTION_IN_BLOCK;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, TxHash.class);
    }

    @Override
    protected Transaction runInternal(final List<Object> parameters) throws Exception {
      logger.debug("Get transaction with {}", parameters);
      final TxHash txHash = (TxHash) parameters.get(0);

      final Rpc.SingleBytes rpcTxHash = Rpc.SingleBytes.newBuilder()
          .setValue(copyFrom(txHash.getBytesValue()))
          .build();
      logger.trace("AergoService getTX arg: {}", rpcTxHash);

      final Blockchain.TxInBlock rpcTxInBlock = getBlockingStub().getBlockTX(rpcTxHash);
      return transactionInBlockConverter.convertToDomainModel(rpcTxInBlock);
    }

  };

  @Getter
  private final RequestMethod<TxHash> commit = new RequestMethod<TxHash>() {

    @Getter
    protected final String name = TRANSACTION_COMMIT;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Transaction.class);
    }

    @Override
    protected TxHash runInternal(final List<Object> parameters) throws Exception {
      logger.debug("Commit transaction with {}", parameters);
      final Transaction transaction = (Transaction) parameters.get(0);

      final Blockchain.Tx rpcTx = transactionConverter.convertToRpcModel(transaction);
      final Blockchain.TxList rpcTxList = Blockchain.TxList.newBuilder()
          .addTxs(rpcTx)
          .build();
      logger.trace("AergoService commitTX arg: {}", rpcTxList);

      final Rpc.CommitResultList rpcCommitResultList = getBlockingStub().commitTX(rpcTxList);
      final Rpc.CommitResult rpcCommitResult = rpcCommitResultList.getResultsList().get(0);
      if (Rpc.CommitStatus.TX_OK != rpcCommitResult.getError()) {
        throw new RpcCommitException(rpcCommitResult.getError(),
            rpcCommitResult.getDetail());
      }
      return new TxHash(of(rpcCommitResult.getHash().toByteArray()));
    }

  };

}
