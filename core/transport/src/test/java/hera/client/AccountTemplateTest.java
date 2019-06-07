/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.ACCOUNT_CREATE_NAME;
import static hera.TransportConstants.ACCOUNT_GETNAMEOWNER;
import static hera.TransportConstants.ACCOUNT_GETSTAKINGINFO;
import static hera.TransportConstants.ACCOUNT_GETSTATE;
import static hera.TransportConstants.ACCOUNT_STAKING;
import static hera.TransportConstants.ACCOUNT_UNSTAKING;
import static hera.TransportConstants.ACCOUNT_UPDATE_NAME;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.Context;
import hera.ContextProvider;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.function.Function3;
import hera.api.function.Function4;
import hera.api.function.WithIdentity;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.StakeInfo;
import hera.api.model.TxHash;
import hera.client.internal.AccountBaseTemplate;
import hera.client.internal.FinishableFuture;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({AccountBaseTemplate.class, EncryptedPrivateKey.class})
public class AccountTemplateTest extends AbstractTestCase {

  protected static final String PASSWORD = randomUUID().toString();

  protected AccountTemplate supplyAccountTemplate(
      final AccountBaseTemplate accountBaseTemplate) {
    final AccountTemplate accountTemplate = new AccountTemplate();
    accountTemplate.accountBaseTemplate = accountBaseTemplate;
    accountTemplate.setContextProvider(new ContextProvider() {
      @Override
      public Context get() {
        return context;
      }
    });
    return accountTemplate;
  }

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testGetState() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final AccountState mockState = mock(AccountState.class);
    final FinishableFuture<AccountState> future = new FinishableFuture<AccountState>();
    future.success(mockState);
    when(base.getStateFunction())
        .thenReturn(new Function1<AccountAddress, FinishableFuture<AccountState>>() {
          @Override
          public FinishableFuture<AccountState> apply(AccountAddress t) {
            return future;
          }
        });

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final AccountState accountState = accountTemplate.getState(accountAddress);
    assertNotNull(accountState);
    assertEquals(ACCOUNT_GETSTATE,
        ((WithIdentity) accountTemplate.getStateFunction()).getIdentity());
  }

  @Test
  public void testCreateName() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final TxHash mockHash = mock(TxHash.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    future.success(mockHash);
    when(base.getCreateNameFunction())
        .thenReturn(new Function3<Signer, String, Long, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Signer t1, String t2, Long t3) {
            return future;
          }
        });

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Signer signer = new AergoKeyGenerator().create();
    final TxHash nameTxHash = accountTemplate.createName(signer, randomUUID().toString(), 0L);
    assertNotNull(nameTxHash);
    assertEquals(ACCOUNT_CREATE_NAME,
        ((WithIdentity) accountTemplate.getCreateNameFunction()).getIdentity());
  }

  @Test
  public void testUpdateName() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final TxHash mockHash = mock(TxHash.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    future.success(mockHash);
    when(base.getUpdateNameFunction()).thenReturn(
        new Function4<Signer, String, AccountAddress, Long, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Signer t1, String t2, AccountAddress t3,
              Long t4) {
            return future;
          }
        });

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Signer owner = new AergoKeyGenerator().create();
    final AccountAddress newOwner = new AergoKeyGenerator().create().getAddress();
    final TxHash updateTxHash = accountTemplate.updateName(owner, randomUUID().toString(),
        newOwner, 0L);
    assertNotNull(updateTxHash);
    assertEquals(ACCOUNT_UPDATE_NAME,
        ((WithIdentity) accountTemplate.getUpdateNameFunction()).getIdentity());
  }

  @Test
  public void testGetNameOwner() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final FinishableFuture<AccountAddress> future = new FinishableFuture<AccountAddress>();
    future.success(new AccountAddress(BytesValue.EMPTY));
    when(base.getGetNameOwnerFunction())
        .thenReturn(new Function2<String, Long, FinishableFuture<AccountAddress>>() {
          @Override
          public FinishableFuture<AccountAddress> apply(String t1, Long t2) {
            return future;
          }
        });

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final AccountAddress owner = accountTemplate.getNameOwner(randomUUID().toString());
    assertNotNull(owner);
    assertEquals(ACCOUNT_GETNAMEOWNER,
        ((WithIdentity) accountTemplate.getNameOwnerFunction()).getIdentity());
  }

  @Test
  public void testStake() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final TxHash mockHash = mock(TxHash.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    future.success(mockHash);
    when(base.getStakingFunction()).thenReturn(
        new Function3<Signer, Aer, Long, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Signer t1, Aer t2, Long t4) {
            return future;
          }
        });

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Signer signer = new AergoKeyGenerator().create();
    final TxHash stakingTxHash = accountTemplate.stake(signer, Aer.GIGA_ONE, 0L);
    assertNotNull(stakingTxHash);
    assertEquals(ACCOUNT_STAKING,
        ((WithIdentity) accountTemplate.getStakingFunction()).getIdentity());
  }

  @Test
  public void testUnstake() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final TxHash mockHash = mock(TxHash.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    future.success(mockHash);
    when(base.getUnstakingFunction()).thenReturn(
        new Function3<Signer, Aer, Long, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Signer t1, Aer t2, Long t4) {
            return future;
          }
        });

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Signer signer = new AergoKeyGenerator().create();
    final TxHash unstakingTxHash = accountTemplate.unstake(signer, Aer.GIGA_ONE, 0L);
    assertNotNull(unstakingTxHash);
    assertEquals(ACCOUNT_UNSTAKING,
        ((WithIdentity) accountTemplate.getUnstakingFunction()).getIdentity());
  }

  @Test
  public void testGetStakingInfo() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final FinishableFuture<StakeInfo> future = new FinishableFuture<StakeInfo>();
    future.success(mock(StakeInfo.class));
    when(base.getStakingInfoFunction())
        .thenReturn(new Function1<AccountAddress, FinishableFuture<StakeInfo>>() {
          @Override
          public FinishableFuture<StakeInfo> apply(AccountAddress t) {
            return future;
          }
        });

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Signer signer = new AergoKeyGenerator().create();
    final StakeInfo stakingInfo = accountTemplate.getStakingInfo(signer.getPrincipal());
    assertNotNull(stakingInfo);
    assertEquals(ACCOUNT_GETSTAKINGINFO,
        ((WithIdentity) accountTemplate.getStakingInfoFunction()).getIdentity());
  }

}
