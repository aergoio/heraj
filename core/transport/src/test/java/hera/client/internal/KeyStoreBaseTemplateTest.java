/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.ThreadLocalContextProvider;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.TxHash;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AccountOuterClass;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@PrepareForTest({AergoRPCServiceFutureStub.class})
public class KeyStoreBaseTemplateTest extends AbstractTestCase {

  protected static final String PASSWORD = randomUUID().toString();

  protected KeyStoreBaseTemplate supplyKeyStoreTemplateBase(
      final AergoRPCServiceFutureStub aergoService) {
    final KeyStoreBaseTemplate accountTemplateBase = new KeyStoreBaseTemplate();
    accountTemplateBase.aergoService = aergoService;
    accountTemplateBase.contextProvider = new ThreadLocalContextProvider(context, this);
    return accountTemplateBase;
  }

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testListAsync() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<AccountOuterClass.AccountList> mockListenableFuture =
        service.submit(new Callable<AccountOuterClass.AccountList>() {
          @Override
          public AccountOuterClass.AccountList call() throws Exception {
            return AccountOuterClass.AccountList.newBuilder().build();
          }
        });
    when(aergoService.getAccounts(any(Rpc.Empty.class))).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate keyStoreBaseTemplate = supplyKeyStoreTemplateBase(aergoService);

    final Future<List<AccountAddress>> accountListFuture =
        keyStoreBaseTemplate.getListFunction().apply();
    assertNotNull(accountListFuture.get());
  }

  @Test
  public void testCreateAsync() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<AccountOuterClass.Account> mockListenableFuture =
        service.submit(new Callable<AccountOuterClass.Account>() {
          @Override
          public AccountOuterClass.Account call() throws Exception {
            return AccountOuterClass.Account.newBuilder().build();
          }
        });
    when(aergoService.createAccount(any(Rpc.Personal.class))).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate keyStoreBaseTemplate = supplyKeyStoreTemplateBase(aergoService);

    final Future<AccountAddress> accountFuture =
        keyStoreBaseTemplate.getCreateFunction().apply(randomUUID().toString());
    assertNotNull(accountFuture.get());
  }

  @Test
  public void testLockAsync() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<AccountOuterClass.Account> mockListenableFuture =
        service.submit(new Callable<AccountOuterClass.Account>() {
          @Override
          public AccountOuterClass.Account call() throws Exception {
            return AccountOuterClass.Account.newBuilder().build();
          }
        });
    when(aergoService.lockAccount(any(Rpc.Personal.class))).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate keyStoreBaseTemplate = supplyKeyStoreTemplateBase(aergoService);

    final Future<Boolean> lockResult =
        keyStoreBaseTemplate.getLockFunction().apply(Authentication.of(accountAddress, PASSWORD));
    assertNotNull(lockResult.get());
  }

  @Test
  public void testUnlock() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<AccountOuterClass.Account> mockListenableFuture =
        service.submit(new Callable<AccountOuterClass.Account>() {
          @Override
          public AccountOuterClass.Account call() throws Exception {
            return AccountOuterClass.Account.newBuilder().build();
          }
        });
    when(aergoService.unlockAccount(any(Rpc.Personal.class))).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate keyStoreBaseTemplate = supplyKeyStoreTemplateBase(aergoService);

    final Future<Boolean> accountFuture =
        keyStoreBaseTemplate.getUnlockFunction().apply(Authentication.of(accountAddress, PASSWORD));
    assertNotNull(accountFuture.get());
  }

  @Test
  public void testImportKey() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<AccountOuterClass.Account> mockListenableFuture =
        service.submit(new Callable<AccountOuterClass.Account>() {
          @Override
          public AccountOuterClass.Account call() throws Exception {
            return AccountOuterClass.Account.newBuilder().build();
          }
        });
    when(aergoService.importAccount(any(Rpc.ImportFormat.class))).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate keyStoreBaseTemplate = supplyKeyStoreTemplateBase(aergoService);

    final Future<AccountAddress> accountFuture =
        keyStoreBaseTemplate.getImportKeyFunction().apply(encryptedPrivateKey, PASSWORD, PASSWORD);
    assertNotNull(accountFuture.get());
  }

  @Test
  public void testExportKey() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.SingleBytes> mockListenableFuture =
        service.submit(new Callable<Rpc.SingleBytes>() {
          @Override
          public Rpc.SingleBytes call() throws Exception {
            return Rpc.SingleBytes.newBuilder().build();
          }
        });
    when(aergoService.exportAccount(any(Rpc.Personal.class))).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate keyStoreBaseTemplate = supplyKeyStoreTemplateBase(aergoService);

    final Future<EncryptedPrivateKey> accountFuture =
        keyStoreBaseTemplate.getExportKeyFunction()
            .apply(Authentication.of(accountAddress, PASSWORD));
    assertNotNull(accountFuture.get());
  }

  @Test
  public void testSend() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.CommitResult> mockListenableFuture =
        service.submit(new Callable<Rpc.CommitResult>() {
          @Override
          public Rpc.CommitResult call() throws Exception {
            return Rpc.CommitResult.newBuilder().build();
          }
        });
    Mockito.when(aergoService.sendTX(any(Blockchain.Tx.class))).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate keyStoreBaseTemplate = supplyKeyStoreTemplateBase(aergoService);

    final Future<TxHash> txHash = keyStoreBaseTemplate.getSendFunction()
        .apply(accountAddress, accountAddress, Aer.of("10", Unit.AER), BytesValue.EMPTY);
    assertNotNull(txHash.get());
  }

}
