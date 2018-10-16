/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.ContractOperation;
import hera.api.encode.Base58WithCheckSum;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.key.AergoKey;
import io.grpc.ManagedChannel;

@ApiAudience.Private
@ApiStability.Unstable
public class ContractTemplate implements ContractOperation, ChannelInjectable {

  protected Context context;

  protected ContractEitherTemplate contractEitherOperation = new ContractEitherTemplate();

  @Override
  public void setContext(final Context context) {
    this.context = context;
    contractEitherOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    contractEitherOperation.injectChannel(channel);
  }

  @Override
  public ContractTxReceipt getReceipt(final ContractTxHash contractTxHash) {
    return contractEitherOperation.getReceipt(contractTxHash).getResult();
  }

  @Override
  public ContractTxHash deploy(final AergoKey key, final AccountAddress creator, final long nonce,
      final Base58WithCheckSum encodedPayload) {
    return contractEitherOperation.deploy(key, creator, nonce, encodedPayload).getResult();
  }

  @Override
  public ContractInterface getContractInterface(final ContractAddress contractAddress) {
    return contractEitherOperation.getContractInterface(contractAddress).getResult();
  }

  @Override
  public ContractTxHash execute(final AergoKey key, final AccountAddress executor, final long nonce,
      final ContractInvocation contractInvocation) {
    return contractEitherOperation.execute(key, executor, nonce, contractInvocation).getResult();
  }

  @Override
  public ContractResult query(final ContractInvocation contractInvocation) {
    return contractEitherOperation.query(contractInvocation).getResult();
  }



}
