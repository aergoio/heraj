/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.ChainIdHash;
import hera.api.model.Identity;
import hera.api.model.Name;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.api.model.Unstake;
import hera.api.transaction.dsl.UnstakeTransaction;
import hera.api.transaction.dsl.UnstakeTransaction.WithChainIdHash;
import hera.api.transaction.dsl.UnstakeTransaction.WithChainIdHashAndSender;
import hera.api.transaction.dsl.UnstakeTransaction.WithChainIdHashAndSenderAndAmount;
import hera.api.transaction.dsl.UnstakeTransaction.WithReady;

@ApiAudience.Public
@ApiStability.Unstable
public class UnstakeTransactionBuilder implements
    UnstakeTransaction.WithNothing,
    UnstakeTransaction.WithChainIdHash,
    UnstakeTransaction.WithChainIdHashAndSender,
    UnstakeTransaction.WithChainIdHashAndSenderAndAmount,
    UnstakeTransaction.WithReady {

  protected final PlainTransactionBuilder delegate = new PlainTransactionBuilder();

  protected final PayloadConverter<Unstake> payloadConverter = new UnstakePayloadConverter();

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
    this.delegate.payload(payloadConverter.convertToPayload(Unstake.newBuilder().build()));
    this.delegate.type(TxType.GOVERNANCE);
    return this.delegate.build();
  }

}
