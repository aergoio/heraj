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
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain.Tx;
import types.Blockchain.TxBody;

public class TransactionConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<Transaction, Tx> domainConverter = domainTransaction -> {
    logger.trace("Domain status: {}", domainTransaction);

    final TxBody.Builder txBodyBuilder = TxBody.newBuilder();
    txBodyBuilder.setNonce(domainTransaction.getNonce());
    txBodyBuilder.setAccount(copyFrom(domainTransaction.getSender()));
    txBodyBuilder.setRecipient(copyFrom(domainTransaction.getRecipient()));
    txBodyBuilder.setAmount(domainTransaction.getAmount());
    txBodyBuilder.setPayload(copyFrom(domainTransaction.getPayload()));
    txBodyBuilder.setLimit(domainTransaction.getLimit());
    txBodyBuilder.setPrice(domainTransaction.getPrice());

    final Tx.Builder txBuilder = Tx.newBuilder();
    ofNullable(domainTransaction.getSignature()).ifPresent(signature -> {
      txBodyBuilder.setSign(copyFrom(signature.getSign()));
      txBuilder.setHash(copyFrom(signature.getHash()));
    });
    txBuilder.setBody(txBodyBuilder.build());

    return txBuilder.build();
  };

  protected final Function<Tx, Transaction> rpcConverter = rpcTransaction -> {
    logger.trace("Blockchain status: {}", rpcTransaction);

    final TxBody txBody = rpcTransaction.getBody();
    final Transaction domainTransaction = new Transaction();
    domainTransaction.setNonce(txBody.getNonce());
    domainTransaction.setSender(AccountAddress.of(txBody.getAccount().toByteArray()));
    domainTransaction.setRecipient(AccountAddress.of(txBody.getRecipient().toByteArray()));
    domainTransaction.setAmount(txBody.getAmount());
    domainTransaction.setPayload(BytesValue.of(txBody.getPayload().toByteArray()));
    domainTransaction.setLimit(txBody.getLimit());
    domainTransaction.setPrice(txBody.getPrice());
    if (null != rpcTransaction.getHash() || null != txBody.getSign()) {
      final Signature signature = new Signature();
      ofNullable(rpcTransaction.getHash())
          .map(ByteString::toByteArray)
          .map(Hash::new)
          .ifPresent(signature::setHash);
      ofNullable(txBody.getSign())
          .map(ByteString::toByteArray)
          .map(BytesValue::of)
          .ifPresent(signature::setSign);
      domainTransaction.setSignature(signature);
    }
    return domainTransaction;
  };

  public ModelConverter<Transaction, Tx> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }
}
