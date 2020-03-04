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
import hera.api.model.UnStake;
import hera.api.transaction.dsl.UnStakeTransaction;
import hera.api.transaction.dsl.UnStakeTransaction.WithChainIdHash;
import hera.api.transaction.dsl.UnStakeTransaction.WithChainIdHashAndSender;
import hera.api.transaction.dsl.UnStakeTransaction.WithChainIdHashAndSenderAndAmount;
import hera.api.transaction.dsl.UnStakeTransaction.WithReady;

@ApiAudience.Public
@ApiStability.Unstable
public class UnStakeTransactionBuilder implements
    UnStakeTransaction.WithNothing,
    UnStakeTransaction.WithChainIdHash,
    UnStakeTransaction.WithChainIdHashAndSender,
    UnStakeTransaction.WithChainIdHashAndSenderAndAmount,
    UnStakeTransaction.WithReady {

  protected final PlainTransactionBuilder delegate = new PlainTransactionBuilder();

  protected final PayloadConverter<UnStake> payloadConverter = new UnStakePayloadConverter();

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
    this.delegate.payload(payloadConverter.convertToPayload(new UnStake()));
    this.delegate.type(TxType.GOVERNANCE);
    return this.delegate.build();
  }

}
