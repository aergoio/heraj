/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.transaction;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.ChainIdHash;
import hera.api.model.Identity;
import hera.api.model.Name;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.spec.resolver.PayloadResolver;
import hera.spec.resolver.PayloadSpec.Type;
import hera.spec.transaction.dsl.UpdateNameTransaction;
import hera.spec.transaction.dsl.UpdateNameTransaction.WithChainIdHash;
import hera.spec.transaction.dsl.UpdateNameTransaction.WithChainIdHashAndSender;
import hera.spec.transaction.dsl.UpdateNameTransaction.WithChainIdHashAndSenderAndNonce;
import hera.spec.transaction.dsl.UpdateNameTransaction.WithChainIdHashAndSenderAndNonceAndName;
import hera.spec.transaction.dsl.UpdateNameTransaction.WithReady;

public class UpdateNameTransactionBuilder implements
    UpdateNameTransaction.WithNothing,
    UpdateNameTransaction.WithChainIdHash,
    UpdateNameTransaction.WithChainIdHashAndSender,
    UpdateNameTransaction.WithChainIdHashAndSenderAndNonce,
    UpdateNameTransaction.WithChainIdHashAndSenderAndNonceAndName,
    UpdateNameTransaction.WithReady {

  protected Name name;

  protected AccountAddress newOwner;

  protected final PlainTransactionBuilder delegate = new PlainTransactionBuilder();

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
  public WithReady newOwner(final AccountAddress newOwner) {
    assertNotNull(newOwner);
    this.newOwner = newOwner;
    return this;
  }

  @Override
  public RawTransaction build() {
    this.delegate.to(Name.AERGO_NAME);
    this.delegate.amount(Aer.AERGO_ONE);
    this.delegate.payload(PayloadResolver.resolve(Type.UpdateName, name.getValue(), newOwner));
    this.delegate.type(TxType.GOVERNANCE);
    return this.delegate.build();
  }

}
