/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_CLIENT;
import static hera.client.ClientContextKeys.GRPC_VALUE_CHAIN_ID_HASH_HOLDER;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.protobuf.ByteString;
import hera.AbstractTestCase;
import hera.Context;
import hera.ContextHolder;
import hera.EmptyContext;
import hera.api.model.AccountAddress;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.ModelConverter;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AccountOuterClass;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;
import types.Blockchain;
import types.Rpc;
import types.Rpc.CommitStatus;

@PrepareForTest({AergoRPCServiceBlockingStub.class, AergoRPCServiceStub.class})
public class KeyStoreMethodsTest extends AbstractTestCase {

  @Test
  public void testList() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final ModelConverter<AccountAddress, ByteString> converter =
              new AccountAddressConverterFactory().create();
          when(mockBlockingStub.getAccounts(any(Rpc.Empty.class)))
              .thenReturn(AccountOuterClass.AccountList.newBuilder()
                  .addAccounts(AccountOuterClass.Account.newBuilder()
                      .setAddress(converter.convertToRpcModel(anyAccountAddress))
                      .build())
                  .build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final KeyStoreMethods keyStoreMethods = new KeyStoreMethods();
          final List<Object> parameters = Arrays.<Object>asList();
          final List<AccountAddress> accountAddresses = keyStoreMethods.getList()
              .invoke(parameters);
          assertNotNull(accountAddresses);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testCreate() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final ModelConverter<AccountAddress, ByteString> converter =
              new AccountAddressConverterFactory().create();
          when(mockBlockingStub.createAccount(any(Rpc.Personal.class)))
              .thenReturn(AccountOuterClass.Account.newBuilder()
                  .setAddress(converter.convertToRpcModel(anyAccountAddress))
                  .build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final KeyStoreMethods keyStoreMethods = new KeyStoreMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyPassword);
          final AccountAddress accountAddress = keyStoreMethods.getCreate()
              .invoke(parameters);
          assertNotNull(accountAddress);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testUnlock() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final ModelConverter<AccountAddress, ByteString> converter =
              new AccountAddressConverterFactory().create();
          when(mockBlockingStub.unlockAccount(any(Rpc.Personal.class)))
              .thenReturn(AccountOuterClass.Account.newBuilder()
                  .setAddress(converter.convertToRpcModel(anyAccountAddress))
                  .build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final KeyStoreMethods keyStoreMethods = new KeyStoreMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyAuthentication);
          final Boolean result = keyStoreMethods.getUnlock()
              .invoke(parameters);
          assertTrue(result);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testLock() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final ModelConverter<AccountAddress, ByteString> converter =
              new AccountAddressConverterFactory().create();
          when(mockBlockingStub.lockAccount(any(Rpc.Personal.class)))
              .thenReturn(AccountOuterClass.Account.newBuilder()
                  .setAddress(converter.convertToRpcModel(anyAccountAddress))
                  .build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final KeyStoreMethods keyStoreMethods = new KeyStoreMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyAuthentication);
          final Boolean result = keyStoreMethods.getLock()
              .invoke(parameters);
          assertTrue(result);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testSign() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.signTX(any(Blockchain.Tx.class)))
              .thenReturn(Blockchain.Tx.newBuilder().build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final KeyStoreMethods keyStoreMethods = new KeyStoreMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyRawTransaction);
          final Transaction signed = keyStoreMethods.getSign()
              .invoke(parameters);
          assertNotNull(signed);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testImportKey() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final ModelConverter<AccountAddress, ByteString> converter =
              new AccountAddressConverterFactory().create();
          when(mockBlockingStub.importAccount(any(Rpc.ImportFormat.class)))
              .thenReturn(AccountOuterClass.Account.newBuilder()
                  .setAddress(converter.convertToRpcModel(anyAccountAddress))
                  .build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final KeyStoreMethods keyStoreMethods = new KeyStoreMethods();
          final List<Object> parameters = Arrays.<Object>asList(EncryptedPrivateKey.EMPTY,
              anyPassword, anyPassword);
          final AccountAddress accountAddress = keyStoreMethods.getImportKey()
              .invoke(parameters);
          assertNotNull(accountAddress);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testExportKey() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final ModelConverter<AccountAddress, ByteString> converter =
              new AccountAddressConverterFactory().create();
          when(mockBlockingStub.exportAccount(any(Rpc.Personal.class)))
              .thenReturn(Rpc.SingleBytes.newBuilder().build());
          final GrpcClient mockClient = mock(GrpcClient.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final KeyStoreMethods keyStoreMethods = new KeyStoreMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyAuthentication);
          final EncryptedPrivateKey encryptedPrivateKey = keyStoreMethods.getExportKey()
              .invoke(parameters);
          assertNotNull(encryptedPrivateKey);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testSend() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.sendTX(any(Blockchain.Tx.class)))
              .thenReturn(Rpc.CommitResult.newBuilder()
                  .setError(CommitStatus.TX_OK)
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
          final KeyStoreMethods keyStoreMethods = new KeyStoreMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyAccountAddress,
              anyAccountAddress, anyAmount, anyPayload);
          final TxHash txHash = keyStoreMethods.getSend()
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
