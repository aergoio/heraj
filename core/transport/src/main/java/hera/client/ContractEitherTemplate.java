/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.StrategyAcceptable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.ContractEitherOperation;
import hera.api.model.Account;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.api.tupleorerror.ResultOrError;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;

@ApiAudience.Private
@ApiStability.Unstable
public class ContractEitherTemplate
    implements ContractEitherOperation, ChannelInjectable, StrategyAcceptable {

  protected ContractAsyncTemplate contractAsyncOperation = new ContractAsyncTemplate();

  @Override
  public void accept(final StrategyChain strategyChain) {
    contractAsyncOperation.accept(strategyChain);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    this.contractAsyncOperation.injectChannel(channel);
  }

  @Override
  public ResultOrError<ContractTxReceipt> getReceipt(final ContractTxHash deployTxHash) {
    return contractAsyncOperation.getReceipt(deployTxHash).get();
  }

  @Override
  public ResultOrError<ContractTxHash> deploy(final Account creator,
      final ContractDefinition contractDefinition, final Fee fee) {
    return contractAsyncOperation.deploy(creator, contractDefinition, fee).get();
  }

  @Override
  public ResultOrError<ContractInterface> getContractInterface(
      final ContractAddress contractAddress) {
    return contractAsyncOperation.getContractInterface(contractAddress).get();
  }

  @Override
  public ResultOrError<ContractTxHash> execute(final Account executor,
      final ContractInvocation contractInvocation, final Fee fee) {
    return contractAsyncOperation.execute(executor, contractInvocation, fee).get();
  }

  @Override
  public ResultOrError<ContractResult> query(final ContractInvocation contractInvocation) {
    return contractAsyncOperation.query(contractInvocation).get();
  }

}
