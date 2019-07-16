/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.transaction;

import static hera.api.model.BytesValue.of;
import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.VersionUtils.envelop;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.spec.resolver.AddressSpec;
import hera.spec.transaction.dsl.PlainTransaction;

public class PlainTransactionBuilder implements
    PlainTransaction.WithNothing,
    PlainTransaction.WithChainIdHash,
    PlainTransaction.WithChainIdHashAndSender,
    PlainTransaction.WithChainIdHashAndSenderAndRecipient,
    PlainTransaction.WithChainIdHashAndSenderAndRecipientAndAmount,
    PlainTransaction.WithReady {

  private ChainIdHash chainIdHash;

  private AccountAddress sender;

  private AccountAddress recipient;

  private Aer amount = Aer.EMPTY;

  private long nonce;


  // following fields aren't need to be explicitly provided

  private Fee fee = Fee.ZERO;

  private BytesValue payload = BytesValue.EMPTY;

  private TxType txType = TxType.NORMAL;


  @Override
  public PlainTransaction.WithChainIdHash chainIdHash(final ChainIdHash chainIdHash) {
    assertNotNull(chainIdHash);
    this.chainIdHash = chainIdHash;
    return this;
  }

  @Override
  public PlainTransaction.WithChainIdHashAndSender from(final String sender) {
    assertNotNull(sender);
    this.sender =
        new AccountAddress(of(envelop(sender.getBytes(), AddressSpec.PREFIX)));
    return this;
  }

  @Override
  public PlainTransaction.WithChainIdHashAndSender from(final Account sender) {
    return from(sender.getAddress());
  }

  @Override
  public PlainTransaction.WithChainIdHashAndSender from(final AccountAddress sender) {
    assertNotNull(sender);
    this.sender = sender;
    return this;
  }

  @Override
  public PlainTransaction.WithChainIdHashAndSenderAndRecipient to(final String recipient) {
    assertNotNull(recipient);
    this.recipient =
        new AccountAddress(of(envelop(recipient.getBytes(), AddressSpec.PREFIX)));
    return this;
  }

  @Override
  public PlainTransaction.WithChainIdHashAndSenderAndRecipient to(final Account recipient) {
    return to(recipient.getAddress());
  }

  @Override
  public PlainTransaction.WithChainIdHashAndSenderAndRecipient to(final AccountAddress recipient) {
    assertNotNull(recipient);
    this.recipient = recipient;
    return this;
  }

  @Override
  public PlainTransaction.WithChainIdHashAndSenderAndRecipientAndAmount amount(final String amount,
      final Aer.Unit unit) {
    return amount(Aer.of(amount, unit));
  }

  @Override
  public PlainTransaction.WithChainIdHashAndSenderAndRecipientAndAmount amount(final Aer amount) {
    assertNotNull(amount);
    this.amount = amount;
    return this;
  }

  @Override
  public PlainTransaction.WithReady nonce(final long nonce) {
    this.nonce = nonce;
    return this;
  }

  @Override
  public PlainTransaction.WithReady fee(final Fee fee) {
    assertNotNull(fee);
    this.fee = fee;
    return this;
  }

  @Override
  public PlainTransaction.WithReady payload(final BytesValue payload) {
    assertNotNull(payload);
    this.payload = payload;
    return this;
  }

  @Override
  public PlainTransaction.WithReady type(final TxType txType) {
    this.txType = txType;
    return this;
  }

  @Override
  public RawTransaction build() {
    return new RawTransaction(chainIdHash, sender, recipient, amount, nonce, fee, payload,
        txType);
  }

}
