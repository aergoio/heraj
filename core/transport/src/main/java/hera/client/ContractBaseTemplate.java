/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
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
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.ContractInterfaceConverterFactory;
import hera.transport.ContractResultConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.ReceiptConverterFactory;
import hera.util.Base58Utils;
import hera.util.HexUtils;
import hera.util.LittleEndianDataOutputStream;
import hera.util.VersionUtils;
import io.grpc.ManagedChannel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
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

  protected final ObjectMapper objectMapper = new ObjectMapper();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  protected ContextProvider contextProvider;

  protected AccountBaseTemplate accountBaseTemplate = new AccountBaseTemplate();

  protected TransactionBaseTemplate transactionBaseTemplate = new TransactionBaseTemplate();

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
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
          if (logger.isDebugEnabled()) {
            logger.debug("Get receipt, txHash: {}, Context: {}", deployTxHash,
                contextProvider.get());
          }

          FinishableFuture<ContractTxReceipt> nextFuture =
              new FinishableFuture<ContractTxReceipt>();
          try {
            final ByteString byteString = copyFrom(deployTxHash.getBytesValue());
            final Rpc.SingleBytes hashBytes =
                Rpc.SingleBytes.newBuilder().setValue(byteString).build();
            final ListenableFuture<Blockchain.Receipt> listenableFuture =
                aergoService.getReceipt(hashBytes);

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
          if (logger.isDebugEnabled()) {
            logger.debug(
                "Deploy contract, creator: {}, definition: {}, nonce: {}, fee: {}, Context: {}",
                creator.getAddress(), contractDefinition, nonce, fee, contextProvider.get());
          }

          try {
            final RawTransaction rawTransaction = RawTransaction.newBuilder()
                .from(creator)
                .to(AccountAddress.of(BytesValue.EMPTY))
                .amount(Aer.ZERO)
                .nonce(nonce)
                .fee(fee)
                .payload(definitionToPayloadForm(contractDefinition))
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
          if (logger.isDebugEnabled()) {
            logger.debug("Get contract interface, contract address: {}, Context: {}",
                contractAddress,
                contextProvider.get());
          }

          FinishableFuture<ContractInterface> nextFuture =
              new FinishableFuture<ContractInterface>();
          try {
            final ByteString byteString =
                accountAddressConverter.convertToRpcModel(contractAddress);
            final Rpc.SingleBytes hashBytes =
                Rpc.SingleBytes.newBuilder().setValue(byteString).build();
            final ListenableFuture<Blockchain.ABI> listenableFuture =
                aergoService.getABI(hashBytes);

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
          if (logger.isDebugEnabled()) {
            logger.debug(
                "Execute contract, executor: {}, invocation: {}, nonce: {}, fee: {}, Context: {}",
                executor.getAddress(), contractInvocation, nonce, fee, contextProvider.get());
          }

          try {
            final String functionCallString = toFunctionCallJsonString(contractInvocation);
            if (logger.isDebugEnabled()) {
              logger.debug("Raw contract execution address: {}, function: {}",
                  contractInvocation.getAddress(), functionCallString);
            }
            final RawTransaction rawTransaction = RawTransaction.newBuilder()
                .from(executor)
                .to(contractInvocation.getAddress())
                .amount(Aer.ZERO)
                .nonce(nonce)
                .fee(fee)
                .payload(BytesValue.of(functionCallString.getBytes()))
                .build();
            return signAndCommit(executor, rawTransaction);
          } catch (Exception e) {
            FinishableFuture<ContractTxHash> next = new FinishableFuture<ContractTxHash>();
            next.fail(e);
            return next;
          }
        }
      };

  @Getter
  private final Function1<ContractInvocation, FinishableFuture<ContractResult>> queryFunction =
      new Function1<ContractInvocation, FinishableFuture<ContractResult>>() {

        @Override
        public FinishableFuture<ContractResult> apply(final ContractInvocation contractInvocation) {
          if (logger.isDebugEnabled()) {
            logger.debug("Query contract invocation: {}, Context: {}", contractInvocation,
                contextProvider.get());
          }

          final FinishableFuture<ContractResult> nextFuture =
              new FinishableFuture<ContractResult>();
          try {
            final String functionCallString = toFunctionCallJsonString(contractInvocation);
            if (logger.isDebugEnabled()) {
              logger.debug("Raw contract query address: {}, function: {}",
                  contractInvocation.getAddress(),
                  functionCallString);
            }

            final Blockchain.Query query = Blockchain.Query.newBuilder()
                .setContractAddress(
                    accountAddressConverter.convertToRpcModel(contractInvocation.getAddress()))
                .setQueryinfo(ByteString.copyFrom(functionCallString.getBytes())).build();
            final ListenableFuture<Rpc.SingleBytes> listenableFuture =
                aergoService.queryContract(query);

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

  protected BytesValue definitionToPayloadForm(final ContractDefinition contractDefinition)
      throws IOException {
    final byte[] rawPayloadWithVersion =
        Base58Utils.decodeWithCheck(contractDefinition.getEncodedContract());
    if (logger.isTraceEnabled()) {
      logger.trace("Encoded contract deploy payload: {}", contractDefinition.getEncodedContract());
      logger.trace("Decoded contract deploy payload: {}", HexUtils.encode(rawPayloadWithVersion));
    }
    VersionUtils.validate(rawPayloadWithVersion, ContractDefinition.PAYLOAD_VERSION);

    final byte[] rawPaylod = VersionUtils.trim(rawPayloadWithVersion);
    final ByteArrayOutputStream rawStream = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream dataOut = new LittleEndianDataOutputStream(rawStream);
    dataOut.writeInt(rawPaylod.length + 4);
    dataOut.write(rawPaylod);
    if (!contractDefinition.getConstructorArgs().isEmpty()) {
      final ArrayNode constructorArgs = getArgsByJsonArray(contractDefinition.getConstructorArgs());
      if (logger.isDebugEnabled()) {
        logger.debug("Contract constructor args: {}", constructorArgs.toString());
      }
      dataOut.write(constructorArgs.toString().getBytes());
    }
    dataOut.close();
    final BytesValue definitionPayload = BytesValue.of(rawStream.toByteArray());
    return definitionPayload;
  }

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

  protected String toFunctionCallJsonString(final ContractInvocation contractInvocation) {
    final ObjectNode node = objectMapper.createObjectNode();
    node.put("Name", contractInvocation.getFunction().getName());
    node.set("Args", getArgsByJsonArray(contractInvocation.getArgs()));
    return node.toString();
  }

  protected ArrayNode getArgsByJsonArray(final List<Object> args) {
    final ArrayNode argsNode = objectMapper.createArrayNode();
    // nil, boolean, number, string, table?
    for (Object arg : args) {
      if (null == arg) {
        argsNode.addNull();
      } else if (arg instanceof Boolean) {
        argsNode.add((Boolean) arg);
      } else if (arg instanceof Integer) {
        argsNode.add((Integer) arg);
      } else if (arg instanceof Long) {
        argsNode.add((Long) arg);
      } else if (arg instanceof Float) {
        argsNode.add((Float) arg);
      } else if (arg instanceof Double) {
        argsNode.add((Double) arg);
      } else if (arg instanceof BigInteger) {
        argsNode.add(new BigDecimal((BigInteger) arg));
      } else if (arg instanceof BigDecimal) {
        argsNode.add((BigDecimal) arg);
      } else if (arg instanceof String) {
        argsNode.add((String) arg);
      } else {
        throw new IllegalArgumentException("Args type must be number or string");
      }
    }
    return argsNode;
  }

}
