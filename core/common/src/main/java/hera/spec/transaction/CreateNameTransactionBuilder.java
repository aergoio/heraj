/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.transaction;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.Aer;
import hera.api.model.ChainIdHash;
import hera.api.model.Identity;
import hera.api.model.Name;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.spec.resolver.PayloadResolver;
import hera.spec.resolver.PayloadSpec.Type;
import hera.spec.transaction.dsl.CreateNameTransaction;
import hera.spec.transaction.dsl.CreateNameTransaction.WithChainIdHash;
import hera.spec.transaction.dsl.CreateNameTransaction.WithChainIdHashAndSender;
import hera.spec.transaction.dsl.CreateNameTransaction.WithChainIdHashAndSenderAndNonce;
import hera.spec.transaction.dsl.CreateNameTransaction.WithReady;

public class CreateNameTransactionBuilder implements
    CreateNameTransaction.WithNothing,
    CreateNameTransaction.WithChainIdHash,
    CreateNameTransaction.WithChainIdHashAndSender,
    CreateNameTransaction.WithChainIdHashAndSenderAndNonce,
    CreateNameTransaction.WithReady {

  protected Name name;

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
  public WithReady name(final String name) {
    assertNotNull(name);
    return name(new Name(name));
  }

  @Override
  public WithReady name(final Name name) {
    assertNotNull(name);
    this.name = name;
    return this;
  }

  @Override
  public RawTransaction build() {
    this.delegate.to(Name.AERGO_NAME);
    this.delegate.amount(Aer.AERGO_ONE);
    this.delegate.payload(PayloadResolver.resolve(Type.CreateName, name.getValue()));
    this.delegate.type(TxType.GOVERNANCE);
    return this.delegate.build();
  }
}
