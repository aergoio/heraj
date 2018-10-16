/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.tupleorerror.ResultOrError;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceStub.class})
public class AccountTemplateTest extends AbstractTestCase {

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected final String password = randomUUID().toString();

  @Test
  public void testList() throws Exception {
    ResultOrError<List<Account>> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(List.class));
    AccountEitherTemplate eitherOperationMock = mock(AccountEitherTemplate.class);
    when(eitherOperationMock.list()).thenReturn(eitherMock);

    final AccountTemplate accountTemplate = new AccountTemplate();
    accountTemplate.accountEitherOperation = eitherOperationMock;

    final List<Account> accountList = accountTemplate.list();
    assertNotNull(accountList);
  }

  @Test
  public void testCreate() throws Exception {
    ResultOrError<Account> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(Account.class));
    AccountEitherTemplate eitherOperationMock = mock(AccountEitherTemplate.class);
    when(eitherOperationMock.create(anyString())).thenReturn(eitherMock);

    final AccountTemplate accountTemplate = new AccountTemplate();
    accountTemplate.accountEitherOperation = eitherOperationMock;

    final Account createdAccount = accountTemplate.create(randomUUID().toString());
    assertNotNull(createdAccount);
  }

  @Test
  public void testGet() throws Exception {
    ResultOrError<Account> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(Account.class));
    AccountEitherTemplate eitherOperationMock = mock(AccountEitherTemplate.class);
    when(eitherOperationMock.get(any(AccountAddress.class))).thenReturn(eitherMock);

    final AccountTemplate accountTemplate = new AccountTemplate();
    accountTemplate.accountEitherOperation = eitherOperationMock;

    final Account account = accountTemplate.get(accountAddress);
    assertNotNull(account);
  }

  @Test
  public void testLock() throws Exception {
    ResultOrError<Boolean> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(true);
    AccountEitherTemplate eitherOperationMock = mock(AccountEitherTemplate.class);
    when(eitherOperationMock.lock(any())).thenReturn(eitherMock);

    final AccountTemplate accountTemplate = new AccountTemplate();
    accountTemplate.accountEitherOperation = eitherOperationMock;

    final boolean lockResult = accountTemplate.lock(Authentication.of(accountAddress, password));
    assertTrue(lockResult);
  }

  @Test
  public void testUnlock() throws Exception {
    ResultOrError<Boolean> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(true);
    AccountEitherTemplate eitherOperationMock = mock(AccountEitherTemplate.class);
    when(eitherOperationMock.unlock(any())).thenReturn(eitherMock);

    final AccountTemplate accountTemplate = new AccountTemplate();
    accountTemplate.accountEitherOperation = eitherOperationMock;

    final boolean unlockResult =
        accountTemplate.unlock(Authentication.of(accountAddress, password));
    assertTrue(unlockResult);
  }

  @Test
  public void testImportKey() throws Exception {
    ResultOrError<Account> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(Account.class));
    AccountEitherTemplate eitherOperationMock = mock(AccountEitherTemplate.class);
    when(eitherOperationMock.importKey(any(), any(), any())).thenReturn(eitherMock);

    final AccountTemplate accountTemplate = new AccountTemplate();
    accountTemplate.accountEitherOperation = eitherOperationMock;

    final Account importedAccount = accountTemplate
        .importKey(new EncryptedPrivateKey(of(new byte[] {EncryptedPrivateKey.VERSION})), password);
    assertNotNull(importedAccount);
  }

  @Test
  public void testExportKey() throws Exception {
    ResultOrError<EncryptedPrivateKey> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(EncryptedPrivateKey.class));
    AccountEitherTemplate eitherOperationMock = mock(AccountEitherTemplate.class);
    when(eitherOperationMock.exportKey(any())).thenReturn(eitherMock);

    final AccountTemplate accountTemplate = new AccountTemplate();
    accountTemplate.accountEitherOperation = eitherOperationMock;

    final EncryptedPrivateKey exportedKey =
        accountTemplate.exportKey(Authentication.of(accountAddress, password));
    assertNotNull(exportedKey);
  }

}
