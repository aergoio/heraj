/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.transport.ModelConverter;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AccountOuterClass;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain.State;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class, AccountOuterClass.Account.class, State.class})
public class AccountAsyncTemplateTest extends AbstractTestCase {

  protected final AccountAddress ACCOUNT_ADDRESS =
      AccountAddress.of(randomUUID().toString().getBytes());

  protected final String PASSWORD = randomUUID().toString();

  protected static final ModelConverter<Account, AccountOuterClass.Account> accountConverter =
      mock(ModelConverter.class);

  protected static final ModelConverter<AccountState, State> accountStateConverter =
      mock(ModelConverter.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    when(accountConverter.convertToDomainModel(any(AccountOuterClass.Account.class)))
        .thenReturn(mock(Account.class));
    when(accountConverter.convertToRpcModel(any(Account.class)))
        .thenReturn(mock(AccountOuterClass.Account.class));
    when(accountStateConverter.convertToDomainModel(any(State.class)))
        .thenReturn(mock(AccountState.class));
    when(accountStateConverter.convertToRpcModel(any(AccountState.class)))
        .thenReturn(mock(State.class));
  }

  @Test
  public void testListAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getAccounts(any())).thenReturn(mockListenableFuture);

    final AccountAsyncTemplate accountAsyncTemplate =
        new AccountAsyncTemplate(aergoService, accountConverter, accountStateConverter);

    final ResultOrErrorFuture<List<Account>> accountListFuture = accountAsyncTemplate.list();
    assertNotNull(accountListFuture);
  }

  @Test
  public void testCreateAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.createAccount(any())).thenReturn(mockListenableFuture);

    final AccountAsyncTemplate accountAsyncTemplate =
        new AccountAsyncTemplate(aergoService, accountConverter, accountStateConverter);

    final ResultOrErrorFuture<Account> accountFuture =
        accountAsyncTemplate.create(randomUUID().toString());
    assertNotNull(accountFuture);
  }

  @Test
  public void testGetAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getAccounts(any())).thenReturn(mockListenableFuture);

    final AccountAsyncTemplate accountAsyncTemplate =
        new AccountAsyncTemplate(aergoService, accountConverter, accountStateConverter);

    final ResultOrErrorFuture<Account> accountFuture = accountAsyncTemplate.get(ACCOUNT_ADDRESS);
    assertNotNull(accountFuture);
  }

  @Test
  public void testLockAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.lockAccount(any())).thenReturn(mockListenableFuture);

    final AccountAsyncTemplate accountAsyncTemplate =
        new AccountAsyncTemplate(aergoService, accountConverter, accountStateConverter);

    final ResultOrErrorFuture<Boolean> lockResult =
        accountAsyncTemplate.lock(ACCOUNT_ADDRESS, PASSWORD);
    assertNotNull(lockResult);
  }

  @Test
  public void testUnlock() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.unlockAccount(any())).thenReturn(mockListenableFuture);

    final AccountAsyncTemplate accountAsyncTemplate =
        new AccountAsyncTemplate(aergoService, accountConverter, accountStateConverter);

    final ResultOrErrorFuture<Boolean> accountFuture =
        accountAsyncTemplate.unlock(ACCOUNT_ADDRESS, PASSWORD);
    assertNotNull(accountFuture);
  }

  @Test
  public void testGetState() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getState(any())).thenReturn(mockListenableFuture);

    final AccountAsyncTemplate accountAsyncTemplate =
        new AccountAsyncTemplate(aergoService, accountConverter, accountStateConverter);

    final ResultOrErrorFuture<AccountState> accountFuture =
        accountAsyncTemplate.getState(ACCOUNT_ADDRESS);
    assertNotNull(accountFuture);
  }

}
