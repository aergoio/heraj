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
import com.google.protobuf.ByteString;
import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.transport.ModelConverter;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AccountOuterClass;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class, AccountOuterClass.Account.class,
    Blockchain.State.class, Rpc.Personal.class, Rpc.SingleBytes.class})
public class AccountAsyncTemplateTest extends AbstractTestCase {

  protected static final EncryptedPrivateKey ENCRYPTED_PRIVATE_KEY =
      new EncryptedPrivateKey(of(new byte[] {EncryptedPrivateKey.PRIVATE_KEY_VERSION}));

  protected static final AccountAddress ACCOUNT_ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.ADDRESS_VERSION}));

  protected static final String PASSWORD = randomUUID().toString();

  protected static final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      mock(ModelConverter.class);

  protected static final ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes> encryptedPrivateKeyConverter =
      mock(ModelConverter.class);

  protected static final ModelConverter<Account, AccountOuterClass.Account> accountConverter =
      mock(ModelConverter.class);

  protected static final ModelConverter<Account, Blockchain.State> accountStateConverter =
      mock(ModelConverter.class);

  protected static final ModelConverter<Authentication, Rpc.Personal> authenticationConverter =
      mock(ModelConverter.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    when(accountAddressConverter.convertToDomainModel(any(ByteString.class)))
        .thenReturn(mock(AccountAddress.class));
    when(accountAddressConverter.convertToRpcModel(any(AccountAddress.class)))
        .thenReturn(mock(ByteString.class));
    when(encryptedPrivateKeyConverter.convertToDomainModel(any(Rpc.SingleBytes.class)))
        .thenReturn(mock(EncryptedPrivateKey.class));
    when(encryptedPrivateKeyConverter.convertToRpcModel(any(EncryptedPrivateKey.class)))
        .thenReturn(mock(Rpc.SingleBytes.class));
    when(accountConverter.convertToDomainModel(any(AccountOuterClass.Account.class)))
        .thenReturn(mock(Account.class));
    when(accountConverter.convertToRpcModel(any(Account.class)))
        .thenReturn(mock(AccountOuterClass.Account.class));
    when(accountStateConverter.convertToDomainModel(any(Blockchain.State.class)))
        .thenReturn(mock(Account.class));
    when(authenticationConverter.convertToRpcModel(any(Authentication.class)))
        .thenReturn(mock(Rpc.Personal.class));
  }

  @Test
  public void testListAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getAccounts(any())).thenReturn(mockListenableFuture);

    final AccountAsyncTemplate accountAsyncTemplate = new AccountAsyncTemplate(aergoService,
        accountAddressConverter, encryptedPrivateKeyConverter, accountConverter,
        accountStateConverter, authenticationConverter);

    final ResultOrErrorFuture<List<Account>> accountListFuture = accountAsyncTemplate.list();
    assertNotNull(accountListFuture);
  }

  @Test
  public void testCreateAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.createAccount(any())).thenReturn(mockListenableFuture);

    final AccountAsyncTemplate accountAsyncTemplate = new AccountAsyncTemplate(aergoService,
        accountAddressConverter, encryptedPrivateKeyConverter, accountConverter,
        accountStateConverter, authenticationConverter);

    final ResultOrErrorFuture<Account> accountFuture =
        accountAsyncTemplate.create(randomUUID().toString());
    assertNotNull(accountFuture);
  }

  @Test
  public void testGetAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getState(any())).thenReturn(mockListenableFuture);

    final AccountAsyncTemplate accountAsyncTemplate = new AccountAsyncTemplate(aergoService,
        accountAddressConverter, encryptedPrivateKeyConverter, accountConverter,
        accountStateConverter, authenticationConverter);

    final ResultOrErrorFuture<Account> accountFuture = accountAsyncTemplate.get(ACCOUNT_ADDRESS);
    assertNotNull(accountFuture);
  }

  @Test
  public void testLockAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.lockAccount(any())).thenReturn(mockListenableFuture);

    final AccountAsyncTemplate accountAsyncTemplate = new AccountAsyncTemplate(aergoService,
        accountAddressConverter, encryptedPrivateKeyConverter, accountConverter,
        accountStateConverter, authenticationConverter);

    final ResultOrErrorFuture<Boolean> lockResult =
        accountAsyncTemplate.lock(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertNotNull(lockResult);
  }

  @Test
  public void testUnlock() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.unlockAccount(any())).thenReturn(mockListenableFuture);

    final AccountAsyncTemplate accountAsyncTemplate = new AccountAsyncTemplate(aergoService,
        accountAddressConverter, encryptedPrivateKeyConverter, accountConverter,
        accountStateConverter, authenticationConverter);

    final ResultOrErrorFuture<Boolean> accountFuture =
        accountAsyncTemplate.unlock(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertNotNull(accountFuture);
  }

  @Test
  public void testImportKey() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.importAccount(any())).thenReturn(mockListenableFuture);

    final AccountAsyncTemplate accountAsyncTemplate = new AccountAsyncTemplate(aergoService,
        accountAddressConverter, encryptedPrivateKeyConverter, accountConverter,
        accountStateConverter, authenticationConverter);

    final ResultOrErrorFuture<Account> accountFuture =
        accountAsyncTemplate.importKey(ENCRYPTED_PRIVATE_KEY, PASSWORD);
    assertNotNull(accountFuture);
  }

  @Test
  public void testExportKey() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.exportAccount(any())).thenReturn(mockListenableFuture);

    final AccountAsyncTemplate accountAsyncTemplate = new AccountAsyncTemplate(aergoService,
        accountAddressConverter, encryptedPrivateKeyConverter, accountConverter,
        accountStateConverter, authenticationConverter);

    final ResultOrErrorFuture<EncryptedPrivateKey> accountFuture =
        accountAsyncTemplate.exportKey(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertNotNull(accountFuture);
  }

}
