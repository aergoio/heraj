/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static hera.client.Methods.TRANSACTION_COMMIT;
import static hera.client.Methods.TRANSACTION_IN_BLOCK;
import static hera.client.Methods.TRANSACTION_IN_MEMPOOL;
import static hera.client.Methods.TRANSACTION_SENDTX_BY_ADDRESS;
import static hera.client.Methods.TRANSACTION_SENDTX_BY_NAME;
import static hera.client.Methods.TRANSACTION_TXRECEIPT;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import hera.RequestMethod;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.Name;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.TxReceipt;
import hera.api.transaction.PlainTransactionBuilder;
import hera.exception.CommitException;
import hera.key.Signer;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import hera.transport.TransactionInBlockConverterFactory;
import hera.transport.TxReceiptConverterFactory;
import io.grpc.StatusRuntimeException;
import java.util.Arrays;
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

  protected final ModelConverter<TxReceipt, Blockchain.Receipt> txReceiptConverter =
      new TxReceiptConverterFactory().create();

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
      final TxHash txHash = (TxHash) parameters.get(0);
      logger.debug("Get transaction with txHash: {}", txHash);

      final Rpc.SingleBytes rpcTxHash = Rpc.SingleBytes.newBuilder()
          .setValue(copyFrom(txHash.getBytesValue()))
          .build();
      logger.trace("AergoService getTX arg: {}", rpcTxHash);

      try {
        final Blockchain.Tx rpcTx = getBlockingStub().getTX(rpcTxHash);
        return transactionConverter.convertToDomainModel(rpcTx);
      } catch (StatusRuntimeException e) {
        if (!e.getMessage().contains("not found")) {
          throw e;
        }
        return null;
      }
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
      final TxHash txHash = (TxHash) parameters.get(0);
      logger.debug("Get transaction with txHash: {}", txHash);

      final Rpc.SingleBytes rpcTxHash = Rpc.SingleBytes.newBuilder()
          .setValue(copyFrom(txHash.getBytesValue()))
          .build();
      logger.trace("AergoService getTX arg: {}", rpcTxHash);

      try {
        final Blockchain.TxInBlock rpcTxInBlock = getBlockingStub().getBlockTX(rpcTxHash);
        return transactionInBlockConverter.convertToDomainModel(rpcTxInBlock);
      } catch (StatusRuntimeException e) {
        if (!e.getMessage().contains("not found")) {
          throw e;
        }
        return null;
      }
    }

  };

  @Getter
  private final RequestMethod<TxReceipt> txReceipt = new RequestMethod<TxReceipt>() {

    @Getter
    protected final String name = TRANSACTION_TXRECEIPT;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, TxHash.class);
    }

    @Override
    protected TxReceipt runInternal(final List<Object> parameters) throws Exception {
      final TxHash txHash = (TxHash) parameters.get(0);
      logger.debug("Get transaction with txHash: {}", txHash);

      final Rpc.SingleBytes rpcTxHash = Rpc.SingleBytes.newBuilder()
          .setValue(copyFrom(txHash.getBytesValue()))
          .build();
      logger.trace("AergoService getTX arg: {}", rpcTxHash);

      try {
        final Blockchain.Receipt rpcTxReceipt = getBlockingStub().getReceipt(rpcTxHash);
        return txReceiptConverter.convertToDomainModel(rpcTxReceipt);
      } catch (StatusRuntimeException e) {
        if (!e.getMessage().contains("not found")) {
          throw e;
        }
        return null;
      }
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
      final Transaction transaction = (Transaction) parameters.get(0);
      logger.debug("Commit transaction with transaction: {}", transaction);

      final Blockchain.Tx rpcTx = transactionConverter.convertToRpcModel(transaction);
      final Blockchain.TxList rpcTxList = Blockchain.TxList.newBuilder()
          .addTxs(rpcTx)
          .build();
      logger.trace("AergoService commitTX arg: {}", rpcTxList);

      final Rpc.CommitResultList rpcCommitResultList = getBlockingStub().commitTX(rpcTxList);
      final Rpc.CommitResult rpcCommitResult = rpcCommitResultList.getResultsList().get(0);
      if (Rpc.CommitStatus.TX_OK != rpcCommitResult.getError()) {
        throw new CommitException(rpcCommitResult.getError(),
            rpcCommitResult.getDetail());
      }
      return new TxHash(of(rpcCommitResult.getHash().toByteArray()));
    }

  };

  @Getter
  private final RequestMethod<TxHash> sendTxByAddress = new RequestMethod<TxHash>() {

    @Getter
    protected final String name = TRANSACTION_SENDTX_BY_ADDRESS;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Signer.class);
      validateType(parameters, 1, AccountAddress.class);
      validateType(parameters, 2, Aer.class);
      validateType(parameters, 3, Long.class);
      validateType(parameters, 4, Fee.class);
      validateType(parameters, 5, BytesValue.class);
    }

    @Override
    protected TxHash runInternal(final List<Object> parameters) throws Exception {
      final Signer signer = (Signer) parameters.get(0);
      final AccountAddress recipient = (AccountAddress) parameters.get(1);
      final Aer amount = (Aer) parameters.get(2);
      final long nonce = (long) parameters.get(3);
      final Fee fee = (Fee) parameters.get(4);
      final BytesValue payload = (BytesValue) parameters.get(5);
      logger.debug(
          "Commit transaction with signer: {}, recipient: {},"
              + "amount: {}, nonce: {}, fee: {}, payload: {}",
          signer, recipient, amount, nonce, fee, payload);

      final RawTransaction rawTransaction = new PlainTransactionBuilder()
          .chainIdHash(getChainIdHash())
          .from(signer.getPrincipal())
          .to(recipient)
          .amount(amount)
          .nonce(nonce)
          .fee(fee)
          .payload(payload)
          .build();
      final Transaction signed = signer.sign(rawTransaction);
      return getCommit().invoke(Arrays.<Object>asList(signed));
    }

  };

  @Getter
  private final RequestMethod<TxHash> sendTxByName = new RequestMethod<TxHash>() {

    @Getter
    protected final String name = TRANSACTION_SENDTX_BY_NAME;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Signer.class);
      validateType(parameters, 1, Name.class);
      validateType(parameters, 2, Aer.class);
      validateType(parameters, 3, Long.class);
      validateType(parameters, 4, Fee.class);
      validateType(parameters, 5, BytesValue.class);
    }

    @Override
    protected TxHash runInternal(final List<Object> parameters) throws Exception {
      final Signer signer = (Signer) parameters.get(0);
      final Name recipient = (Name) parameters.get(1);
      final Aer amount = (Aer) parameters.get(2);
      final long nonce = (long) parameters.get(3);
      final Fee fee = (Fee) parameters.get(4);
      final BytesValue payload = (BytesValue) parameters.get(5);
      logger.debug(
          "Commit transaction with signer: {}, recipient: {},"
              + "amount: {}, nonce: {}, fee: {}, payload: {}",
          signer, recipient, amount, nonce, fee, payload);

      final RawTransaction rawTransaction = new PlainTransactionBuilder()
          .chainIdHash(getChainIdHash())
          .from(signer.getPrincipal())
          .to(recipient)
          .amount(amount)
          .nonce(nonce)
          .fee(fee)
          .payload(payload)
          .build();
      final Transaction signed = signer.sign(rawTransaction);
      return getCommit().invoke(Arrays.<Object>asList(signed));
    }

  };

}
