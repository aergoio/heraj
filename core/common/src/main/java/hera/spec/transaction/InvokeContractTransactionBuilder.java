/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.transaction;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.ChainIdHash;
import hera.api.model.ContractInvocation;
import hera.api.model.Fee;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.spec.resolver.PayloadResolver;
import hera.spec.resolver.PayloadSpec.Type;
import hera.spec.transaction.dsl.InvokeContractTransaction;
import hera.spec.transaction.dsl.InvokeContractTransaction.WithChainIdHash;
import hera.spec.transaction.dsl.InvokeContractTransaction.WithChainIdHashAndSender;
import hera.spec.transaction.dsl.InvokeContractTransaction.WithChainIdHashAndSenderAndNonce;
import hera.spec.transaction.dsl.InvokeContractTransaction.WithReady;

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
    this.delegate.type(contractInvocation.isDelegateFee() ? TxType.FEE_DELEGATION : TxType.CALL);
    return delegate.build();
  }

}
