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
import hera.transaction.dsl.CreateNameTransaction;
import hera.transaction.dsl.CreateNameTransaction.WithChainIdHash;
import hera.transaction.dsl.CreateNameTransaction.WithChainIdHashAndSender;
import hera.transaction.dsl.CreateNameTransaction.WithChainIdHashAndSenderAndNonce;
import hera.transaction.dsl.CreateNameTransaction.WithReady;

public class CreateNameTransactionBuilder implements
    CreateNameTransaction.WithNothing,
    CreateNameTransaction.WithChainIdHash,
    CreateNameTransaction.WithChainIdHashAndSender,
    CreateNameTransaction.WithChainIdHashAndSenderAndNonce,
    CreateNameTransaction.WithReady {

  protected String name;

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
  public WithReady name(final String name) {
    assertNotNull(name);
    this.name = name;
    return this;
  }

  @Override
  public RawTransaction build() {
    this.delegate.to(GovernanceRecipient.AERGO_NAME);
    this.delegate.amount(Aer.AERGO_ONE);
    this.delegate.payload(PayloadResolver.resolve(Type.CreateName, name));
    this.delegate.type(TxType.GOVERNANCE);
    return this.delegate.build();
  }
}
