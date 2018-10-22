/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.api.model.BytesValue.of;
import static hera.util.Sha256Utils.digest;
import static java.util.Optional.ofNullable;

import hera.VersionUtils;
import hera.util.LittleEndianDataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Transaction {

  @Getter
  @Setter
  protected BlockHash blockHash = new BlockHash(BytesValue.EMPTY);

  @Getter
  @Setter
  protected int indexInBlock = 0;

  @Getter
  @Setter
  protected boolean confirmed = false;

  @Getter
  @Setter
  protected long nonce;

  @Getter
  protected AccountAddress sender = new AccountAddress(BytesValue.EMPTY);

  @Getter
  protected AccountAddress recipient = new AccountAddress(BytesValue.EMPTY);

  @Getter
  @Setter
  protected long amount;

  @Getter
  @Setter
  protected BytesValue payload = BytesValue.EMPTY;

  @Getter
  @Setter
  protected Fee fee = Fee.getDefaultFee();

  @Getter
  @Setter
  protected Signature signature = Signature.of(BytesValue.EMPTY, new TxHash(BytesValue.EMPTY));

  @Getter
  @Setter
  protected TxType txType = TxType.NORMAL;

  @RequiredArgsConstructor
  public enum TxType {
    UNRECOGNIZED(-1),
    NORMAL(0),
    GOVERNANCE(1);

    @Getter
    private final int intValue;
  }

  public void setSender(final AccountAddress sender) {
    this.sender = sender;
  }

  public void setSender(final Account sender) {
    sender.adapt(AccountAddress.class).ifPresent(this::setSender);
  }

  public void setRecipient(final AccountAddress recipient) {
    this.recipient = recipient;
  }

  public void setRecipient(final Account account) {
    account.adapt(AccountAddress.class).ifPresent(this::setRecipient);
  }

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
    copy.setBlockHash(source.getBlockHash());
    copy.setIndexInBlock(source.getIndexInBlock());
    copy.setConfirmed(source.isConfirmed());
    copy.setNonce(source.getNonce());
    copy.setSender(source.getSender());
    copy.setRecipient(source.getRecipient());
    copy.setAmount(source.getAmount());
    copy.setPayload(source.getPayload());
    copy.setFee(source.getFee());
    copy.setTxType(source.getTxType());
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
      final LittleEndianDataOutputStream dataOut = new LittleEndianDataOutputStream(raw);
      dataOut.writeLong(getNonce());
      dataOut.write(VersionUtils.trim(getSender().getBytesValue().getValue()));
      dataOut.write(VersionUtils.trim(getRecipient().getBytesValue().getValue()));
      dataOut.writeLong(getAmount());
      dataOut.write(getPayload().getValue());
      dataOut.writeLong(getFee().getLimit());
      dataOut.writeLong(getFee().getPrice());
      dataOut.writeInt(getTxType().getIntValue());
      ofNullable(signature).map(Signature::getSign).map(BytesValue::getValue).ifPresent(b -> {
        try {
          dataOut.write(b);
        } catch (final IOException e) {
          throw new IllegalStateException(e);
        }
      });
      dataOut.flush();
      dataOut.close();
      return new TxHash(of(digest(raw.toByteArray())));
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    }
  }

}
