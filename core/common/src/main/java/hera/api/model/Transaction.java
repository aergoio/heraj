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
  protected long nonce;

  @Getter
  @Setter
  protected AccountAddress sender;

  @Getter
  @Setter
  protected AccountAddress recipient;

  @Getter
  @Setter
  protected long amount;

  @Getter
  @Setter
  protected Signature signature;

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
    copy.setSignature(Signature.copyOf(source.getSignature()));

    return copy;
  }

  /**
   * Calculate the hash for this transaction.
   *
   * @return hash value
   */
  public Hash calculateHash() {
    try {
      final ByteArrayOutputStream raw = new ByteArrayOutputStream();
      final DataOutputStream dataOut = new DataOutputStream(raw);
      dataOut.writeLong(getNonce());
      dataOut.write(getSender().getValue());
      dataOut.write(getRecipient().getValue());
      dataOut.writeLong(getAmount());
      ofNullable(signature).map(Signature::getSign).map(BytesValue::getValue).ifPresent(b -> {
        try {
          dataOut.write(b);
        } catch (final IOException e) {
          throw new IllegalStateException(e);
        }
      });
      dataOut.flush();
      return new Hash(digest(raw.toByteArray()));
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    }
  }

}
