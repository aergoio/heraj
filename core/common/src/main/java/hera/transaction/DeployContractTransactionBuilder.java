/*
 * @copyright defined in LICENSE.txt
 */

package hera.transaction;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.ContractDefinition;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.spec.PayloadSpec.Type;
import hera.spec.resolver.PayloadResolver;
import hera.transaction.dsl.DeployContractTransaction;
import hera.transaction.dsl.DeployContractTransaction.WithChainIdHash;
import hera.transaction.dsl.DeployContractTransaction.WithChainIdHashAndSender;
import hera.transaction.dsl.DeployContractTransaction.WithChainIdHashAndSenderAndNonce;
import hera.transaction.dsl.DeployContractTransaction.WithReady;

public class DeployContractTransactionBuilder implements
    DeployContractTransaction.WithNothing,
    DeployContractTransaction.WithChainIdHash,
    DeployContractTransaction.WithChainIdHashAndSender,
    DeployContractTransaction.WithChainIdHashAndSenderAndNonce,
    DeployContractTransaction.WithReady {

  protected ContractDefinition contractDefinition;

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
  public WithReady definition(final ContractDefinition contractDefinition) {
    assertNotNull(contractDefinition);
    this.contractDefinition = contractDefinition;
    return this;
  }

  @Override
  public WithReady fee(final Fee fee) {
    this.delegate.fee(fee);
    return this;
  }

  @Override
  public RawTransaction build() {
    this.delegate.to(AccountAddress.of(BytesValue.EMPTY));
    this.delegate.amount(contractDefinition.getAmount());
    this.delegate.payload(PayloadResolver.resolve(Type.ContractDefinition, contractDefinition));
    return delegate.build();
  }
}
