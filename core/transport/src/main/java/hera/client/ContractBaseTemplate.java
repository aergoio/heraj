/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;
import static types.AergoRPCServiceGrpc.newStub;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.function.Function4;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
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
import hera.api.model.RawTransaction;
import hera.api.model.Subscription;
import hera.api.model.Transaction;
import hera.client.PayloadResolver.Type;
import hera.client.grpc.GrpcStreamObserverAdaptor;
import hera.client.grpc.GrpcStreamSubscription;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.ContractInterfaceConverterFactory;
import hera.transport.ContractResultConverterFactory;
import hera.transport.EventConverterFactory;
import hera.transport.EventFilterConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.ReceiptConverterFactory;
import io.grpc.Context;
import io.grpc.ManagedChannel;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class ContractBaseTemplate implements ChannelInjectable, ContextProviderInjectable {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<ContractTxReceipt, Blockchain.Receipt> receiptConverter =
      new ReceiptConverterFactory().create();

  protected final ModelConverter<ContractInterface, Blockchain.ABI> contractInterfaceConverter =
      new ContractInterfaceConverterFactory().create();

  protected final ModelConverter<ContractResult, Rpc.SingleBytes> contractResultConverter =
      new ContractResultConverterFactory().create();

  protected final ModelConverter<EventFilter, Blockchain.FilterInfo> eventFilterConverter =
      new EventFilterConverterFactory().create();

  protected final ModelConverter<Event, Blockchain.Event> eventConverter =
      new EventConverterFactory().create();

  protected AergoRPCServiceFutureStub futureService;
  protected AergoRPCServiceStub streamService;

  protected ContextProvider contextProvider;

  protected AccountBaseTemplate accountBaseTemplate = new AccountBaseTemplate();

  protected TransactionBaseTemplate transactionBaseTemplate = new TransactionBaseTemplate();

  protected PayloadResolver payloadResolver = new PayloadResolver();

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.futureService = newFutureStub(channel);
    this.streamService = newStub(channel);
    accountBaseTemplate.setChannel(channel);
    transactionBaseTemplate.setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    accountBaseTemplate.setContextProvider(contextProvider);
    transactionBaseTemplate.setContextProvider(contextProvider);
  }

  @Getter
  private final Function1<ContractTxHash, FinishableFuture<ContractTxReceipt>> receiptFunction =
      new Function1<ContractTxHash, FinishableFuture<ContractTxReceipt>>() {

        @Override
        public hera.client.FinishableFuture<ContractTxReceipt> apply(
            final ContractTxHash deployTxHash) {
          logger.debug("Get receipt with txHash: {}", deployTxHash);

          FinishableFuture<ContractTxReceipt> nextFuture =
              new FinishableFuture<ContractTxReceipt>();
          try {
            final Rpc.SingleBytes rpcDeployTxHash = Rpc.SingleBytes.newBuilder()
                .setValue(copyFrom(deployTxHash.getBytesValue()))
                .build();
            logger.trace("AergoService getReceipt arg: {}", rpcDeployTxHash);

            final ListenableFuture<Blockchain.Receipt> listenableFuture =
                futureService.getReceipt(rpcDeployTxHash);
            FutureChain<Blockchain.Receipt, ContractTxReceipt> callback =
                new FutureChain<Blockchain.Receipt, ContractTxReceipt>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<Blockchain.Receipt, ContractTxReceipt>() {

              @Override
              public ContractTxReceipt apply(final Blockchain.Receipt receipt) {
                return receiptConverter.convertToDomainModel(receipt);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function4<Account, ContractDefinition, Long, Fee,
      FinishableFuture<ContractTxHash>> deployFunction = new Function4<Account, ContractDefinition,
          Long, Fee, FinishableFuture<ContractTxHash>>() {

        @Override
        public FinishableFuture<ContractTxHash> apply(final Account creator,
            final ContractDefinition contractDefinition, final Long nonce,
            final Fee fee) {
          logger.debug("Deploy contract with creator: {}, definition: {}, nonce: {}, fee: {}",
              creator.getAddress(), contractDefinition, nonce, fee);
          try {
            final RawTransaction rawTransaction = RawTransaction.newBuilder()
                .from(creator)
                .to(AccountAddress.of(BytesValue.EMPTY))
                .amount(Aer.ZERO)
                .nonce(nonce)
                .fee(fee)
                .payload(payloadResolver.resolve(Type.ContractDefinition, contractDefinition))
                .build();
            return signAndCommit(creator, rawTransaction);
          } catch (Exception e) {
            FinishableFuture<ContractTxHash> next = new FinishableFuture<ContractTxHash>();
            next.fail(e);
            return next;
          }
        }
      };

  @Getter
  private final Function1<ContractAddress,
      FinishableFuture<ContractInterface>> contractInterfaceFunction = new Function1<
          ContractAddress, FinishableFuture<ContractInterface>>() {

        @Override
        public FinishableFuture<ContractInterface> apply(
            final ContractAddress contractAddress) {
          logger.debug("Get contract interface with contract address: {}", contractAddress);

          FinishableFuture<ContractInterface> nextFuture =
              new FinishableFuture<ContractInterface>();
          try {
            final Rpc.SingleBytes rpcContractAddress = Rpc.SingleBytes.newBuilder()
                .setValue(accountAddressConverter.convertToRpcModel(contractAddress))
                .build();
            logger.trace("AergoService getABI arg: {}", rpcContractAddress);

            final ListenableFuture<Blockchain.ABI> listenableFuture =
                futureService.getABI(rpcContractAddress);
            FutureChain<Blockchain.ABI, ContractInterface> callback =
                new FutureChain<Blockchain.ABI, ContractInterface>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<Blockchain.ABI, ContractInterface>() {

              @Override
              public ContractInterface apply(final Blockchain.ABI abi) {
                final ContractInterface withoutAddress =
                    contractInterfaceConverter.convertToDomainModel(abi);
                return new ContractInterface(contractAddress, withoutAddress.getVersion(),
                    withoutAddress.getLanguage(), withoutAddress.getFunctions());
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function4<Account, ContractInvocation, Long, Fee,
      FinishableFuture<ContractTxHash>> executeFunction = new Function4<Account, ContractInvocation,
          Long, Fee, FinishableFuture<ContractTxHash>>() {

        @Override
        public FinishableFuture<ContractTxHash> apply(final Account executor,
            final ContractInvocation contractInvocation, final Long nonce,
            final Fee fee) {
          logger.debug("Execute contract with executor: {}, invocation: {}, nonce: {}, fee: {}",
              executor.getAddress(), contractInvocation, nonce, fee);
          try {
            final RawTransaction rawTransaction = RawTransaction.newBuilder()
                .from(executor)
                .to(contractInvocation.getAddress())
                .amount(Aer.ZERO)
                .nonce(nonce)
                .fee(fee)
                .payload(payloadResolver.resolve(Type.ContractInvocation, contractInvocation))
                .build();
            return signAndCommit(executor, rawTransaction);
          } catch (Exception e) {
            FinishableFuture<ContractTxHash> next = new FinishableFuture<ContractTxHash>();
            next.fail(e);
            return next;
          }
        }
      };

  protected FinishableFuture<ContractTxHash> signAndCommit(final Account account,
      final RawTransaction transaction) {
    final FinishableFuture<ContractTxHash> contractTxHash = new FinishableFuture<ContractTxHash>();

    final FinishableFuture<Transaction> signed =
        accountBaseTemplate.getSignFunction().apply(account, transaction);
    addCallback(signed, new FutureCallback<Transaction>() {

      @Override
      public void onSuccess(final Transaction signed) {
        try {
          contractTxHash.success(transactionBaseTemplate.getCommitFunction().apply(signed).get()
              .adapt(ContractTxHash.class));
        } catch (Exception e) {
          contractTxHash.fail(e);
        }
      }

      @Override
      public void onFailure(final Throwable t) {
        contractTxHash.fail(t);
      }
    }, directExecutor());

    return contractTxHash;
  }

  @Getter
  private final Function1<ContractInvocation, FinishableFuture<ContractResult>> queryFunction =
      new Function1<ContractInvocation, FinishableFuture<ContractResult>>() {

        @Override
        public FinishableFuture<ContractResult> apply(final ContractInvocation contractInvocation) {
          logger.debug("Query contract with invocation: {}", contractInvocation);

          final FinishableFuture<ContractResult> nextFuture =
              new FinishableFuture<ContractResult>();
          try {
            final ByteString rpcContractAddress =
                accountAddressConverter.convertToRpcModel(contractInvocation.getAddress());
            final BytesValue rpcContractInvocation =
                payloadResolver.resolve(Type.ContractInvocation, contractInvocation);
            final Blockchain.Query rpcQuery = Blockchain.Query.newBuilder()
                .setContractAddress(rpcContractAddress)
                .setQueryinfo(copyFrom(rpcContractInvocation))
                .build();
            logger.trace("AergoService queryContract arg: {}", rpcQuery);

            final ListenableFuture<Rpc.SingleBytes> listenableFuture =
                futureService.queryContract(rpcQuery);
            FutureChain<Rpc.SingleBytes, ContractResult> callback =
                new FutureChain<Rpc.SingleBytes, ContractResult>(nextFuture, contextProvider.get());
            callback.setSuccessHandler(new Function1<Rpc.SingleBytes, ContractResult>() {

              @Override
              public ContractResult apply(final Rpc.SingleBytes rawQueryResult) {
                return contractResultConverter.convertToDomainModel(rawQueryResult);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function1<EventFilter, FinishableFuture<List<Event>>> listEventFunction =
      new Function1<EventFilter, FinishableFuture<List<Event>>>() {

        @Override
        public FinishableFuture<List<Event>> apply(final EventFilter eventFilter) {
          logger.debug("List event with filter: {}", eventFilter);

          final FinishableFuture<List<Event>> nextFuture =
              new FinishableFuture<List<Event>>();
          try {
            final Blockchain.FilterInfo rpcEventFilter =
                eventFilterConverter.convertToRpcModel(eventFilter);
            logger.trace("AergoService listEvents arg: {}", rpcEventFilter);

            final ListenableFuture<Rpc.EventList> listenableFuture =
                futureService.listEvents(rpcEventFilter);
            FutureChain<Rpc.EventList, List<Event>> callback =
                new FutureChain<Rpc.EventList, List<Event>>(nextFuture, contextProvider.get());
            callback.setSuccessHandler(new Function1<Rpc.EventList, List<Event>>() {

              @Override
              public List<Event> apply(final Rpc.EventList rawEventList) {
                final List<Event> domainEvents = new ArrayList<Event>();
                for (Blockchain.Event rpcEvent : rawEventList.getEventsList()) {
                  domainEvents.add(eventConverter.convertToDomainModel(rpcEvent));
                }
                return domainEvents;
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function2<EventFilter, hera.api.model.StreamObserver<Event>,
      Subscription<Event>> subscribeEventFunction = new Function2<EventFilter,
          hera.api.model.StreamObserver<Event>, Subscription<Event>>() {

        @Override
        public Subscription<Event> apply(final EventFilter filter,
            final hera.api.model.StreamObserver<Event> observer) {
          logger.debug("Event subsribe with filter: {}, observer: {}", filter, observer);

          final Blockchain.FilterInfo filterInfo = eventFilterConverter.convertToRpcModel(filter);
          final io.grpc.stub.StreamObserver<Blockchain.Event> adaptor =
              new GrpcStreamObserverAdaptor<Blockchain.Event, Event>(observer, eventConverter);
          Context.CancellableContext cancellableContext = Context.current().withCancellation();
          cancellableContext.run(new Runnable() {
            @Override
            public void run() {
              streamService.listEventStream(filterInfo, adaptor);
            }
          });

          return new GrpcStreamSubscription<Event>(cancellableContext);
        }
      };

}
