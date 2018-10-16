/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static hera.api.tupleorerror.FunctionChain.fail;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

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
import hera.api.tupleorerror.ResultOrError;
import hera.exception.RpcException;
import hera.key.AergoKey;
import io.grpc.ManagedChannel;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
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
    try {
      return contractAsyncOperation.getReceipt(deployTxHash).get(TIMEOUT, MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<ContractTxHash> deploy(final AergoKey key, final AccountAddress creator,
      final long nonce, final Base58WithCheckSum encodedPayload) {
    try {
      return contractAsyncOperation.deploy(key, creator, nonce, encodedPayload).get(TIMEOUT,
          MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<ContractInterface> getContractInterface(
      final ContractAddress contractAddress) {
    try {
      return contractAsyncOperation.getContractInterface(contractAddress).get(TIMEOUT,
          MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<ContractTxHash> execute(final AergoKey key, final AccountAddress executor,
      final long nonce, final ContractInvocation contractInvocation) {
    try {
      return contractAsyncOperation.execute(key, executor, nonce, contractInvocation).get(TIMEOUT,
          MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<ContractResult> query(final ContractInvocation contractInvocation) {
    try {
      return contractAsyncOperation.query(contractInvocation).get(TIMEOUT, MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

}
