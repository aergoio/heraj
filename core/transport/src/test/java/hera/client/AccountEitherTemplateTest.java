/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceFutureStub.class})
public class AccountEitherTemplateTest extends AbstractTestCase {

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected final String password = randomUUID().toString();

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testList() {
    ResultOrErrorFuture<List<AccountAddress>> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get()).thenReturn(mock(ResultOrError.class));
    AccountAsyncTemplate asyncOperationMock = mock(AccountAsyncTemplate.class);
    when(asyncOperationMock.list()).thenReturn(futureMock);

    final AccountEitherTemplate accountTemplate = new AccountEitherTemplate();
    accountTemplate.accountAsyncOperation = asyncOperationMock;

    final ResultOrError<List<AccountAddress>> accountListFuture = accountTemplate.list();
    assertNotNull(accountListFuture);
  }

  @Test
  public void testCreate() {
    ResultOrErrorFuture<Account> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get()).thenReturn(mock(ResultOrError.class));
    AccountAsyncTemplate asyncOperationMock = mock(AccountAsyncTemplate.class);
    when(asyncOperationMock.create(anyString())).thenReturn(futureMock);

    final AccountEitherTemplate accountTemplate = new AccountEitherTemplate();
    accountTemplate.accountAsyncOperation = asyncOperationMock;

    final ResultOrError<Account> createdAccount = accountTemplate.create(randomUUID().toString());
    assertNotNull(createdAccount);
  }

  @Test
  public void testGetState() {
    ResultOrErrorFuture<AccountState> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get()).thenReturn(mock(ResultOrError.class));
    AccountAsyncTemplate asyncOperationMock = mock(AccountAsyncTemplate.class);
    when(asyncOperationMock.getState(any(AccountAddress.class))).thenReturn(futureMock);

    final AccountEitherTemplate accountTemplate = new AccountEitherTemplate();
    accountTemplate.accountAsyncOperation = asyncOperationMock;

    final ResultOrError<AccountState> accountState = accountTemplate.getState(accountAddress);
    assertNotNull(accountState);
  }

  @Test
  public void testLock() {
    ResultOrErrorFuture<Boolean> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get()).thenReturn(mock(ResultOrError.class));
    AccountAsyncTemplate asyncOperationMock = mock(AccountAsyncTemplate.class);
    when(asyncOperationMock.lock(any())).thenReturn(futureMock);

    final AccountEitherTemplate accountTemplate = new AccountEitherTemplate();
    accountTemplate.accountAsyncOperation = asyncOperationMock;

    ResultOrError<Boolean> lockResult =
        accountTemplate.lock(Authentication.of(accountAddress, password));
    assertNotNull(lockResult);
  }

  @Test
  public void testUnlock() {
    ResultOrErrorFuture<Boolean> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get()).thenReturn(mock(ResultOrError.class));
    AccountAsyncTemplate asyncOperationMock = mock(AccountAsyncTemplate.class);
    when(asyncOperationMock.unlock(any())).thenReturn(futureMock);

    final AccountEitherTemplate accountTemplate = new AccountEitherTemplate();
    accountTemplate.accountAsyncOperation = asyncOperationMock;

    ResultOrError<Boolean> unlockResult =
        accountTemplate.unlock(Authentication.of(accountAddress, password));
    assertNotNull(unlockResult);
  }

  @Test
  public void testImportKey() {
    ResultOrErrorFuture<Account> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get()).thenReturn(mock(ResultOrError.class));
    AccountAsyncTemplate asyncOperationMock = mock(AccountAsyncTemplate.class);
    when(asyncOperationMock.importKey(any(), any(), any())).thenReturn(futureMock);

    final AccountEitherTemplate accountTemplate = new AccountEitherTemplate();
    accountTemplate.accountAsyncOperation = asyncOperationMock;

    ResultOrError<Account> importedAccount = accountTemplate
        .importKey(new EncryptedPrivateKey(of(new byte[] {EncryptedPrivateKey.VERSION})), password);
    assertNotNull(importedAccount);
  }

  @Test
  public void testExportKey() {
    ResultOrErrorFuture<EncryptedPrivateKey> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get()).thenReturn(mock(ResultOrError.class));
    AccountAsyncTemplate asyncOperationMock = mock(AccountAsyncTemplate.class);
    when(asyncOperationMock.exportKey(any())).thenReturn(futureMock);

    final AccountEitherTemplate accountTemplate = new AccountEitherTemplate();
    accountTemplate.accountAsyncOperation = asyncOperationMock;

    ResultOrError<EncryptedPrivateKey> exportedKey =
        accountTemplate.exportKey(Authentication.of(accountAddress, password));
    assertNotNull(exportedKey);
  }

}
