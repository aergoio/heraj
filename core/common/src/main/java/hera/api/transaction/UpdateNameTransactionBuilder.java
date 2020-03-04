/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Aer;
import hera.api.model.ChainIdHash;
import hera.api.model.Identity;
import hera.api.model.Name;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.api.model.UpdateName;
import hera.api.transaction.dsl.UpdateNameTransaction;
import hera.api.transaction.dsl.UpdateNameTransaction.WithChainIdHash;
import hera.api.transaction.dsl.UpdateNameTransaction.WithChainIdHashAndSender;
import hera.api.transaction.dsl.UpdateNameTransaction.WithChainIdHashAndSenderAndNonce;
import hera.api.transaction.dsl.UpdateNameTransaction.WithChainIdHashAndSenderAndNonceAndName;
import hera.api.transaction.dsl.UpdateNameTransaction.WithReady;

@ApiAudience.Public
@ApiStability.Unstable
public class UpdateNameTransactionBuilder implements
    UpdateNameTransaction.WithNothing,
    UpdateNameTransaction.WithChainIdHash,
    UpdateNameTransaction.WithChainIdHashAndSender,
    UpdateNameTransaction.WithChainIdHashAndSenderAndNonce,
    UpdateNameTransaction.WithChainIdHashAndSenderAndNonceAndName,
    UpdateNameTransaction.WithReady {

  protected final PlainTransactionBuilder delegate = new PlainTransactionBuilder();

  protected final PayloadConverter<UpdateName> payloadConverter = new UpdateNamePayloadConverter();

  protected Name name;

  protected Identity nextOwner;

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
  public WithChainIdHashAndSenderAndNonce nonce(long nonce) {
    this.delegate.nonce(nonce);
    return this;
  }

  @Override
  public WithChainIdHashAndSenderAndNonceAndName name(final String name) {
    assertNotNull(name);
    return name(new Name(name));
  }

  @Override
  public WithChainIdHashAndSenderAndNonceAndName name(final Name name) {
    assertNotNull(name);
    this.name = name;
    return this;
  }

  @Override
  public WithReady nextOwner(final Identity nextOwner) {
    assertNotNull(nextOwner);
    this.nextOwner = nextOwner;
    return this;
  }

  @Override
  public RawTransaction build() {
    this.delegate.to(Name.AERGO_NAME);
    this.delegate.amount(Aer.AERGO_ONE);
    final UpdateName updateName = UpdateName.newBuilder()
        .name(name)
        .nextOwner(nextOwner)
        .build();
    this.delegate.payload(payloadConverter.convertToPayload(updateName));
    this.delegate.type(TxType.GOVERNANCE);
    return this.delegate.build();
  }

}
