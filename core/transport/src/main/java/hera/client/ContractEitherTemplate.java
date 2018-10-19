/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
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
import hera.api.model.Time;
import hera.api.tupleorerror.ResultOrError;
import hera.strategy.TimeoutStrategy;
import io.grpc.ManagedChannel;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class ContractEitherTemplate implements ContractEitherOperation, ChannelInjectable {

  protected Context context;

  protected ContractAsyncTemplate contractAsyncOperation = new ContractAsyncTemplate();

  @Getter(lazy = true)
  private final Time timeout =
      context.getStrategy(TimeoutStrategy.class).map(TimeoutStrategy::getTimeout).get();

  @Override
  public void setContext(final Context context) {
    this.context = context;
    this.contractAsyncOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    this.contractAsyncOperation.injectChannel(channel);
  }

  @Override
  public ResultOrError<ContractTxReceipt> getReceipt(final ContractTxHash deployTxHash) {
    return contractAsyncOperation.getReceipt(deployTxHash).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<ContractTxHash> deploy(final Account creator,
      final ContractDefinition contractDefinition) {
    return contractAsyncOperation.deploy(creator, contractDefinition).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<ContractInterface> getContractInterface(
      final ContractAddress contractAddress) {
    return contractAsyncOperation.getContractInterface(contractAddress).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<ContractTxHash> execute(final Account executor,
      final ContractInvocation contractInvocation) {
    return contractAsyncOperation.execute(executor, contractInvocation).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<ContractResult> query(final ContractInvocation contractInvocation) {
    return contractAsyncOperation.query(contractInvocation).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

}
