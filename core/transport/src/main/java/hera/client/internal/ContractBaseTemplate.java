/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;
import static types.AergoRPCServiceGrpc.newStub;

import com.google.protobuf.ByteString;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.function.Function4;
import hera.api.function.Function5;
import hera.api.model.AccountAddress;
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
import hera.api.model.TxHash;
import hera.client.ChannelInjectable;
import hera.client.stream.GrpcStreamObserverAdaptor;
import hera.client.stream.GrpcStreamSubscription;
import hera.key.Signer;
import hera.spec.resolver.PayloadResolver;
import hera.spec.resolver.PayloadSpec.Type;
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
import java.util.concurrent.Future;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class ContractBaseTemplate implements ChannelInjectable, ContextProviderInjectable {

  protected final transient Logger logger = getLogger(getClass());

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
  private final Function1<ContractTxHash, Future<ContractTxReceipt>> receiptFunction =
      new Function1<ContractTxHash, Future<ContractTxReceipt>>() {

        @Override
        public Future<ContractTxReceipt> apply(
            final ContractTxHash deployTxHash) {
          logger.debug("Get receipt with txHash: {}", deployTxHash);

          final Rpc.SingleBytes rpcDeployTxHash = Rpc.SingleBytes.newBuilder()
              .setValue(copyFrom(deployTxHash.getBytesValue()))
              .build();
          logger.trace("AergoService getReceipt arg: {}", rpcDeployTxHash);

          final Future<Blockchain.Receipt> rawFuture = futureService.getReceipt(rpcDeployTxHash);
          final Future<ContractTxReceipt> convertedFuture = HerajFutures.transform(rawFuture,
              new Function1<Blockchain.Receipt, ContractTxReceipt>() {

                @Override
                public ContractTxReceipt apply(final Blockchain.Receipt receipt) {
                  return receiptConverter.convertToDomainModel(receipt);
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function4<Signer, ContractDefinition, Long, Fee,
      Future<ContractTxHash>> deployFunction = new Function4<Signer, ContractDefinition, Long, Fee,
          Future<ContractTxHash>>() {

        @Override
        public Future<ContractTxHash> apply(final Signer signer,
            final ContractDefinition contractDefinition, final Long nonce, final Fee fee) {
          logger.debug("Deploy contract with creator: {}, definition: {}, nonce: {}, fee: {}",
              signer, contractDefinition, nonce, fee);

          final RawTransaction rawTransaction = RawTransaction.newDeployContractBuilder()
              .chainIdHash(contextProvider.get().getChainIdHash())
              .from(signer.getPrincipal())
              .nonce(nonce)
              .definition(contractDefinition)
              .build();
          return signAndCommit(signer, rawTransaction);
        }
      };

  @Getter
  private final Function5<Signer, ContractAddress, ContractDefinition, Long, Fee,
      Future<ContractTxHash>> reDeployFunction = new Function5<Signer, ContractAddress,
          ContractDefinition, Long, Fee, Future<ContractTxHash>>() {

        @Override
        public Future<ContractTxHash> apply(final Signer signer,
            final ContractAddress existingContract, final ContractDefinition contractDefinition,
            final Long nonce, final Fee fee) {
          logger.debug("Re-deploy contract with creator: {}, existing one: {}, "
              + "definition: {}, nonce: {}, fee: {}",
              signer, existingContract, contractDefinition, nonce, fee);
          final RawTransaction rawTransaction = RawTransaction.newReDeployContractBuilder()
              .chainIdHash(contextProvider.get().getChainIdHash())
              .creator(signer.getPrincipal())
              .nonce(nonce)
              .contractAddress(existingContract)
              .definition(contractDefinition)
              .build();
          return signAndCommit(signer, rawTransaction);
        }
      };

  @Getter
  private final Function1<ContractAddress,
      Future<ContractInterface>> contractInterfaceFunction = new Function1<
          ContractAddress, Future<ContractInterface>>() {

        @Override
        public Future<ContractInterface> apply(
            final ContractAddress contractAddress) {
          logger.debug("Get contract interface with contract address: {}", contractAddress);

          final Rpc.SingleBytes rpcContractAddress = Rpc.SingleBytes.newBuilder()
              .setValue(accountAddressConverter.convertToRpcModel(contractAddress))
              .build();
          logger.trace("AergoService getABI arg: {}", rpcContractAddress);

          final Future<Blockchain.ABI> rawFuture = futureService.getABI(rpcContractAddress);
          final Future<ContractInterface> convertedFuture = HerajFutures.transform(rawFuture,
              new Function1<Blockchain.ABI, ContractInterface>() {

                @Override
                public ContractInterface apply(final Blockchain.ABI abi) {
                  final ContractInterface withoutAddress =
                      contractInterfaceConverter.convertToDomainModel(abi);
                  return new ContractInterface(contractAddress, withoutAddress.getVersion(),
                      withoutAddress.getLanguage(), withoutAddress.getFunctions(),
                      withoutAddress.getStateVariables());
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function4<Signer, ContractInvocation, Long, Fee,
      Future<ContractTxHash>> executeFunction = new Function4<Signer, ContractInvocation,
          Long, Fee, Future<ContractTxHash>>() {

        @Override
        public Future<ContractTxHash> apply(final Signer signer,
            final ContractInvocation contractInvocation, final Long nonce,
            final Fee fee) {
          logger.debug("Execute contract with executor: {}, invocation: {}, nonce: {}, fee: {}",
              signer.getPrincipal(), contractInvocation, nonce, fee);
          final RawTransaction rawTransaction = RawTransaction.newInvokeContractBuilder()
              .chainIdHash(contextProvider.get().getChainIdHash())
              .from(signer.getPrincipal())
              .nonce(nonce)
              .invocation(contractInvocation)
              .build();
          return signAndCommit(signer, rawTransaction);
        }
      };

  protected Future<ContractTxHash> signAndCommit(final Signer signer,
      final RawTransaction rawTransaction) {
    final Transaction signed = signer.sign(rawTransaction);
    final Future<TxHash> txHashFuture = transactionBaseTemplate.getCommitFunction().apply(signed);
    return HerajFutures.transform(txHashFuture, new Function1<TxHash, ContractTxHash>() {

      @Override
      public ContractTxHash apply(final TxHash txHash) {
        return txHash.adapt(ContractTxHash.class);
      }
    });
  }

  @Getter
  private final Function1<ContractInvocation, Future<
      ContractResult>> queryFunction = new Function1<ContractInvocation, Future<ContractResult>>() {

        @Override
        public Future<ContractResult> apply(final ContractInvocation contractInvocation) {
          logger.debug("Query contract with invocation: {}", contractInvocation);

          final ByteString rpcContractAddress =
              accountAddressConverter.convertToRpcModel(contractInvocation.getAddress());
          final BytesValue rpcContractInvocation =
              PayloadResolver.resolve(Type.ContractInvocation, contractInvocation);
          final Blockchain.Query rpcQuery = Blockchain.Query.newBuilder()
              .setContractAddress(rpcContractAddress)
              .setQueryinfo(copyFrom(rpcContractInvocation))
              .build();
          logger.trace("AergoService queryContract arg: {}", rpcQuery);

          final Future<Rpc.SingleBytes> rawFuture = futureService.queryContract(rpcQuery);
          final Future<ContractResult> convertedFuture = HerajFutures.transform(rawFuture,
              new Function1<Rpc.SingleBytes, ContractResult>() {

                @Override
                public ContractResult apply(final Rpc.SingleBytes rawQueryResult) {
                  return contractResultConverter.convertToDomainModel(rawQueryResult);
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function1<EventFilter,
      Future<List<Event>>> listEventFunction = new Function1<EventFilter, Future<List<Event>>>() {

        @Override
        public Future<List<Event>> apply(final EventFilter eventFilter) {
          logger.debug("List event with filter: {}", eventFilter);

          final Blockchain.FilterInfo rpcEventFilter =
              eventFilterConverter.convertToRpcModel(eventFilter);
          logger.trace("AergoService listEvents arg: {}", rpcEventFilter);

          final Future<Rpc.EventList> rawFuture = futureService.listEvents(rpcEventFilter);
          final Future<List<Event>> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Rpc.EventList, List<Event>>() {

                @Override
                public List<Event> apply(final Rpc.EventList rawEventList) {
                  final List<Event> domainEvents = new ArrayList<>();
                  for (final Blockchain.Event rpcEvent : rawEventList.getEventsList()) {
                    domainEvents.add(eventConverter.convertToDomainModel(rpcEvent));
                  }
                  return domainEvents;
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function2<EventFilter, hera.api.model.StreamObserver<Event>,
      Future<Subscription<Event>>> subscribeEventFunction = new Function2<EventFilter,
          hera.api.model.StreamObserver<Event>, Future<Subscription<Event>>>() {

        @Override
        public Future<Subscription<Event>> apply(final EventFilter filter,
            final hera.api.model.StreamObserver<Event> observer) {
          logger.debug("Event subsribe with filter: {}, observer: {}", filter, observer);

          final Blockchain.FilterInfo filterInfo =
              eventFilterConverter.convertToRpcModel(filter);
          Context.CancellableContext cancellableContext =
              Context.current().withCancellation();
          final io.grpc.stub.StreamObserver<Blockchain.Event> adaptor =
              new GrpcStreamObserverAdaptor<Blockchain.Event, Event>(cancellableContext,
                  observer, eventConverter);
          cancellableContext.run(new Runnable() {
            @Override
            public void run() {
              streamService.listEventStream(filterInfo, adaptor);
            }
          });

          final Subscription<Event> subscription = new GrpcStreamSubscription<>(cancellableContext);
          return HerajFutures.success(subscription);
        }
      };

}
