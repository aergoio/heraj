/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.CONTRACT_DEPLOY_EITHER;
import static hera.TransportConstants.CONTRACT_EXECUTE_EITHER;
import static hera.TransportConstants.CONTRACT_GETINTERFACE_EITHER;
import static hera.TransportConstants.CONTRACT_GETRECEIPT_EITHER;
import static hera.TransportConstants.CONTRACT_QUERY_EITHER;
import static hera.api.tupleorerror.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
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
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function4;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import lombok.AccessLevel;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class ContractEitherTemplate
    implements ContractEitherOperation, ChannelInjectable, ContextProviderInjectable {

  @Getter
  protected ContractBaseTemplate contractBaseTemplate = new ContractBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    getContractBaseTemplate().setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    getContractBaseTemplate().setContextProvider(contextProvider);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<ContractTxHash,
      ResultOrErrorFuture<ContractTxReceipt>> receiptFunction = getStrategyChain().apply(
          identify(getContractBaseTemplate().getReceiptFunction(), CONTRACT_GETRECEIPT_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function4<Account, ContractDefinition, Long, Fee,
      ResultOrErrorFuture<ContractTxHash>> deployFunction = getStrategyChain()
          .apply(identify(getContractBaseTemplate().getDeployFunction(), CONTRACT_DEPLOY_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<ContractAddress,
      ResultOrErrorFuture<ContractInterface>> contractInterfaceFunction =
          getStrategyChain()
              .apply(identify(getContractBaseTemplate().getContractInterfaceFunction(),
                  CONTRACT_GETINTERFACE_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function4<Account, ContractInvocation, Long, Fee,
      ResultOrErrorFuture<ContractTxHash>> executeFunction = getStrategyChain()
          .apply(identify(getContractBaseTemplate().getExecuteFunction(), CONTRACT_EXECUTE_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<ContractInvocation, ResultOrErrorFuture<ContractResult>> queryFunction =
      getStrategyChain()
          .apply(identify(getContractBaseTemplate().getQueryFunction(), CONTRACT_QUERY_EITHER));

  @Override
  public ResultOrError<ContractTxReceipt> getReceipt(final ContractTxHash contractTxHash) {
    return getReceiptFunction().apply(contractTxHash).get();
  }

  @Override
  public ResultOrError<ContractTxHash> deploy(final Account creator,
      final ContractDefinition contractDefinition, final long nonce, final Fee fee) {
    return getDeployFunction().apply(creator, contractDefinition, nonce, fee).get();
  }

  @Override
  public ResultOrError<ContractInterface> getContractInterface(
      final ContractAddress contractAddress) {
    return getContractInterfaceFunction().apply(contractAddress).get();
  }

  @Override
  public ResultOrError<ContractTxHash> execute(final Account executor,
      final ContractInvocation contractInvocation, final long nonce, final Fee fee) {
    return getExecuteFunction().apply(executor, contractInvocation, nonce, fee).get();
  }

  @Override
  public ResultOrError<ContractResult> query(final ContractInvocation contractInvocation) {
    return getQueryFunction().apply(contractInvocation).get();
  }

}
