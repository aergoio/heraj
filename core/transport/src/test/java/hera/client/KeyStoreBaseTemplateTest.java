/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AccountOuterClass;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Rpc;

@SuppressWarnings({"rawtypes", "unchecked"})
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
    return accountTemplateBase;
  }

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testListAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> AccountOuterClass.AccountList.newBuilder().build());
    when(aergoService.getAccounts(any())).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final ResultOrErrorFuture<List<AccountAddress>> accountListFuture =
        accountTemplateBase.getListFunction().apply();
    assertTrue(accountListFuture.get().hasResult());
  }

  @Test
  public void testCreateAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> AccountOuterClass.Account.newBuilder().build());
    when(aergoService.createAccount(any())).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final ResultOrErrorFuture<Account> accountFuture =
        accountTemplateBase.getCreateFunction().apply(randomUUID().toString());
    assertTrue(accountFuture.get().hasResult());
  }

  @Test
  public void testLockAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> AccountOuterClass.Account.newBuilder().build());
    when(aergoService.lockAccount(any())).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final ResultOrErrorFuture<Boolean> lockResult =
        accountTemplateBase.getLockFunction().apply(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertTrue(lockResult.get().hasResult());
  }

  @Test
  public void testUnlock() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> AccountOuterClass.Account.newBuilder().build());
    when(aergoService.unlockAccount(any())).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final ResultOrErrorFuture<Boolean> accountFuture =
        accountTemplateBase.getUnlockFunction().apply(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertTrue(accountFuture.get().hasResult());
  }

  @Test
  public void testImportKey() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> AccountOuterClass.Account.newBuilder().build());
    when(aergoService.importAccount(any())).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final ResultOrErrorFuture<Account> accountFuture =
        accountTemplateBase.getImportKeyFunction().apply(ENCRYPTED_PRIVATE_KEY, PASSWORD, PASSWORD);
    assertTrue(accountFuture.get().hasResult());
  }

  @Test
  public void testExportKey() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.SingleBytes.newBuilder().build());
    when(aergoService.exportAccount(any())).thenReturn(mockListenableFuture);

    final KeyStoreBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final ResultOrErrorFuture<EncryptedPrivateKey> accountFuture =
        accountTemplateBase.getExportKeyFunction()
            .apply(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertTrue(accountFuture.get().hasResult());
  }

}
