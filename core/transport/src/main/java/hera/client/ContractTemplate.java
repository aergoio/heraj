/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.ContractOperation;
import hera.api.model.Account;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import io.grpc.ManagedChannel;

@ApiAudience.Private
@ApiStability.Unstable
public class ContractTemplate
    implements ContractOperation, ChannelInjectable, ContextProviderInjectable {

  protected ContractEitherTemplate contractEitherOperation = new ContractEitherTemplate();

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    contractEitherOperation.setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    contractEitherOperation.setContextProvider(contextProvider);
  }

  @Override
  public ContractTxReceipt getReceipt(final ContractTxHash contractTxHash) {
    return contractEitherOperation.getReceipt(contractTxHash).getResult();
  }

  @Override
  public ContractTxHash deploy(final Account creator, final ContractDefinition contractDefinition,
      final Fee fee) {
    return contractEitherOperation.deploy(creator, contractDefinition, fee).getResult();
  }

  @Override
  public ContractInterface getContractInterface(final ContractAddress contractAddress) {
    return contractEitherOperation.getContractInterface(contractAddress).getResult();
  }

  @Override
  public ContractTxHash execute(final Account executor, final ContractInvocation contractInvocation,
      final Fee fee) {
    return contractEitherOperation.execute(executor, contractInvocation, fee).getResult();
  }

  @Override
  public ContractResult query(final ContractInvocation contractInvocation) {
    return contractEitherOperation.query(contractInvocation).getResult();
  }

}
