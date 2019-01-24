/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.api.model.BytesValue.of;
import static hera.util.VersionUtils.envelop;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Transaction.TxType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class RawTransaction {

  @ApiAudience.Public
  public static RawTransactionWithNothing newBuilder() {
    return new RawTransaction.Builder();
  }

  /**
   * Copy {@code RawTransaction} except for nonce. Nonce is set as passed once.
   *
   * @param rawTransaction a raw transaction to copy
   * @param nonce an new nonce
   * @return a {@code RawTransaction} instance
   */
  @ApiAudience.Public
  public static RawTransaction copyOf(final RawTransaction rawTransaction, final long nonce) {
    return new RawTransaction(rawTransaction.getSender(), rawTransaction.getRecipient(),
        rawTransaction.getAmount(), nonce, rawTransaction.getFee(), rawTransaction.getPayload(),
        rawTransaction.getTxType());
  }

  @Getter
  protected final AccountAddress sender;

  @Getter
  protected final AccountAddress recipient;

  @Getter
  protected final Aer amount;

  @Getter
  protected final Long nonce;

  @Getter
  protected final Fee fee;

  @Getter
  protected final BytesValue payload;

  @Getter
  protected final TxType txType;

  /**
   * RawTransaction constructor.
   *
   * @param sender a sender
   * @param recipient a recipient
   * @param amount an amount
   * @param nonce an nonce
   * @param fee a fee
   * @param payload a payload
   * @param txType a txType
   */
  @ApiAudience.Private
  public RawTransaction(final AccountAddress sender, final AccountAddress recipient,
      final Aer amount, final Long nonce, final Fee fee, final BytesValue payload,
      final TxType txType) {
    this.sender = null != sender ? sender : AccountAddress.of(BytesValue.EMPTY);
    this.recipient = null != recipient ? recipient : AccountAddress.of(BytesValue.EMPTY);
    this.amount = null != amount ? amount : Aer.EMPTY;
    this.nonce = nonce;
    this.fee = null != fee ? fee : Fee.EMPTY;
    this.payload = null != payload ? payload : BytesValue.EMPTY;
    this.txType = null != txType ? txType : TxType.UNRECOGNIZED;
  }

  public interface RawTransactionWithNothing {
    RawTransactionWithSender from(String sender);

    RawTransactionWithSender from(Account sender);

    RawTransactionWithSender from(AccountAddress sender);
  }

  public interface RawTransactionWithSender {
    RawTransactionWithSenderAndRecipient to(String recipientName);

    RawTransactionWithSenderAndRecipient to(Account recipient);

    RawTransactionWithSenderAndRecipient to(AccountAddress recipient);

  }

  public interface RawTransactionWithSenderAndRecipient {
    RawTransactionWithSenderAndRecipientAndAmount amount(String amount, Aer.Unit unit);

    RawTransactionWithSenderAndRecipientAndAmount amount(Aer amount);
  }

  public interface RawTransactionWithSenderAndRecipientAndAmount {
    RawTransactionWithReady nonce(long nonce);
  }

  public interface RawTransactionWithReady extends hera.util.Builder<RawTransaction> {
    RawTransactionWithReady fee(Fee fee);

    RawTransactionWithReady payload(BytesValue payload);
  }

  protected static class Builder
      implements RawTransactionWithNothing, RawTransactionWithSender,
      RawTransactionWithSenderAndRecipient, RawTransactionWithSenderAndRecipientAndAmount,
      RawTransactionWithReady {

    private AccountAddress sender;

    private AccountAddress recipient;

    private Aer amount = Aer.ZERO;

    private long nonce;

    private Fee fee = Fee.ZERO;

    private BytesValue payload = BytesValue.EMPTY;

    private TxType txType = TxType.NORMAL;

    @Override
    public RawTransactionWithSender from(final String sender) {
      this.sender =
          new AccountAddress(of(envelop(sender.getBytes(), AccountAddress.VERSION)));
      return this;
    }

    @Override
    public RawTransactionWithSender from(final Account sender) {
      return from(sender.getAddress());
    }

    @Override
    public RawTransactionWithSender from(final AccountAddress sender) {
      this.sender = sender;
      return this;
    }

    @Override
    public RawTransactionWithSenderAndRecipient to(final String recipient) {
      this.recipient =
          new AccountAddress(of(envelop(recipient.getBytes(), AccountAddress.VERSION)));
      return this;
    }

    @Override
    public RawTransactionWithSenderAndRecipient to(final Account recipient) {
      return to(recipient.getAddress());
    }

    @Override
    public RawTransactionWithSenderAndRecipient to(final AccountAddress recipient) {
      this.recipient = recipient;
      return this;
    }

    @Override
    public RawTransactionWithSenderAndRecipientAndAmount amount(final String amount,
        final Aer.Unit unit) {
      return amount(Aer.of(amount, unit));
    }

    @Override
    public RawTransactionWithSenderAndRecipientAndAmount amount(final Aer amount) {
      this.amount = amount;
      return this;
    }

    @Override
    public RawTransactionWithReady nonce(final long nonce) {
      this.nonce = nonce;
      return this;
    }

    @Override
    public RawTransactionWithReady fee(final Fee fee) {
      this.fee = fee;
      return this;
    }

    @Override
    public RawTransactionWithReady payload(
        final BytesValue payload) {
      this.payload = payload;
      return this;
    }

    @Override
    public RawTransaction build() {
      return new RawTransaction(sender, recipient, amount, nonce, fee, payload, txType);
    }
  }

}
