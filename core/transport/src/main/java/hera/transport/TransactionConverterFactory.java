/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static com.google.protobuf.ByteString.copyFrom;
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

  protected final Logger logger = getLogger(getClass());

  protected final Function<Transaction, Tx> domainConverter = domainTransaction -> {
    logger.trace("Domain status: {}", domainTransaction);

    final TxBody.Builder txBodyBuilder = TxBody.newBuilder();
    txBodyBuilder.setNonce(domainTransaction.getNonce());
    txBodyBuilder.setAmount(domainTransaction.getAmount());
    txBodyBuilder.setAccount(copyFrom(domainTransaction.getSender().getValue()));
    txBodyBuilder.setRecipient(copyFrom(domainTransaction.getRecipient().getValue()));

    final Tx.Builder txBuilder = Tx.newBuilder();
    ofNullable(domainTransaction.getSignature()).ifPresent(signature -> {
      txBodyBuilder.setSign(copyFrom(signature.getSign().getValue()));
      txBuilder.setHash(copyFrom(signature.getHash().getValue()));
    });
    txBuilder.setBody(txBodyBuilder.build());

    return txBuilder.build();
  };

  protected final Function<Tx, Transaction> rpcConverter = rpcTransaction -> {
    logger.trace("Blockchain status: {}", rpcTransaction);

    final Transaction domainTransaction = new Transaction();
    domainTransaction.setNonce(rpcTransaction.getBody().getNonce());
    domainTransaction.setAmount(rpcTransaction.getBody().getAmount());
    domainTransaction.setSender(
        AccountAddress.of(rpcTransaction.getBody().getAccount().toByteArray()));
    domainTransaction.setRecipient(
        AccountAddress.of(rpcTransaction.getBody().getRecipient().toByteArray()));
    if (null != rpcTransaction.getHash() || null != rpcTransaction.getBody().getSign()) {
      final Signature signature = new Signature();
      ofNullable(rpcTransaction.getHash())
          .map(ByteString::toByteArray)
          .map(Hash::new)
          .ifPresent(signature::setHash);
      ofNullable(rpcTransaction.getBody().getSign())
          .map(ByteString::toByteArray).ifPresent(BytesValue::of);
      domainTransaction.setSignature(signature);
    }
    return domainTransaction;
  };

  public ModelConverter<Transaction, Tx> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }
}
