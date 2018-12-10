/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.CONTRACT_DEPLOY_ASYNC;
import static hera.TransportConstants.CONTRACT_EXECUTE_ASYNC;
import static hera.TransportConstants.CONTRACT_GETINTERFACE_ASYNC;
import static hera.TransportConstants.CONTRACT_GETRECEIPT_ASYNC;
import static hera.TransportConstants.CONTRACT_QUERY_ASYNC;
import static hera.api.tupleorerror.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.ContractAsyncOperation;
import hera.api.model.Account;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function4;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@ApiAudience.Private
@ApiStability.Unstable
public class ContractAsyncTemplate
    implements ContractAsyncOperation, ChannelInjectable, ContextProviderInjectable {

  protected ContractBaseTemplate contractBaseTemplate = new ContractBaseTemplate();

  @Setter
  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    contractBaseTemplate.setChannel(channel);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<ContractTxHash,
      ResultOrErrorFuture<ContractTxReceipt>> receiptFunction = getStrategyChain().apply(
          identify(contractBaseTemplate.getReceiptFunction(), CONTRACT_GETRECEIPT_ASYNC));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function4<Account, ContractDefinition, Long, Fee,
      ResultOrErrorFuture<ContractTxHash>> deployFunction =
          getStrategyChain()
              .apply(identify(contractBaseTemplate.getDeployFunction(), CONTRACT_DEPLOY_ASYNC));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<ContractAddress,
      ResultOrErrorFuture<ContractInterface>> contractInterfaceFunction =
          getStrategyChain().apply(identify(contractBaseTemplate.getContractInterfaceFunction(),
              CONTRACT_GETINTERFACE_ASYNC));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function4<Account, ContractInvocation, Long, Fee,
      ResultOrErrorFuture<ContractTxHash>> executeFunction = getStrategyChain()
          .apply(identify(contractBaseTemplate.getExecuteFunction(), CONTRACT_EXECUTE_ASYNC));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<ContractInvocation, ResultOrErrorFuture<ContractResult>> queryFunction =
      getStrategyChain()
          .apply(identify(contractBaseTemplate.getQueryFunction(), CONTRACT_QUERY_ASYNC));

  @Override
  public ResultOrErrorFuture<ContractTxReceipt> getReceipt(final ContractTxHash contractTxHash) {
    return getReceiptFunction().apply(contractTxHash);
  }

  @Override
  public ResultOrErrorFuture<ContractTxHash> deploy(final Account creator,
      ContractDefinition contractDefinition, final long nonce, final Fee fee) {
    return getDeployFunction().apply(creator, contractDefinition, nonce, fee);
  }

  @Override
  public ResultOrErrorFuture<ContractInterface> getContractInterface(
      final ContractAddress contractAddress) {
    return getContractInterfaceFunction().apply(contractAddress);
  }

  @Override
  public ResultOrErrorFuture<ContractTxHash> execute(final Account executor,
      final ContractInvocation contractInvocation, final long nonce, final Fee fee) {
    return getExecuteFunction().apply(executor, contractInvocation, nonce, fee);
  }

  @Override
  public ResultOrErrorFuture<ContractResult> query(final ContractInvocation contractInvocation) {
    return getQueryFunction().apply(contractInvocation);
  }

}
