/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.ChainIdHash;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.api.transaction.dsl.ReDeployContractTransaction;
import hera.api.transaction.dsl.ReDeployContractTransaction.WithChainIdHash;
import hera.api.transaction.dsl.ReDeployContractTransaction.WithChainIdHashAndCreator;
import hera.api.transaction.dsl.ReDeployContractTransaction.WithChainIdHashAndCreatorAndNonce;
import hera.api.transaction.dsl.ReDeployContractTransaction.WithChainIdHashAndCreatorAndNonceAndContractAddress;
import hera.api.transaction.dsl.ReDeployContractTransaction.WithReady;

@ApiAudience.Public
@ApiStability.Unstable
public class ReDeployContractTransactionBuilder implements
    ReDeployContractTransaction.WithNothing,
    ReDeployContractTransaction.WithChainIdHash,
    ReDeployContractTransaction.WithChainIdHashAndCreator,
    ReDeployContractTransaction.WithChainIdHashAndCreatorAndNonce,
    ReDeployContractTransaction.WithChainIdHashAndCreatorAndNonceAndContractAddress,
    ReDeployContractTransaction.WithReady {

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
  public WithChainIdHashAndCreator creator(final String creatorName) {
    this.delegate.from(creatorName);
    return this;
  }

  @Override
  public WithChainIdHashAndCreator creator(final AccountAddress creator) {
    this.delegate.from(creator);
    return this;
  }

  @Override
  public WithChainIdHashAndCreatorAndNonce nonce(final long nonce) {
    this.delegate.nonce(nonce);
    return this;
  }

  @Override
  public WithChainIdHashAndCreatorAndNonceAndContractAddress contractAddress(
      final ContractAddress contractAddress) {
    this.delegate.to(contractAddress);
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
    this.delegate.amount(contractDefinition.getAmount());
    this.delegate.payload(payloadConverter.convertToPayload(contractDefinition));
    this.delegate.type(TxType.REDEPLOY);
    return delegate.build();
  }

}
