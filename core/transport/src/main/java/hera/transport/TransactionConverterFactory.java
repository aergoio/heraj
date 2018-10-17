/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
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
          case COINBASE:
            return Blockchain.TxType.COINBASE;
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
          case COINBASE:
            return Transaction.TxType.COINBASE;
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
    txBodyBuilder.setAmount(domainTransaction.getAmount());
    txBodyBuilder.setPayload(copyFrom(domainTransaction.getPayload()));
    txBodyBuilder.setLimit(domainTransaction.getLimit());
    txBodyBuilder.setPrice(domainTransaction.getPrice());
    if (Transaction.TxType.UNRECOGNIZED != domainTransaction.getTxType()) {
      txBodyBuilder.setType(txTypeDomainConverter.apply(domainTransaction.getTxType()));
    }

    final Tx.Builder txBuilder = Tx.newBuilder();
    ofNullable(domainTransaction.getSignature()).ifPresent(signature -> {
      txBodyBuilder.setSign(copyFrom(signature.getSign()));
      txBuilder.setHash(copyFrom(signature.getTxHash().getBytesValue()));
    });
    txBuilder.setBody(txBodyBuilder.build());

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
    domainTransaction.setAmount(txBody.getAmount());
    domainTransaction.setPayload(BytesValue.of(txBody.getPayload().toByteArray()));
    domainTransaction.setLimit(txBody.getLimit());
    domainTransaction.setPrice(txBody.getPrice());
    if (null != rpcTransaction.getHash() || null != txBody.getSign()) {
      final Signature signature = new Signature();
      ofNullable(rpcTransaction.getHash()).map(ByteString::toByteArray).map(BytesValue::new)
          .map(TxHash::new).ifPresent(signature::setTxHash);
      ofNullable(txBody.getSign()).map(ByteString::toByteArray).map(BytesValue::of)
          .ifPresent(signature::setSign);
      domainTransaction.setSignature(signature);
    }
    domainTransaction.setTxType(txTypeRpcConverter.apply(txBody.getType()));
    return domainTransaction;
  };

  public ModelConverter<Transaction, Tx> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }
}
