/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.transaction;

import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.ChainIdHash;
import hera.api.model.Identity;
import hera.api.model.Name;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.spec.resolver.PayloadResolver;
import hera.spec.resolver.PayloadSpec.Type;
import hera.spec.transaction.dsl.UnstakeTransaction;
import hera.spec.transaction.dsl.UnstakeTransaction.WithChainIdHash;
import hera.spec.transaction.dsl.UnstakeTransaction.WithChainIdHashAndSender;
import hera.spec.transaction.dsl.UnstakeTransaction.WithChainIdHashAndSenderAndAmount;
import hera.spec.transaction.dsl.UnstakeTransaction.WithReady;

public class UnstakeTransactionBuilder implements
    UnstakeTransaction.WithNothing,
    UnstakeTransaction.WithChainIdHash,
    UnstakeTransaction.WithChainIdHashAndSender,
    UnstakeTransaction.WithChainIdHashAndSenderAndAmount,
    UnstakeTransaction.WithReady {

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
  public WithChainIdHashAndSender from(final Identity sender) {
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
    this.delegate.to(Name.AERGO_SYSTEM);
    this.delegate.payload(PayloadResolver.resolve(Type.Unstake));
    this.delegate.type(TxType.GOVERNANCE);
    return this.delegate.build();
  }

}
