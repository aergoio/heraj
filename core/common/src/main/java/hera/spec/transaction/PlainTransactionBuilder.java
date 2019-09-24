/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.transaction;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.Fee;
import hera.api.model.Identity;
import hera.api.model.Name;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.exception.HerajException;
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
    this.chainIdHash = null == chainIdHash ? new ChainIdHash(BytesValue.EMPTY) : chainIdHash;
    return this;
  }

  @Override
  public PlainTransaction.WithChainIdHashAndSender from(final Identity sender) {
    assertNotNull(sender);
    this.sender = deriveAddress(sender);
    return this;
  }

  @Override
  public PlainTransaction.WithChainIdHashAndSender from(final String sender) {
    assertNotNull(sender);
    this.sender = deriveAddress(sender);
    return this;
  }

  @Override
  public PlainTransaction.WithChainIdHashAndSenderAndRecipient to(final String recipient) {
    assertNotNull(recipient);
    this.recipient = deriveAddress(recipient);
    return this;
  }

  @Override
  public PlainTransaction.WithChainIdHashAndSenderAndRecipient to(final Identity recipient) {
    assertNotNull(recipient);
    this.recipient = deriveAddress(recipient);
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

  protected AccountAddress deriveAddress(final Identity identity) {
    try {
      if (identity instanceof AccountAddress) {
        return (AccountAddress) identity;
      } else if (identity instanceof Name) {
        return ((Name) identity).adapt(AccountAddress.class);
      } else {
        return new AccountAddress(identity.getValue());
      }
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  protected AccountAddress deriveAddress(final String encodedAddressOrName) {
    try {
      final String encodedAddress = encodedAddressOrName;
      return new AccountAddress(encodedAddress);
    } catch (final HerajException notEncodedAddress) {
      try {
        // it's not address, treat it as name
        final String name = encodedAddressOrName;
        return new Name(name).adapt(AccountAddress.class);
      } catch (HerajException e) {
        throw e;
      } catch (Exception e) {
        throw new HerajException(e);
      }
    }
  }

}
