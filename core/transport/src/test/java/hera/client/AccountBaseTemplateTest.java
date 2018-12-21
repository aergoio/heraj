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
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.Aer.Unit;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
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
    accountTemplateBase.contextProvider = () -> context;
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
  public void testCreateName() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction()).thenReturn(t -> ResultOrErrorFutureFactory
        .supply(() -> new TxHash(BytesValue.of(randomUUID().toString().getBytes()))));
    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);
    accountTemplateBase.transactionBaseTemplate = mockTransactionBaseTemplate;

    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key);
    final ResultOrErrorFuture<TxHash> nameTxHash =
        accountTemplateBase.getCreateNameFunction().apply(account, randomUUID().toString(),
            account.incrementAndGetNonce());
    assertTrue(nameTxHash.get().hasResult());
  }

  @Test
  public void testUpdateName() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction()).thenReturn(t -> ResultOrErrorFutureFactory
        .supply(() -> new TxHash(BytesValue.of(randomUUID().toString().getBytes()))));
    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);
    accountTemplateBase.transactionBaseTemplate = mockTransactionBaseTemplate;

    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    final Account newOwner = new AccountFactory().create(new AergoKeyGenerator().create());
    final ResultOrErrorFuture<TxHash> nameTxHash =
        accountTemplateBase.getUpdateNameFunction().apply(account, randomUUID().toString(),
            newOwner.getAddress(), account.incrementAndGetNonce());
    assertTrue(nameTxHash.get().hasResult());
  }

  @Test
  public void testGetNameOwner() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.NameInfo.newBuilder().build());
    when(aergoService.getNameInfo(any())).thenReturn(mockListenableFuture);

    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);
    accountTemplateBase.aergoService = aergoService;

    final ResultOrErrorFuture<AccountAddress> nameTxHash =
        accountTemplateBase.getGetNameOwnerFunction().apply(randomUUID().toString());
    assertTrue(nameTxHash.get().hasResult());
  }

  @Test
  public void testSignWithLocalAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key);
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(ACCOUNT_ADDRESS)
        .to(ACCOUNT_ADDRESS)
        .amount("1000", Unit.AER)
        .nonce(1L)
        .build();
    final ResultOrErrorFuture<Transaction> accountStateFuture =
        accountTemplateBase.getSignFunction().apply(account, rawTransaction);
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
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(ACCOUNT_ADDRESS)
        .to(ACCOUNT_ADDRESS)
        .amount("1000", Unit.AER)
        .nonce(1L)
        .build();
    final ResultOrErrorFuture<Transaction> signedTransactionStateFuture =
        accountTemplateBase.getSignFunction().apply(account, rawTransaction);
    assertTrue(signedTransactionStateFuture.get().hasResult());
  }

  @Test
  public void testVerifyWithLocalAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key);
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(ACCOUNT_ADDRESS)
        .to(ACCOUNT_ADDRESS)
        .amount("1000", Unit.AER)
        .nonce(1L)
        .build();
    final Transaction transaction = key.sign(rawTransaction);
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
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(ACCOUNT_ADDRESS)
        .to(ACCOUNT_ADDRESS)
        .amount("1000", Unit.AER)
        .nonce(1L)
        .build();
    final Transaction transaction = new Transaction(rawTransaction, Signature.of(BytesValue.EMPTY),
        TxHash.of(BytesValue.EMPTY), null, 0, false);

    final ResultOrErrorFuture<Boolean> verifyResultFuture =
        accountTemplateBase.getVerifyFunction().apply(account, transaction);
    assertTrue(verifyResultFuture.get().hasResult());
  }

}
