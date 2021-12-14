/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_CLIENT;
import static hera.client.ClientContextKeys.GRPC_VALUE_CHAIN_ID_HASH_HOLDER;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.*;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.TxReceipt;
import hera.exception.CommitException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;
import types.Blockchain;
import types.Rpc;
import types.Rpc.CommitStatus;

@PrepareForTest({AergoRPCServiceBlockingStub.class, AergoRPCServiceStub.class})
public class TransactionMethodsTest extends AbstractTestCase {
  @BeforeClass
  public static void beforeClass() throws Exception {
    // powermock cannot mock java.security packages in jdk17 due to stricter security policies
    Assume.assumeTrue(TestUtils.getVersion() < 17 );
  }

  @Test
  public void testTransactionInMemPool() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getTX(any(Rpc.SingleBytes.class)))
              .thenReturn(Blockchain.Tx.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final TransactionMethods transactionMethods = new TransactionMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyTxHash);
          final Transaction transaction = transactionMethods.getTransactionInMemPool()
              .invoke(parameters);
          assertNotNull(transaction);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void shouldTransactionInMemPoolReturnNullOnNotFoundError() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final StatusRuntimeException error = new StatusRuntimeException(
              Status.NOT_FOUND.withDescription("not found"));
          when(mockBlockingStub.getTX(any(Rpc.SingleBytes.class))).thenThrow(error);
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final TransactionMethods transactionMethods = new TransactionMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyTxHash);
          final Transaction transaction = transactionMethods.getTransactionInMemPool()
              .invoke(parameters);
          assertNull(transaction);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testTransactionInBlock() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getBlockTX(any(Rpc.SingleBytes.class)))
              .thenReturn(Blockchain.TxInBlock.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final TransactionMethods transactionMethods = new TransactionMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyTxHash);
          final Transaction transaction = transactionMethods.getTransactionInBlock()
              .invoke(parameters);
          assertNotNull(transaction);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void shouldTransactionInBlockReturnNullOnNotFoundError() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final StatusRuntimeException error = new StatusRuntimeException(
              Status.NOT_FOUND.withDescription("not found"));
          when(mockBlockingStub.getBlockTX(any(Rpc.SingleBytes.class))).thenThrow(error);
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final TransactionMethods transactionMethods = new TransactionMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyTxHash);
          final Transaction transaction = transactionMethods.getTransactionInBlock()
              .invoke(parameters);
          assertNull(transaction);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testTxReceipt() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getReceipt(any(Rpc.SingleBytes.class)))
              .thenReturn(Blockchain.Receipt.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final TransactionMethods transactionMethods = new TransactionMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyTxHash);
          final TxReceipt txReceipt = transactionMethods.getTxReceipt()
              .invoke(parameters);
          assertNotNull(txReceipt);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void shouldTxReceiptReturnNullOnNotFoundError() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final StatusRuntimeException error = new StatusRuntimeException(
              Status.NOT_FOUND.withDescription("not found"));
          when(mockBlockingStub.getReceipt(any(Rpc.SingleBytes.class))).thenThrow(error);
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final TransactionMethods transactionMethods = new TransactionMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyTxHash);
          final TxReceipt txReceipt = transactionMethods.getTxReceipt()
              .invoke(parameters);
          assertNull(txReceipt);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void shouldThrowCommitExceptionOnNoOk() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.commitTX(any(Blockchain.TxList.class)))
              .thenReturn(Rpc.CommitResultList.newBuilder()
                  .addResults(Rpc.CommitResult.newBuilder()
                      .setError(CommitStatus.TX_NONCE_TOO_LOW)
                      .build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final TransactionMethods transactionMethods = new TransactionMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyTransaction);
          transactionMethods.getCommit().invoke(parameters);
        } catch (CommitException e) {
          // then
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testCommit() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.commitTX(any(Blockchain.TxList.class)))
              .thenReturn(Rpc.CommitResultList.newBuilder()
                  .addResults(Rpc.CommitResult.newBuilder()
                      .setError(CommitStatus.TX_OK)
                      .build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final TransactionMethods transactionMethods = new TransactionMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyTransaction);
          final TxHash txHash = transactionMethods.getCommit()
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
  public void testSendTxByAddress() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.commitTX(any(Blockchain.TxList.class)))
              .thenReturn(Rpc.CommitResultList.newBuilder()
                  .addResults(Rpc.CommitResult.newBuilder()
                      .setError(CommitStatus.TX_OK)
                      .build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final ChainIdHashHolder chainIdHashHolder = new ChainIdHashHolder();
          chainIdHashHolder.put(anyChainIdHash);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient)
              .withValue(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, chainIdHashHolder);
          ContextHolder.attach(context);

          // then
          final TransactionMethods transactionMethods = new TransactionMethods();
          final List<Object> parameters = Arrays.<Object>asList(anySigner, anyAccountAddress,
              anyAmount, anyNonce, anyFee, anyPayload);
          final TxHash txHash = transactionMethods.getSendTxByAddress()
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
  public void testSendTxByName() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.commitTX(any(Blockchain.TxList.class)))
              .thenReturn(Rpc.CommitResultList.newBuilder()
                  .addResults(Rpc.CommitResult.newBuilder()
                      .setError(CommitStatus.TX_OK)
                      .build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final ChainIdHashHolder chainIdHashHolder = new ChainIdHashHolder();
          chainIdHashHolder.put(anyChainIdHash);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient)
              .withValue(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, chainIdHashHolder);
          ContextHolder.attach(context);

          // then
          final TransactionMethods transactionMethods = new TransactionMethods();
          final List<Object> parameters = Arrays.<Object>asList(anySigner, anyName,
              anyAmount, anyNonce, anyFee, anyPayload);
          final TxHash txHash = transactionMethods.getSendTxByName()
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

}
