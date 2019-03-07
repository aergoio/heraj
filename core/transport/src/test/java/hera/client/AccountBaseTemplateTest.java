/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.Context;
import hera.ContextProvider;
import hera.api.function.Function1;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.BytesValue;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.StakingInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@PrepareForTest({AergoRPCServiceFutureStub.class})
public class AccountBaseTemplateTest extends AbstractTestCase {

  protected final String password = randomUUID().toString();

  protected AccountBaseTemplate supplyAccountTemplateBase(
      final AergoRPCServiceFutureStub aergoService) {
    final AccountBaseTemplate accountTemplateBase = new AccountBaseTemplate();
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
  public void testGetStateAsync() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Blockchain.State> mockListenableFuture =
        service.submit(new Callable<Blockchain.State>() {
          @Override
          public Blockchain.State call() throws Exception {
            return Blockchain.State.newBuilder().build();
          }
        });
    when(aergoService.getState(any(Rpc.SingleBytes.class))).thenReturn(mockListenableFuture);

    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final FinishableFuture<AccountState> accountStateFuture =
        accountTemplateBase.getStateFunction().apply(accountAddress);
    assertNotNull(accountStateFuture.get());
  }

  @Test
  public void testCreateName() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    future.success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())));
    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction())
        .thenReturn(new Function1<Transaction, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Transaction t) {
            return future;
          }
        });

    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);
    accountTemplateBase.transactionBaseTemplate = mockTransactionBaseTemplate;

    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key);
    final FinishableFuture<TxHash> nameTxHash =
        accountTemplateBase.getCreateNameFunction().apply(account, randomUUID().toString(),
            account.incrementAndGetNonce());
    assertNotNull(nameTxHash.get());
  }

  @Test
  public void testUpdateName() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    future.success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())));
    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction())
        .thenReturn(new Function1<Transaction, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Transaction t) {
            return future;
          }
        });

    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);
    accountTemplateBase.transactionBaseTemplate = mockTransactionBaseTemplate;

    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    final Account newOwner = new AccountFactory().create(new AergoKeyGenerator().create());
    final FinishableFuture<TxHash> nameTxHash =
        accountTemplateBase.getUpdateNameFunction().apply(account, randomUUID().toString(),
            newOwner.getAddress(), account.incrementAndGetNonce());
    assertNotNull(nameTxHash.get());
  }

  @Test
  public void testGetNameOwner() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.NameInfo> mockListenableFuture =
        service.submit(new Callable<Rpc.NameInfo>() {
          @Override
          public Rpc.NameInfo call() throws Exception {
            return Rpc.NameInfo.newBuilder().build();
          }
        });
    when(aergoService.getNameInfo(any(Rpc.Name.class))).thenReturn(mockListenableFuture);

    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);
    accountTemplateBase.aergoService = aergoService;

    final FinishableFuture<AccountAddress> nameTxHash =
        accountTemplateBase.getGetNameOwnerFunction().apply(randomUUID().toString());
    assertNotNull(nameTxHash.get());
  }

  @Test
  public void testSignWithLocalAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key);
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(accountAddress)
        .to(accountAddress)
        .amount("1000", Unit.AER)
        .nonce(1L)
        .build();
    final FinishableFuture<Transaction> accountStateFuture =
        accountTemplateBase.getSignFunction().apply(account, rawTransaction);
    assertNotNull(accountStateFuture.get());
  }

  @Test
  public void testStake() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    future.success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())));
    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction())
        .thenReturn(new Function1<Transaction, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Transaction t) {
            return future;
          }
        });

    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);
    accountTemplateBase.transactionBaseTemplate = mockTransactionBaseTemplate;

    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key);
    final FinishableFuture<TxHash> staTxHash = accountTemplateBase.getStakingFunction()
        .apply(account, Aer.ONE, account.incrementAndGetNonce());
    assertNotNull(staTxHash.get());
  }

  @Test
  public void testUnstake() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    future.success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())));
    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction())
        .thenReturn(new Function1<Transaction, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Transaction t) {
            return future;
          }
        });

    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);
    accountTemplateBase.transactionBaseTemplate = mockTransactionBaseTemplate;

    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key);
    final FinishableFuture<TxHash> staTxHash = accountTemplateBase.getUnstakingFunction()
        .apply(account, Aer.ONE, account.incrementAndGetNonce());
    assertNotNull(staTxHash.get());
  }

  @Test
  public void testGetStakingInfo() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.Staking> mockListenableFuture =
        service.submit(new Callable<Rpc.Staking>() {
          @Override
          public Rpc.Staking call() throws Exception {
            return Rpc.Staking.newBuilder().setAmount(copyFrom(Aer.ONE)).build();
          }
        });
    when(aergoService.getStaking(any(Rpc.SingleBytes.class))).thenReturn(mockListenableFuture);

    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);
    accountTemplateBase.aergoService = aergoService;

    final FinishableFuture<StakingInfo> stakingInfo =
        accountTemplateBase.getStakingInfoFunction().apply(accountAddress);
    assertNotNull(stakingInfo.get());
  }

  @Test
  public void testSignWithRemoteAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Blockchain.Tx> mockListenableFuture =
        service.submit(new Callable<Blockchain.Tx>() {
          @Override
          public Blockchain.Tx call() throws Exception {
            return Blockchain.Tx.newBuilder()
                .setHash(copyFrom(of(randomUUID().toString().getBytes())))
                .setBody(Blockchain.TxBody.newBuilder()
                    .setSign(copyFrom(of(randomUUID().toString().getBytes()))))
                .build();
          }
        });
    when(aergoService.signTX(any(Blockchain.Tx.class))).thenReturn(mockListenableFuture);

    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final Account account = mock(Account.class);
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(accountAddress)
        .to(accountAddress)
        .amount("1000", Unit.AER)
        .nonce(1L)
        .build();
    final FinishableFuture<Transaction> signedTransactionStateFuture =
        accountTemplateBase.getSignFunction().apply(account, rawTransaction);
    assertNotNull(signedTransactionStateFuture.get());
  }

  @Test
  public void testVerifyWithLocalAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key);
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(accountAddress)
        .to(accountAddress)
        .amount("1000", Unit.AER)
        .nonce(1L)
        .build();
    final Transaction transaction = key.sign(rawTransaction);
    final FinishableFuture<Boolean> verifyResultFuture =
        accountTemplateBase.getVerifyFunction().apply(account, transaction);
    assertNotNull(verifyResultFuture.get());
  }

  @Test
  public void testVerifyWithRemoteAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.VerifyResult> mockListenableFuture =
        service.submit(new Callable<Rpc.VerifyResult>() {
          @Override
          public Rpc.VerifyResult call() throws Exception {
            return Rpc.VerifyResult.newBuilder().setError(Rpc.VerifyStatus.VERIFY_STATUS_OK)
                .build();
          }
        });
    when(aergoService.verifyTX(any(Blockchain.Tx.class))).thenReturn(mockListenableFuture);

    final AccountBaseTemplate accountTemplateBase = supplyAccountTemplateBase(aergoService);

    final Account account = mock(Account.class);
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(accountAddress)
        .to(accountAddress)
        .amount("1000", Unit.AER)
        .nonce(1L)
        .build();
    final Transaction transaction = new Transaction(rawTransaction, Signature.of(BytesValue.EMPTY),
        TxHash.of(BytesValue.EMPTY), null, 0, false);

    final FinishableFuture<Boolean> verifyResultFuture =
        accountTemplateBase.getVerifyFunction().apply(account, transaction);
    assertNotNull(verifyResultFuture.get());
  }

}
