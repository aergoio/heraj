/*
 * @copyright defined in LICENSE.txt
 */

package hera.transaction;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.ChainIdHash;
import hera.api.model.ContractInvocation;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.spec.PayloadSpec.Type;
import hera.spec.resolver.PayloadResolver;
import hera.transaction.dsl.InvokeContractTransaction;
import hera.transaction.dsl.InvokeContractTransaction.WithChainIdHash;
import hera.transaction.dsl.InvokeContractTransaction.WithChainIdHashAndSender;
import hera.transaction.dsl.InvokeContractTransaction.WithChainIdHashAndSenderAndNonce;
import hera.transaction.dsl.InvokeContractTransaction.WithReady;

public class InvokeContractTransactionBuilder implements
    InvokeContractTransaction.WithNothing,
    InvokeContractTransaction.WithChainIdHash,
    InvokeContractTransaction.WithChainIdHashAndSender,
    InvokeContractTransaction.WithChainIdHashAndSenderAndNonce,
    InvokeContractTransaction.WithReady {

  protected ContractInvocation contractInvocation;

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
  public WithChainIdHashAndSenderAndNonce nonce(final long nonce) {
    this.delegate.nonce(nonce);
    return this;
  }

  @Override
  public WithReady invocation(final ContractInvocation contractInvocation) {
    assertNotNull(contractInvocation);
    this.contractInvocation = contractInvocation;
    return this;
  }

  @Override
  public WithReady fee(final Fee fee) {
    this.delegate.fee(fee);
    return this;
  }

  @Override
  public RawTransaction build() {
    this.delegate.to(contractInvocation.getAddress());
    this.delegate.amount(contractInvocation.getAmount());
    this.delegate.payload(PayloadResolver.resolve(Type.ContractInvocation, contractInvocation));
    return delegate.build();
  }

}
