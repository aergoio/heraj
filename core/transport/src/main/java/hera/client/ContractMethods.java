/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.Methods.CONTRACT_DEPLOYTX;
import static hera.client.Methods.CONTRACT_EXECUTETX;
import static hera.client.Methods.CONTRACT_INTERFACE;
import static hera.client.Methods.CONTRACT_LIST_EVENT;
import static hera.client.Methods.CONTRACT_QUERY;
import static hera.client.Methods.CONTRACT_TXRECEIPT;
import static hera.client.Methods.CONTRACT_REDEPLOYTX;
import static hera.client.Methods.CONTRACT_SUBSCRIBE_EVENT;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.RequestMethod;
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
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.transaction.ContractInvocationPayloadConverter;
import hera.api.transaction.DeployContractTransactionBuilder;
import hera.api.transaction.InvokeContractTransactionBuilder;
import hera.api.transaction.PayloadConverter;
import hera.api.transaction.ReDeployContractTransactionBuilder;
import hera.key.Signer;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.ContractInterfaceConverterFactory;
import hera.transport.ContractResultConverterFactory;
import hera.transport.ContractTxReceiptConverterFactory;
import hera.transport.EventConverterFactory;
import hera.transport.EventFilterConverterFactory;
import hera.transport.ModelConverter;
import io.grpc.Context;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.Blockchain;
import types.Rpc;

class ContractMethods extends AbstractMethods {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<ContractTxReceipt, Blockchain.Receipt> receiptConverter =
      new ContractTxReceiptConverterFactory().create();

  protected final ModelConverter<ContractInterface, Blockchain.ABI> contractInterfaceConverter =
      new ContractInterfaceConverterFactory().create();

  protected final ModelConverter<ContractResult, Rpc.SingleBytes> contractResultConverter =
      new ContractResultConverterFactory().create();

  protected final ModelConverter<EventFilter, Blockchain.FilterInfo> eventFilterConverter =
      new EventFilterConverterFactory().create();

  protected final ModelConverter<Event, Blockchain.Event> eventConverter =
      new EventConverterFactory().create();

  protected final PayloadConverter<ContractInvocation> payloadConverter =
      new ContractInvocationPayloadConverter();

  protected final TransactionMethods transactionMethods = new TransactionMethods();

  @Getter
  protected final RequestMethod<ContractTxReceipt> contractTxReceipt =
      new RequestMethod<ContractTxReceipt>() {

        @Getter
        protected final String name = CONTRACT_TXRECEIPT;

        @Override
        protected void validate(final List<Object> parameters) {
          validateType(parameters, 0, TxHash.class);
        }

        @Override
        protected ContractTxReceipt runInternal(final List<Object> parameters) throws Exception {
          final TxHash txHash = (TxHash) parameters.get(0);
          logger.debug("Get receipt with txHash: {}", txHash);

          final Rpc.SingleBytes rpcDeployTxHash = Rpc.SingleBytes.newBuilder()
              .setValue(copyFrom(txHash.getBytesValue()))
              .build();
          logger.trace("AergoService getReceipt arg: {}", rpcDeployTxHash);

          final Blockchain.Receipt rpcReceipt = getBlockingStub().getReceipt(rpcDeployTxHash);
          return receiptConverter.convertToDomainModel(rpcReceipt);
        }
      };

