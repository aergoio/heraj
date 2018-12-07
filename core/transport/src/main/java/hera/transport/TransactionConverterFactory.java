/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.NumberUtils.byteArrayToPostive;
import static hera.util.NumberUtils.postiveToByteArray;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;
import types.Blockchain.Tx;
import types.Blockchain.TxBody;

public class TransactionConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<Transaction.TxType, Blockchain.TxType> txTypeDomainConverter =
      domainTxType -> {
        switch (domainTxType) {
          case NORMAL:
            return Blockchain.TxType.NORMAL;
          case GOVERNANCE:
            return Blockchain.TxType.GOVERNANCE;
          default:
            return Blockchain.TxType.UNRECOGNIZED;
        }
      };


  protected final Function<Blockchain.TxType, Transaction.TxType> txTypeRpcConverter =
      rpcTxType -> {
        switch (rpcTxType) {
          case NORMAL:
            return Transaction.TxType.NORMAL;
          case GOVERNANCE:
            return Transaction.TxType.GOVERNANCE;
          default:
            return Transaction.TxType.UNRECOGNIZED;
        }
      };

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function<Transaction, Tx> domainConverter = domainTransaction -> {
    logger.trace("Domain transaction: {}", domainTransaction);

    final TxBody.Builder txBodyBuilder = TxBody.newBuilder();
    txBodyBuilder.setNonce(domainTransaction.getNonce());
    txBodyBuilder
        .setAccount(accountAddressConverter.convertToRpcModel(domainTransaction.getSender()));
    txBodyBuilder
        .setRecipient(accountAddressConverter.convertToRpcModel(domainTransaction.getRecipient()));
    txBodyBuilder.setAmount(ByteString.copyFrom(postiveToByteArray(domainTransaction.getAmount())));
    txBodyBuilder.setPayload(copyFrom(domainTransaction.getPayload()));
    txBodyBuilder.setLimit(domainTransaction.getFee().getLimit());
    txBodyBuilder
        .setPrice(ByteString.copyFrom(postiveToByteArray(domainTransaction.getFee().getPrice())));
    if (Transaction.TxType.UNRECOGNIZED != domainTransaction.getTxType()) {
      txBodyBuilder.setType(txTypeDomainConverter.apply(domainTransaction.getTxType()));
    }
    txBodyBuilder.setSign(copyFrom(domainTransaction.getSignature().getSign()));

    final Tx.Builder txBuilder = Tx.newBuilder();
    txBuilder.setBody(txBodyBuilder.build());
    txBuilder.setHash(copyFrom(domainTransaction.getHash().getBytesValue()));

    return txBuilder.build();
  };

  protected final Function<Tx, Transaction> rpcConverter = rpcTransaction -> {
    logger.trace("Rpc transaction: {}", rpcTransaction);

    final TxBody txBody = rpcTransaction.getBody();
    final Transaction domainTransaction = new Transaction();
    domainTransaction.setNonce(txBody.getNonce());
    domainTransaction.setSender(accountAddressConverter.convertToDomainModel(txBody.getAccount()));
    domainTransaction
        .setRecipient(accountAddressConverter.convertToDomainModel(txBody.getRecipient()));
    domainTransaction.setAmount(byteArrayToPostive(txBody.getAmount().toByteArray()));
    domainTransaction.setPayload(BytesValue.of(txBody.getPayload().toByteArray()));
    domainTransaction
        .setFee(new Fee(byteArrayToPostive(txBody.getPrice().toByteArray()), txBody.getLimit()));
    if (null != rpcTransaction.getHash() || null != txBody.getSign()) {
      domainTransaction.setSignature(new Signature(of(txBody.getSign().toByteArray())));
      domainTransaction.setHash(new TxHash(of(rpcTransaction.getHash().toByteArray())));
    }
    domainTransaction.setTxType(txTypeRpcConverter.apply(txBody.getType()));
    return domainTransaction;
  };

  public ModelConverter<Transaction, Tx> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
