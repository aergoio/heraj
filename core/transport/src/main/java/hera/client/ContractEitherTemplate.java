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
import hera.api.model.Fee;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.NoStrategyFoundException;
import hera.exception.RpcException;
import hera.strategy.TimeoutStrategy;
import io.grpc.ManagedChannel;

@ApiAudience.Private
@ApiStability.Unstable
public class ContractEitherTemplate implements ContractEitherOperation, ChannelInjectable {

  protected Context context;

  protected ContractAsyncTemplate contractAsyncOperation = new ContractAsyncTemplate();

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
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(contractAsyncOperation.getReceipt(deployTxHash)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<ContractTxHash> deploy(final Account creator,
      final ContractDefinition contractDefinition, final Fee fee) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(contractAsyncOperation.deploy(creator, contractDefinition, fee)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<ContractInterface> getContractInterface(
      final ContractAddress contractAddress) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(contractAsyncOperation.getContractInterface(contractAddress)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<ContractTxHash> execute(final Account executor,
      final ContractInvocation contractInvocation, final Fee fee) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(contractAsyncOperation.execute(executor, contractInvocation, fee)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<ContractResult> query(final ContractInvocation contractInvocation) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(contractAsyncOperation.query(contractInvocation)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

}
