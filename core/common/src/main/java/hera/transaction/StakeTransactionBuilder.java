/*
 * @copyright defined in LICENSE.txt
 */

package hera.transaction;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.ChainIdHash;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.api.model.internal.GovernanceRecipient;
import hera.spec.PayloadSpec.Type;
import hera.spec.resolver.PayloadResolver;
import hera.transaction.dsl.StakeTransaction;
import hera.transaction.dsl.StakeTransaction.WithChainIdHash;
import hera.transaction.dsl.StakeTransaction.WithChainIdHashAndSender;
import hera.transaction.dsl.StakeTransaction.WithChainIdHashAndSenderAndAmount;
import hera.transaction.dsl.StakeTransaction.WithReady;

public class StakeTransactionBuilder implements
    StakeTransaction.WithNothing,
    StakeTransaction.WithChainIdHash,
    StakeTransaction.WithChainIdHashAndSender,
    StakeTransaction.WithChainIdHashAndSenderAndAmount,
    StakeTransaction.WithReady {

  protected final PlainTransactionBuilder delegate = new PlainTransactionBuilder();

  @Override
  public WithChainIdHash chainIdHash(final ChainIdHash chainIdHash) {
    this.delegate.chainIdHash(chainIdHash);
    return this;
  }

  @Override
  public WithChainIdHashAndSender from(final String senderName) {
    this.delegate.from(senderName);
    return this;
  }

  @Override
  public WithChainIdHashAndSender from(final Account sender) {
    this.delegate.from(sender);
    return this;
  }

  @Override
  public WithChainIdHashAndSender from(final AccountAddress sender) {
    this.delegate.from(sender);
    return this;
  }

  @Override
  public WithChainIdHashAndSenderAndAmount amount(final String amount, final Unit unit) {
    this.delegate.amount(amount, unit);
    return this;
  }

  @Override
  public WithChainIdHashAndSenderAndAmount amount(final Aer amount) {
    this.delegate.amount(amount);
    return this;
  }

  @Override
  public WithReady nonce(long nonce) {
    this.delegate.nonce(nonce);
    return this;
  }

  @Override
  public RawTransaction build() {
    this.delegate.to(GovernanceRecipient.AERGO_SYSTEM);
    this.delegate.payload(PayloadResolver.resolve(Type.Stake));
    this.delegate.type(TxType.GOVERNANCE);
    return this.delegate.build();
  }

}
