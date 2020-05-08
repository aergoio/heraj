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
import hera.api.model.Stake;
import hera.api.model.Transaction.TxType;
import hera.api.transaction.dsl.StakeTransaction;
import hera.api.transaction.dsl.StakeTransaction.WithChainIdHash;
import hera.api.transaction.dsl.StakeTransaction.WithChainIdHashAndSender;
import hera.api.transaction.dsl.StakeTransaction.WithChainIdHashAndSenderAndAmount;
import hera.api.transaction.dsl.StakeTransaction.WithReady;

@ApiAudience.Public
@ApiStability.Unstable
public class StakeTransactionBuilder implements
    StakeTransaction.WithNothing,
    StakeTransaction.WithChainIdHash,
    StakeTransaction.WithChainIdHashAndSender,
    StakeTransaction.WithChainIdHashAndSenderAndAmount,
    StakeTransaction.WithReady {

  protected final PlainTransactionBuilder delegate = new PlainTransactionBuilder();

  protected final PayloadConverter<Stake> payloadConverter = new StakePayloadConverter();

  @Override
  public WithChainIdHash chainIdHash(final ChainIdHash chainIdHash) {
    this.delegate.chainIdHash(chainIdHash);
    return this;
  }

  @Override
  public WithChainIdHashAndSender from(final String sender) {
    this.delegate.from(sender);
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
    this.delegate.payload(payloadConverter.convertToPayload(Stake.newBuilder().build()));
    this.delegate.type(TxType.GOVERNANCE);
    return this.delegate.build();
  }

}
