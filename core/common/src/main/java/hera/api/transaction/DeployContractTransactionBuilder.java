/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.ChainIdHash;
import hera.api.model.ContractDefinition;
import hera.api.model.Fee;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.api.transaction.dsl.DeployContractTransaction;
import hera.api.transaction.dsl.DeployContractTransaction.WithChainIdHash;
import hera.api.transaction.dsl.DeployContractTransaction.WithChainIdHashAndSender;
import hera.api.transaction.dsl.DeployContractTransaction.WithChainIdHashAndSenderAndDefinition;
import hera.api.transaction.dsl.DeployContractTransaction.WithReady;


@ApiAudience.Public
@ApiStability.Unstable
public class DeployContractTransactionBuilder implements
    DeployContractTransaction.WithNothing,
    DeployContractTransaction.WithChainIdHash,
    DeployContractTransaction.WithChainIdHashAndSender,
    DeployContractTransaction.WithChainIdHashAndSenderAndDefinition,
    DeployContractTransaction.WithReady {

  protected final PlainTransactionBuilder delegate = new PlainTransactionBuilder();

  protected final PayloadConverter<ContractDefinition> payloadConverter =
      new ContractDefinitionPayloadConverter();

  protected ContractDefinition contractDefinition;

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
  public WithChainIdHashAndSenderAndDefinition definition(
      final ContractDefinition contractDefinition) {
    assertNotNull(contractDefinition);
    this.contractDefinition = contractDefinition;
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
    this.delegate.to(AccountAddress.EMPTY);
    this.delegate.amount(contractDefinition.getAmount());
    this.delegate.payload(payloadConverter.convertToPayload(contractDefinition));
    this.delegate.type(TxType.DEPLOY);
    return delegate.build();
  }

}
