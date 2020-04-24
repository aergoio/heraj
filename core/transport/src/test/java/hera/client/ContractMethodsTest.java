/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_CLIENT;
import static hera.client.ClientContextKeys.GRPC_VALUE_CHAIN_ID_HASH_HOLDER;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.Context;
import hera.ContextHolder;
import hera.EmptyContext;
import hera.api.model.ContractInterface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Event;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.api.model.TxHash;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;
import types.Blockchain;
import types.Rpc;

@PrepareForTest({AergoRPCServiceBlockingStub.class, AergoRPCServiceStub.class})
public class ContractMethodsTest extends AbstractTestCase {

  @Test
  public void testContractTxReceipt() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getReceipt(any(Rpc.SingleBytes.class)))
              .thenReturn(Blockchain.Receipt.newBuilder().build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final ContractMethods contractMethods = new ContractMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyTxHash);
          final ContractTxReceipt contractTxReceipt = contractMethods.getContractTxReceipt()
              .invoke(parameters);
          assertNotNull(contractTxReceipt);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testDeployTx() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.commitTX(any(Blockchain.TxList.class)))
              .thenReturn(Rpc.CommitResultList.newBuilder()
                  .addResults(Rpc.CommitResult.newBuilder().build())
                  .build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final ChainIdHashHolder chainIdHashHolder = new ChainIdHashHolder();
          chainIdHashHolder.put(anyChainIdHash);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient)
              .withValue(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, chainIdHashHolder);
          ContextHolder.attach(context);

          // then
          final ContractMethods contractMethods = new ContractMethods();
          final List<Object> parameters = Arrays.<Object>asList(anySigner, anyDefinition, anyNonce,
              anyFee);
          final TxHash txHash = contractMethods.getDeployTx()
              .invoke(parameters);
          assertNotNull(txHash);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testRedeployTx() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.commitTX(any(Blockchain.TxList.class)))
              .thenReturn(Rpc.CommitResultList.newBuilder()
                  .addResults(Rpc.CommitResult.newBuilder().build())
                  .build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final ChainIdHashHolder chainIdHashHolder = new ChainIdHashHolder();
          chainIdHashHolder.put(anyChainIdHash);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient)
              .withValue(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, chainIdHashHolder);
          ContextHolder.attach(context);

          // then
          final ContractMethods contractMethods = new ContractMethods();
          final List<Object> parameters = Arrays.<Object>asList(anySigner, anyContractAddress,
              anyDefinition, anyNonce, anyFee);
          final TxHash txHash = contractMethods.getRedeployTx()
              .invoke(parameters);
          assertNotNull(txHash);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testContractInterface() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getABI(any(Rpc.SingleBytes.class)))
              .thenReturn(Blockchain.ABI.newBuilder()
                  .addFunctions(Blockchain.Function.newBuilder().build())
                  .build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final ContractMethods contractMethods = new ContractMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyContractAddress);
          final ContractInterface contractInterface = contractMethods.getContractInterface()
              .invoke(parameters);
          assertNotNull(contractInterface);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testExecutetx() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.commitTX(any(Blockchain.TxList.class)))
              .thenReturn(Rpc.CommitResultList.newBuilder()
                  .addResults(Rpc.CommitResult.newBuilder().build())
                  .build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final ChainIdHashHolder chainIdHashHolder = new ChainIdHashHolder();
          chainIdHashHolder.put(anyChainIdHash);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient)
              .withValue(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, chainIdHashHolder);
          ContextHolder.attach(context);

          // then
          final ContractMethods contractMethods = new ContractMethods();
          final List<Object> parameters = Arrays.<Object>asList(anySigner, anyInvocation, anyNonce,
              anyFee);
          final TxHash txHash = contractMethods.getExecuteTx()
              .invoke(parameters);
          assertNotNull(txHash);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testQuery() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.queryContract(any(Blockchain.Query.class)))
              .thenReturn(Rpc.SingleBytes.newBuilder().build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final ContractMethods contractMethods = new ContractMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyInvocation);
          final ContractResult contractResult = contractMethods.getQuery()
              .invoke(parameters);
          assertNotNull(contractResult);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testListEvent() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.listEvents(any(Blockchain.FilterInfo.class)))
              .thenReturn(Rpc.EventList.newBuilder().build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final ContractMethods contractMethods = new ContractMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyEventFilter);
          final List<Event> events = contractMethods.getListEvent()
              .invoke(parameters);
          assertNotNull(events);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testSubscribeEvent() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceStub mockStreamStub = mock(AergoRPCServiceStub.class);
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getStreamStub()).thenReturn(mockStreamStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final ContractMethods contractMethods = new ContractMethods();
          final StreamObserver<Event> streamObserver = new StreamObserver<Event>() {
            @Override
            public void onNext(Event value) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
          };
          final List<Object> parameters = Arrays.<Object>asList(anyEventFilter, streamObserver);
          final Subscription<Event> subscription = contractMethods.getSubscribeEvent()
              .invoke(parameters);
          assertNotNull(subscription);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

}
