/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.tupleorerror.FunctionChain.fail;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.ContractEitherOperation;
import hera.api.encode.Base58WithCheckSum;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Time;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.RpcException;
import hera.key.AergoKey;
import hera.strategy.TimeoutStrategy;
import io.grpc.ManagedChannel;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
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
    try {
      return contractAsyncOperation.getReceipt(deployTxHash).get(getTimeout().getValue(),
          getTimeout().getUnit());
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<ContractTxHash> deploy(final AergoKey key, final AccountAddress creator,
      final long nonce, final Base58WithCheckSum encodedPayload) {
    try {
      return contractAsyncOperation.deploy(key, creator, nonce, encodedPayload)
          .get(getTimeout().getValue(), getTimeout().getUnit());
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<ContractInterface> getContractInterface(
      final ContractAddress contractAddress) {
    try {
      return contractAsyncOperation.getContractInterface(contractAddress)
          .get(getTimeout().getValue(), getTimeout().getUnit());
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<ContractTxHash> execute(final AergoKey key, final AccountAddress executor,
      final long nonce, final ContractInvocation contractInvocation) {
    try {
      return contractAsyncOperation.execute(key, executor, nonce, contractInvocation)
          .get(getTimeout().getValue(), getTimeout().getUnit());
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<ContractResult> query(final ContractInvocation contractInvocation) {
    try {
      return contractAsyncOperation.query(contractInvocation).get(getTimeout().getValue(),
          getTimeout().getUnit());
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

}
