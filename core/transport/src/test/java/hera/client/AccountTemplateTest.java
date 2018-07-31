/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.protobuf.ByteString.copyFrom;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.transport.ModelConverter;
import java.util.List;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AccountOuterClass;
import types.AccountOuterClass.AccountList;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Blockchain.State;

@PrepareForTest({AergoRPCServiceBlockingStub.class, AccountList.class,
    AccountOuterClass.Account.class, State.class})
public class AccountTemplateTest extends AbstractTestCase {

  protected final byte[] ADDRESS = randomUUID().toString().getBytes();

  protected static final ModelConverter<Account, AccountOuterClass.Account> accountConverter = mock(
      ModelConverter.class);

  protected static final ModelConverter<AccountState, State> accountStateConverter = mock(
      ModelConverter.class);

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
  public void testList() {
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    when(aergoService.getAccounts(any())).thenReturn(mock(AccountList.class));

    final AccountTemplate accountTemplate = new AccountTemplate(aergoService, accountConverter,
        accountStateConverter);

    final List<Account> accountList = accountTemplate.list();
    assertNotNull(accountList);
  }

  @Test
  public void testCreate() {
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    when(aergoService.createAccount(any())).thenReturn(mock(AccountOuterClass.Account.class));

    final AccountTemplate accountTemplate = new AccountTemplate(aergoService, accountConverter,
        accountStateConverter);

    final Account account = accountTemplate.create(randomUUID().toString());
    assertNotNull(account);
  }

  @Test
  public void testGet() {
    final AccountOuterClass.Account account = AccountOuterClass.Account.newBuilder()
        .setAddress(copyFrom(ADDRESS))
        .build();
    final AccountList blockchainAccountList = AccountList.newBuilder()
        .addAccounts(account)
        .build();
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    when(aergoService.getAccounts(any())).thenReturn(blockchainAccountList);

    final AccountTemplate accountTemplate = new AccountTemplate(aergoService);

    final Optional<Account> acocunt = accountTemplate
        .get(AccountAddress.of(ADDRESS));
    assertTrue(acocunt.isPresent());
  }

  @Test
  public void testLock() {
    final AccountOuterClass.Account blockchainAccount = AccountOuterClass.Account.newBuilder()
        .setAddress(copyFrom(ADDRESS))
        .build();
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    when(aergoService.lockAccount(any())).thenReturn(blockchainAccount);

    final AccountTemplate accountTemplate = new AccountTemplate(aergoService, accountConverter,
        accountStateConverter);

    final Account account = Account.of(ADDRESS, randomUUID().toString());
    final boolean lockResult = accountTemplate.lock(account);
    assertTrue(lockResult);
  }

  @Test
  public void testUnlock() {
    final AccountOuterClass.Account blockchainAccount = AccountOuterClass.Account.newBuilder()
        .setAddress(copyFrom(ADDRESS))
        .build();
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    when(aergoService.unlockAccount(any())).thenReturn(blockchainAccount);

    final AccountTemplate accountTemplate = new AccountTemplate(aergoService, accountConverter,
        accountStateConverter);

    final Account account = Account.of(ADDRESS, randomUUID().toString());
    final boolean unlockResult = accountTemplate.unlock(account);
    assertTrue(unlockResult);
  }

  @Test
  public void testGetState() {
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    when(aergoService.getState(any())).thenReturn(mock(State.class));

    final AccountTemplate accountTemplate = new AccountTemplate(aergoService, accountConverter,
        accountStateConverter);

    final Optional<AccountState> state = accountTemplate.getState(AccountAddress.of(ADDRESS));
    assertTrue(state.isPresent());
  }

}
