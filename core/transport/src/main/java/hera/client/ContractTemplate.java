/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.function.Functions.identify;
import static hera.client.ClientConstants.CONTRACT_DEPLOY;
import static hera.client.ClientConstants.CONTRACT_EXECUTE;
import static hera.client.ClientConstants.CONTRACT_GETINTERFACE;
import static hera.client.ClientConstants.CONTRACT_GETRECEIPT;
import static hera.client.ClientConstants.CONTRACT_LIST_EVENT;
import static hera.client.ClientConstants.CONTRACT_QUERY;
import static hera.client.ClientConstants.CONTRACT_REDEPLOY;
import static hera.client.ClientConstants.CONTRACT_SUBSCRIBE_EVENT;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.ContractOperation;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.function.Function4;
import hera.api.function.Function5;
import hera.api.model.Account;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.Fee;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.client.internal.ContractBaseTemplate;
import hera.exception.RpcException;
import hera.exception.RpcExceptionConverter;
import hera.key.Signer;
import hera.strategy.PriorityProvider;
import hera.strategy.StrategyApplier;
import hera.util.ExceptionConverter;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.concurrent.Future;
import lombok.AccessLevel;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class ContractTemplate
    implements ContractOperation, ChannelInjectable, ContextProviderInjectable {

  protected final ExceptionConverter<RpcException> exceptionConverter = new RpcExceptionConverter();

  protected ContractBaseTemplate contractBaseTemplate = new ContractBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyApplier strategyApplier =
      StrategyApplier.of(contextProvider.get(), PriorityProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.contractBaseTemplate.setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    this.contractBaseTemplate.setContextProvider(contextProvider);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<ContractTxHash,
      Future<ContractTxReceipt>> receiptFunction = getStrategyApplier().apply(
          identify(contractBaseTemplate.getReceiptFunction(), CONTRACT_GETRECEIPT));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function4<Signer, ContractDefinition, Long, Fee,
      Future<ContractTxHash>> deployFunction =
          getStrategyApplier()
              .apply(identify(contractBaseTemplate.getDeployFunction(), CONTRACT_DEPLOY));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function5<Signer, ContractAddress, ContractDefinition, Long, Fee,
      Future<ContractTxHash>> reDeployFunction =
          getStrategyApplier()
              .apply(identify(contractBaseTemplate.getReDeployFunction(), CONTRACT_REDEPLOY));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<ContractAddress,
      Future<ContractInterface>> contractInterfaceFunction =
          getStrategyApplier().apply(identify(contractBaseTemplate.getContractInterfaceFunction(),
              CONTRACT_GETINTERFACE));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function4<Signer, ContractInvocation, Long, Fee,
      Future<ContractTxHash>> executeFunction =
          getStrategyApplier()
              .apply(identify(contractBaseTemplate.getExecuteFunction(), CONTRACT_EXECUTE));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<ContractInvocation, Future<ContractResult>> queryFunction =
      getStrategyApplier().apply(identify(contractBaseTemplate.getQueryFunction(), CONTRACT_QUERY));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<EventFilter, Future<List<Event>>> listEventFunction =
      getStrategyApplier()
          .apply(identify(contractBaseTemplate.getListEventFunction(), CONTRACT_LIST_EVENT));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<EventFilter, StreamObserver<Event>,
      Future<Subscription<Event>>> subscribeEventFunction =
          getStrategyApplier().apply(
              identify(contractBaseTemplate.getSubscribeEventFunction(), CONTRACT_SUBSCRIBE_EVENT));

  @Override
  public ContractTxReceipt getReceipt(final ContractTxHash contractTxHash) {
    try {
      return getReceiptFunction().apply(contractTxHash).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ContractTxHash deploy(final Account creator, final ContractDefinition contractDefinition,
      final long nonce) {
    return deploy(creator, contractDefinition, nonce, Fee.ZERO);
  }

  @Override
  public ContractTxHash deploy(final Account creator, final ContractDefinition contractDefinition,
      final long nonce, final Fee fee) {
    if (null == creator.getKey()) {
      throw new UnsupportedOperationException();
    }
    return deploy(creator.getKey(), contractDefinition, nonce, fee);
  }

  @Override
  public ContractTxHash deploy(final Signer signer, final ContractDefinition contractDefinition,
      final long nonce, final Fee fee) {
    try {
      return getDeployFunction().apply(signer, contractDefinition, nonce, fee).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ContractTxHash redeploy(final Signer signer, final ContractAddress existingContract,
      final ContractDefinition contractDefinition, final long nonce, final Fee fee) {
    try {
      return getReDeployFunction().apply(signer, existingContract, contractDefinition, nonce, fee)
          .get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ContractInterface getContractInterface(final ContractAddress contractAddress) {
    try {
      return getContractInterfaceFunction().apply(contractAddress).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ContractTxHash execute(final Account executor, final ContractInvocation contractInvocation,
      final long nonce) {
    return execute(executor, contractInvocation, nonce, Fee.ZERO);
  }

  @Override
  public ContractTxHash execute(final Account executor, final ContractInvocation contractInvocation,
      final long nonce, final Fee fee) {
    if (null == executor.getKey()) {
      throw new UnsupportedOperationException();
    }
    return execute(executor.getKey(), contractInvocation, nonce, fee);
  }

  @Override
  public ContractTxHash execute(final Signer signer, final ContractInvocation contractInvocation,
      final long nonce, final Fee fee) {
    try {
      return getExecuteFunction().apply(signer, contractInvocation, nonce, fee).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ContractResult query(final ContractInvocation contractInvocation) {
    try {
      return getQueryFunction().apply(contractInvocation).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<Event> listEvents(final EventFilter filter) {
    try {
      return getListEventFunction().apply(filter).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public Subscription<Event> subscribeEvent(final EventFilter filter,
      final StreamObserver<Event> observer) {
    try {
      return getSubscribeEventFunction().apply(filter, observer).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

}