  @Getter
  protected final RequestMethod<ContractTxHash> deployTx = new RequestMethod<ContractTxHash>() {

    @Getter
    protected final String name = CONTRACT_DEPLOYTX;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Signer.class);
      validateType(parameters, 1, ContractDefinition.class);
      validateType(parameters, 2, Long.class);
      validateType(parameters, 3, Fee.class);
    }

    @Override
    protected ContractTxHash runInternal(final List<Object> parameters) throws Exception {
      final Signer signer = (Signer) parameters.get(0);
      final ContractDefinition contractDefinition = (ContractDefinition) parameters.get(1);
      final long nonce = (long) parameters.get(2);
      final Fee fee = (Fee) parameters.get(3);
      logger.debug("Deploy contract with creator: {}, definition: {}, nonce: {}, fee: {}",
          signer, contractDefinition, nonce, fee);

      final RawTransaction rawTransaction = new DeployContractTransactionBuilder()
          .chainIdHash(getChainIdHash())
          .from(signer.getPrincipal())
          .definition(contractDefinition)
          .nonce(nonce)
          .fee(fee)
          .build();
      final Transaction signed = signer.sign(rawTransaction);
      return transactionMethods.getCommit().invoke(Arrays.<Object>asList(signed))
          .adapt(ContractTxHash.class);
    }

  };

  @Getter
  protected final RequestMethod<ContractTxHash> redeployTx = new RequestMethod<ContractTxHash>() {

    @Getter
    protected final String name = CONTRACT_REDEPLOYTX;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Signer.class);
      validateType(parameters, 1, ContractAddress.class);
      validateType(parameters, 2, ContractDefinition.class);
      validateType(parameters, 3, Long.class);
      validateType(parameters, 4, Fee.class);
    }

    @Override
    protected ContractTxHash runInternal(final List<Object> parameters) throws Exception {
      final Signer signer = (Signer) parameters.get(0);
      final ContractAddress existingContract = (ContractAddress) parameters.get(1);
      final ContractDefinition contractDefinition = (ContractDefinition) parameters.get(2);
      final long nonce = (long) parameters.get(3);
      final Fee fee = (Fee) parameters.get(4);
      logger.debug("Re-deploy contract with creator: {}, "
              + "existing one: {}, definition: {}, nonce: {}, fee: {}",
          signer, existingContract, contractDefinition, nonce, fee);

      final RawTransaction rawTransaction = new ReDeployContractTransactionBuilder()
          .chainIdHash(getChainIdHash())
          .creator(signer.getPrincipal())
          .contractAddress(existingContract)
          .definition(contractDefinition)
          .nonce(nonce)
          .fee(fee)
          .build();
      final Transaction signed = signer.sign(rawTransaction);
      return transactionMethods.getCommit().invoke(Arrays.<Object>asList(signed))
          .adapt(ContractTxHash.class);
    }
  };

  @Getter
  protected final RequestMethod<ContractInterface> contractInterface =
      new RequestMethod<ContractInterface>() {

        @Getter
        protected final String name = CONTRACT_INTERFACE;

        @Override
        protected void validate(final List<Object> parameters) {
          validateType(parameters, 0, ContractAddress.class);
        }

        @Override
        protected ContractInterface runInternal(final List<Object> parameters) throws Exception {
          final ContractAddress contractAddress = (ContractAddress) parameters.get(0);
          logger.debug("Get contract interface with contract address: {}", contractAddress);

          final Rpc.SingleBytes rpcContractAddress = Rpc.SingleBytes.newBuilder()
              .setValue(accountAddressConverter.convertToRpcModel(contractAddress))
              .build();
          logger.trace("AergoService getABI arg: {}", rpcContractAddress);

          final Blockchain.ABI rpcAbi = getBlockingStub().getABI(rpcContractAddress);
          final ContractInterface withoutAddress =
              contractInterfaceConverter.convertToDomainModel(rpcAbi);
          return new ContractInterface(contractAddress, withoutAddress.getVersion(),
              withoutAddress.getLanguage(), withoutAddress.getFunctions(),
              withoutAddress.getStateVariables());
        }

      };

  @Getter
  protected final RequestMethod<ContractTxHash> executeTx = new RequestMethod<ContractTxHash>() {

    @Getter
    protected final String name = CONTRACT_EXECUTETX;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Signer.class);
      validateType(parameters, 1, ContractInvocation.class);
      validateType(parameters, 2, Long.class);
      validateType(parameters, 3, Fee.class);
    }

    @Override
    protected ContractTxHash runInternal(final List<Object> parameters) throws Exception {
      final Signer signer = (Signer) parameters.get(0);
      final ContractInvocation contractInvocation = (ContractInvocation) parameters.get(1);
      final long nonce = (long) parameters.get(2);
      final Fee fee = (Fee) parameters.get(3);
      logger.debug("Execute contract with executor: {}, invocation: {}, nonce: {}, fee: {}",
          signer.getPrincipal(), contractInvocation, nonce, fee);

      final RawTransaction rawTransaction = new InvokeContractTransactionBuilder()
          .chainIdHash(getChainIdHash())
          .from(signer.getPrincipal())
          .invocation(contractInvocation)
          .nonce(nonce)
          .fee(fee)
          .build();
      final Transaction signed = signer.sign(rawTransaction);
      return transactionMethods.getCommit().invoke(Arrays.<Object>asList(signed))
          .adapt(ContractTxHash.class);
    }
  };

  @Getter
  protected final RequestMethod<ContractResult> query = new RequestMethod<ContractResult>() {

    @Getter
    protected final String name = CONTRACT_QUERY;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, ContractInvocation.class);
    }

    @Override
    protected ContractResult runInternal(final List<Object> parameters) throws Exception {
      final ContractInvocation contractInvocation = (ContractInvocation) parameters.get(0);
      logger.debug("Query contract with invocation: {}", contractInvocation);

      final ByteString rpcContractAddress = accountAddressConverter
          .convertToRpcModel(contractInvocation.getAddress());
      final BytesValue rpcContractInvocation =
          payloadConverter.convertToPayload(contractInvocation);
      final Blockchain.Query rpcQuery = Blockchain.Query.newBuilder()
          .setContractAddress(rpcContractAddress)
          .setQueryinfo(copyFrom(rpcContractInvocation))
          .build();
      logger.trace("AergoService queryContract arg: {}", rpcQuery);

      final Rpc.SingleBytes rawQueryResult = getBlockingStub().queryContract(rpcQuery);
      return contractResultConverter.convertToDomainModel(rawQueryResult);
    }
  };

  @Getter
  protected final RequestMethod<List<Event>> listEvent = new RequestMethod<List<Event>>() {

    @Getter
    protected final String name = CONTRACT_LIST_EVENT;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, EventFilter.class);
    }

    @Override
    protected List<Event> runInternal(final List<Object> parameters) throws Exception {
      final EventFilter eventFilter = (EventFilter) parameters.get(0);
      logger.debug("List event with filter: {}", eventFilter);

      final Blockchain.FilterInfo rpcEventFilter =
          eventFilterConverter.convertToRpcModel(eventFilter);
      logger.trace("AergoService listEvents arg: {}", rpcEventFilter);

      final Rpc.EventList rpcEventList = getBlockingStub().listEvents(rpcEventFilter);
      final List<Event> domainEvents = new LinkedList<>();
      for (final Blockchain.Event rpcEvent : rpcEventList.getEventsList()) {
        domainEvents.add(eventConverter.convertToDomainModel(rpcEvent));
      }
      return domainEvents;
    }

  };

  @Getter
  protected final RequestMethod<Subscription<Event>> subscribeEvent =
      new RequestMethod<Subscription<Event>>() {

        @Getter
        protected final String name = CONTRACT_SUBSCRIBE_EVENT;

        @Override
        protected void validate(final List<Object> parameters) {
          validateType(parameters, 0, EventFilter.class);
          validateType(parameters, 1, StreamObserver.class);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Subscription<Event> runInternal(final List<Object> parameters) throws Exception {
          final EventFilter eventFilter = (EventFilter) parameters.get(0);
          final StreamObserver<Event> streamObserver = (StreamObserver<Event>) parameters.get(1);
          logger.debug("Event subsribe with filter: {}, observer: {}", eventFilter, streamObserver);

          final Blockchain.FilterInfo filterInfo = eventFilterConverter
              .convertToRpcModel(eventFilter);
          logger.trace("Rpc filter: {}", filterInfo);
          Context.CancellableContext cancellableContext =
              Context.current().withCancellation();
          final io.grpc.stub.StreamObserver<Blockchain.Event> adaptor =
              new GrpcStreamObserverAdaptor<>(cancellableContext, streamObserver, eventConverter);
          cancellableContext.run(new Runnable() {

            @Override
            public void run() {
              getStreamStub().listEventStream(filterInfo, adaptor);
            }
          });

          return new GrpcStreamSubscription<Event>(cancellableContext);
        }

      };

}
