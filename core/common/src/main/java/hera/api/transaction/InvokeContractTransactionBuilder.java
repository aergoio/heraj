/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.ChainIdHash;
import hera.api.model.ContractInvocation;
import hera.api.model.Fee;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.api.transaction.dsl.InvokeContractTransaction;
import hera.api.transaction.dsl.InvokeContractTransaction.WithChainIdHash;
import hera.api.transaction.dsl.InvokeContractTransaction.WithChainIdHashAndSender;
import hera.api.transaction.dsl.InvokeContractTransaction.WithChainIdHashAndSenderAndInvocation;
import hera.api.transaction.dsl.InvokeContractTransaction.WithReady;

@ApiAudience.Public
@ApiStability.Unstable
public class InvokeContractTransactionBuilder implements
    InvokeContractTransaction.WithNothing,
    InvokeContractTransaction.WithChainIdHash,
    InvokeContractTransaction.WithChainIdHashAndSender,
    InvokeContractTransaction.WithChainIdHashAndSenderAndInvocation,
    InvokeContractTransaction.WithReady {

  protected final PlainTransactionBuilder delegate = new PlainTransactionBuilder();

  protected final PayloadConverter<ContractInvocation> payloadConverter =
      new ContractInvocationPayloadConverter();

  protected ContractInvocation contractInvocation;

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
  public WithChainIdHashAndSenderAndInvocation invocation(
      final ContractInvocation contractInvocation) {
    assertNotNull(contractInvocation);
    this.contractInvocation = contractInvocation;
    return this;
  }

  @Override
  public WithReady nonce(final long nonce) {
    this.delegate.nonce(nonce);
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
    this.delegate.payload(payloadConverter.convertToPayload(contractInvocation));
    this.delegate.type(contractInvocation.isDelegateFee() ? TxType.FEE_DELEGATION : TxType.CALL);
    return delegate.build();
  }

}
