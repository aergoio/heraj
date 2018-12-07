/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.BytesValue;
import hera.api.model.ClientManagedAccount;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class})
public class AccountBaseTemplateTest extends AbstractTestCase {

  protected static final EncryptedPrivateKey ENCRYPTED_PRIVATE_KEY =
      new EncryptedPrivateKey(of(new byte[] {EncryptedPrivateKey.VERSION}));

  protected static final AccountAddress ACCOUNT_ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected static final String PASSWORD = randomUUID().toString();

  protected AccountBaseTemplate supplyAccountTemplateBase(
      final AergoRPCServiceFutureStub aergoService) {
    final AccountBaseTemplate accountTemplateBase = new AccountBaseTemplate();
    accountTemplateBase.aergoService = aergoService;
    return accountTemplateBase;
  }

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testGetStateAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Blockchain.State.newBuilder().build());
    when(aergoService.getState(any())).thenReturn(mockListenableFuture);

    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final ResultOrErrorFuture<AccountState> accountStateFuture =
        accountTemplateBase.getStateFunction().apply(ACCOUNT_ADDRESS);
    assertTrue(accountStateFuture.get().hasResult());
  }

  @Test
  public void testSignWithLocalAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final Account account = ClientManagedAccount.of(new AergoKeyGenerator().create());
    final Transaction transaction = new Transaction();
    final ResultOrErrorFuture<Transaction> accountStateFuture =
        accountTemplateBase.getSignFunction().apply(account, transaction);
    assertTrue(accountStateFuture.get().hasResult());
  }

  @Test
  public void testSignWithRemoteAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(() -> Blockchain.Tx.newBuilder()
        .setHash(copyFrom(of(randomUUID().toString().getBytes())))
        .setBody(Blockchain.TxBody.newBuilder()
            .setSign(copyFrom(of(randomUUID().toString().getBytes()))))
        .build());
    when(aergoService.signTX(any())).thenReturn(mockListenableFuture);

    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final Account account = mock(Account.class);
    final Transaction transaction = new Transaction();
    final ResultOrErrorFuture<Transaction> signedTransactionStateFuture =
        accountTemplateBase.getSignFunction().apply(account, transaction);
    assertTrue(signedTransactionStateFuture.get().hasResult());
  }

  @Test
  public void testVerifyWithLocalAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = ClientManagedAccount.of(key);
    final Transaction transaction = new Transaction();
    final BytesValue sign = key.sign(transaction.calculateHash().getBytesValue().get());
    transaction.setSignature(Signature.of(sign));
    transaction.calculateHash();

    final ResultOrErrorFuture<Boolean> verifyResultFuture =
        accountTemplateBase.getVerifyFunction().apply(account, transaction);
    assertTrue(verifyResultFuture.get().hasResult());
  }

  @Test
  public void testVerifyWithRemoteAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.VerifyResult.newBuilder()
            .setError(Rpc.VerifyStatus.VERIFY_STATUS_OK).build());
    when(aergoService.verifyTX(any())).thenReturn(mockListenableFuture);

    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final Account account = mock(Account.class);
    final Transaction transaction = new Transaction();
    final ResultOrErrorFuture<Boolean> verifyResultFuture =
        accountTemplateBase.getVerifyFunction().apply(account, transaction);
    assertTrue(verifyResultFuture.get().hasResult());
  }

}
