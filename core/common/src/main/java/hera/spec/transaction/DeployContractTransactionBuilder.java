/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.transaction;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.AccountAddress;
import hera.api.model.ChainIdHash;
import hera.api.model.ContractDefinition;
import hera.api.model.Fee;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.spec.resolver.PayloadResolver;
import hera.spec.resolver.PayloadSpec.Type;
import hera.spec.transaction.dsl.DeployContractTransaction;
import hera.spec.transaction.dsl.DeployContractTransaction.WithChainIdHash;
import hera.spec.transaction.dsl.DeployContractTransaction.WithChainIdHashAndSender;
import hera.spec.transaction.dsl.DeployContractTransaction.WithChainIdHashAndSenderAndNonce;
import hera.spec.transaction.dsl.DeployContractTransaction.WithReady;

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
    this.delegate.to(AccountAddress.EMPTY);
    this.delegate.amount(contractDefinition.getAmount());
    this.delegate.payload(PayloadResolver.resolve(Type.ContractDefinition, contractDefinition));
    this.delegate.type(TxType.DEPLOY);
    return delegate.build();
  }
}
