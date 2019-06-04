/*
 * @copyright defined in LICENSE.txt
 */

package hera.transaction;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.ChainIdHash;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.api.model.internal.GovernanceRecipient;
import hera.spec.PayloadSpec.Type;
import hera.spec.resolver.PayloadResolver;
import hera.transaction.dsl.UpdateNameTransaction;
import hera.transaction.dsl.UpdateNameTransaction.WithChainIdHash;
import hera.transaction.dsl.UpdateNameTransaction.WithChainIdHashAndSender;
import hera.transaction.dsl.UpdateNameTransaction.WithChainIdHashAndSenderAndNonce;
import hera.transaction.dsl.UpdateNameTransaction.WithChainIdHashAndSenderAndNonceAndName;
import hera.transaction.dsl.UpdateNameTransaction.WithReady;

public class UpdateNameTransactionBuilder implements
    UpdateNameTransaction.WithNothing,
    UpdateNameTransaction.WithChainIdHash,
    UpdateNameTransaction.WithChainIdHashAndSender,
    UpdateNameTransaction.WithChainIdHashAndSenderAndNonce,
    UpdateNameTransaction.WithChainIdHashAndSenderAndNonceAndName,
    UpdateNameTransaction.WithReady {

  protected String name;

  protected AccountAddress newOwner;

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
  public WithChainIdHashAndSenderAndNonce nonce(long nonce) {
    this.delegate.nonce(nonce);
    return this;
  }

  @Override
  public WithChainIdHashAndSenderAndNonceAndName name(final String name) {
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
    this.delegate.to(GovernanceRecipient.AERGO_NAME);
    this.delegate.amount(Aer.AERGO_ONE);
    this.delegate.payload(PayloadResolver.resolve(Type.UpdateName, name, newOwner));
    this.delegate.type(TxType.GOVERNANCE);
    return this.delegate.build();
  }

}
