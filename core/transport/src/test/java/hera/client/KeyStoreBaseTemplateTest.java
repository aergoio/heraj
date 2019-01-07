/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.Context;
import hera.ContextProvider;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AccountOuterClass;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Rpc;

@PrepareForTest({AergoRPCServiceFutureStub.class})
public class KeyStoreBaseTemplateTest extends AbstractTestCase {

  protected static final EncryptedPrivateKey ENCRYPTED_PRIVATE_KEY =
      new EncryptedPrivateKey(of(new byte[] {EncryptedPrivateKey.VERSION}));

  protected static final AccountAddress ACCOUNT_ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected static final String PASSWORD = randomUUID().toString();

  protected KeyStoreBaseTemplate supplyAccountTemplateBase(
      final AergoRPCServiceFutureStub aergoService) {
    final KeyStoreBaseTemplate accountTemplateBase = new KeyStoreBaseTemplate();
    accountTemplateBase.aergoService = aergoService;
    accountTemplateBase.contextProvider = new ContextProvider() {
      @Override
      public Context get() {
        return context;
      }
    };
    return accountTemplateBase;
  }

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testListAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<AccountOuterClass.AccountList> mockListenableFuture =
        service.submit(new Callable<AccountOuterClass.AccountList>() {
          @Override
          public AccountOuterClass.AccountList call() throws Exception {
            return AccountOuterClass.AccountList.newBuilder().build();
          }
        });
    when(aergoService.getAccounts(any())).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final FinishableFuture<List<AccountAddress>> accountListFuture =
        accountTemplateBase.getListFunction().apply();
    assertNotNull(accountListFuture.get());
  }

  @Test
  public void testCreateAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<AccountOuterClass.Account> mockListenableFuture =
        service.submit(new Callable<AccountOuterClass.Account>() {
          @Override
          public AccountOuterClass.Account call() throws Exception {
            return AccountOuterClass.Account.newBuilder().build();
          }
        });
    when(aergoService.createAccount(any())).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final FinishableFuture<Account> accountFuture =
        accountTemplateBase.getCreateFunction().apply(randomUUID().toString());
    assertNotNull(accountFuture.get());
  }

  @Test
  public void testLockAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<AccountOuterClass.Account> mockListenableFuture =
        service.submit(new Callable<AccountOuterClass.Account>() {
          @Override
          public AccountOuterClass.Account call() throws Exception {
            return AccountOuterClass.Account.newBuilder().build();
          }
        });
    when(aergoService.lockAccount(any())).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final FinishableFuture<Boolean> lockResult =
        accountTemplateBase.getLockFunction().apply(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertNotNull(lockResult.get());
  }

  @Test
  public void testUnlock() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<AccountOuterClass.Account> mockListenableFuture =
        service.submit(new Callable<AccountOuterClass.Account>() {
          @Override
          public AccountOuterClass.Account call() throws Exception {
            return AccountOuterClass.Account.newBuilder().build();
          }
        });
    when(aergoService.unlockAccount(any())).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final FinishableFuture<Boolean> accountFuture =
        accountTemplateBase.getUnlockFunction().apply(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertNotNull(accountFuture.get());
  }

  @Test
  public void testImportKey() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<AccountOuterClass.Account> mockListenableFuture =
        service.submit(new Callable<AccountOuterClass.Account>() {
          @Override
          public AccountOuterClass.Account call() throws Exception {
            return AccountOuterClass.Account.newBuilder().build();
          }
        });
    when(aergoService.importAccount(any())).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final FinishableFuture<Account> accountFuture =
        accountTemplateBase.getImportKeyFunction().apply(ENCRYPTED_PRIVATE_KEY, PASSWORD, PASSWORD);
    assertNotNull(accountFuture.get());
  }

  @Test
  public void testExportKey() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.SingleBytes> mockListenableFuture =
        service.submit(new Callable<Rpc.SingleBytes>() {
          @Override
          public Rpc.SingleBytes call() throws Exception {
            return Rpc.SingleBytes.newBuilder().build();
          }
        });
    when(aergoService.exportAccount(any())).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final FinishableFuture<EncryptedPrivateKey> accountFuture =
        accountTemplateBase.getExportKeyFunction()
            .apply(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertNotNull(accountFuture.get());
  }

}
