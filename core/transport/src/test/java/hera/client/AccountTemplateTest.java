/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.AccountAsyncOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@PrepareForTest({AergoRPCServiceFutureStub.class})
public class AccountTemplateTest extends AbstractTestCase {

  @Test
  public void testList() throws Exception {
    CompletableFuture<List<Account>> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(List.class));
    AccountAsyncOperation asyncOperationMock = mock(AccountAsyncOperation.class);
    when(asyncOperationMock.list()).thenReturn(futureMock);

    final AccountTemplate accountTemplate = new AccountTemplate(asyncOperationMock);

    final List<Account> accountListFuture = accountTemplate.list();
    assertNotNull(accountListFuture);
  }

  @Test
  public void testCreate() throws Exception {
    CompletableFuture<Account> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(Account.class));
    AccountAsyncOperation asyncOperationMock = mock(AccountAsyncOperation.class);
    when(asyncOperationMock.create(anyString())).thenReturn(futureMock);

    final AccountTemplate accountTemplate = new AccountTemplate(asyncOperationMock);

    final Account createdAccount = accountTemplate.create(randomUUID().toString());
    assertNotNull(createdAccount);
  }

  @Test
  public void testGet() throws Exception {
    CompletableFuture<Optional<Account>> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(Optional.empty());
    AccountAsyncOperation asyncOperationMock = mock(AccountAsyncOperation.class);
    when(asyncOperationMock.get(any())).thenReturn(futureMock);

    final AccountTemplate accountTemplate = new AccountTemplate(asyncOperationMock);

    final Optional<Account> account = accountTemplate
        .get(AccountAddress.of(randomUUID().toString().getBytes()));
    assertNotNull(account);
  }

  @Test
  public void testLock() throws Exception {
    CompletableFuture<Boolean> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(true);
    AccountAsyncOperation asyncOperationMock = mock(AccountAsyncOperation.class);
    when(asyncOperationMock.lock(any())).thenReturn(futureMock);

    final AccountTemplate accountTemplate = new AccountTemplate(asyncOperationMock);

    boolean lockResult = accountTemplate
        .lock(Account.of(randomUUID().toString().getBytes(), randomUUID().toString()));
    assertTrue(lockResult);
  }

  @Test
  public void testUnlock() throws Exception {
    CompletableFuture<Boolean> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(true);
    AccountAsyncOperation asyncOperationMock = mock(AccountAsyncOperation.class);
    when(asyncOperationMock.unlock(any())).thenReturn(futureMock);

    final AccountTemplate accountTemplate = new AccountTemplate(asyncOperationMock);

    boolean lockResult = accountTemplate
        .unlock(Account.of(randomUUID().toString().getBytes(), randomUUID().toString()));
    assertTrue(lockResult);
  }

  @Test
  public void testGetState() throws Exception {
    CompletableFuture<Optional<AccountState>> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(Optional.empty());
    AccountAsyncOperation asyncOperationMock = mock(AccountAsyncOperation.class);
    when(asyncOperationMock.getState(any())).thenReturn(futureMock);

    final AccountTemplate accountTemplate = new AccountTemplate(asyncOperationMock);

    final Optional<AccountState> createdAccount = accountTemplate
        .getState(AccountAddress.of(randomUUID().toString().getBytes()));
    assertNotNull(createdAccount);
  }

}
