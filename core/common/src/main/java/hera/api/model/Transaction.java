/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.Sha256Utils.digest;
import static java.util.Optional.ofNullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Transaction {

  @Getter
  @Setter
  protected BlockHash blockHash;

  @Getter
  @Setter
  protected int indexInBlock;

  @Getter
  @Setter
  protected long nonce;

  @Getter
  @Setter
  protected AccountAddress sender = new AccountAddress(null);

  @Getter
  @Setter
  protected AccountAddress recipient = new AccountAddress(null);

  @Getter
  @Setter
  protected long amount;

  @Getter
  @Setter
  protected BytesValue payload = new BytesValue(null);

  @Getter
  @Setter
  protected long limit;

  @Getter
  @Setter
  protected long price;

  @Getter
  @Setter
  protected Signature signature;

  @Getter
  @Setter
  protected TransactionType txType = TransactionType.UNRECOGNIZED;

  /**
   * Copy deep.
   *
   * @param source {@link Transaction} to copy
   * @return copied transaction
   */
  public static Transaction copyOf(final Transaction source) {
    if (null == source) {
      return null;
    }
    final Transaction copy = new Transaction();
    copy.setNonce(source.getNonce());
    ofNullable(source.getSender()).ifPresent(copy::setSender);
    ofNullable(source.getRecipient()).ifPresent(copy::setRecipient);
    copy.setAmount(source.getAmount());
    ofNullable(source.getPayload()).ifPresent(copy::setPayload);
    copy.setLimit(source.getLimit());
    copy.setPrice(source.getPrice());
    copy.setSignature(Signature.copyOf(source.getSignature()));

    return copy;
  }

  /**
   * Calculate the hash for this transaction.
   *
   * @return hash value
   */
  public TxHash calculateHash() {
    try {
      final ByteArrayOutputStream raw = new ByteArrayOutputStream();
      final DataOutputStream dataOut = new DataOutputStream(raw);
      dataOut.writeLong(getNonce());
      dataOut.write(getSender().getValue());
      dataOut.write(getRecipient().getValue());
      dataOut.writeLong(getAmount());
      dataOut.write(getPayload().getValue());
      dataOut.writeLong(getLimit());
      dataOut.writeLong(getPrice());
      ofNullable(signature).map(Signature::getSign).map(BytesValue::getValue).ifPresent(b -> {
        try {
          dataOut.write(b);
        } catch (final IOException e) {
          throw new IllegalStateException(e);
        }
      });
      dataOut.flush();
      return new TxHash(digest(raw.toByteArray()));
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    }
  }

}
