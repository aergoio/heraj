/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientConstants.ACCOUNT_CREATE_NAME;
import static hera.client.ClientConstants.ACCOUNT_GETNAMEOWNER;
import static hera.client.ClientConstants.ACCOUNT_GETSTAKINGINFO;
import static hera.client.ClientConstants.ACCOUNT_GETSTATE;
import static hera.client.ClientConstants.ACCOUNT_STAKING;
import static hera.client.ClientConstants.ACCOUNT_UNSTAKING;
import static hera.client.ClientConstants.ACCOUNT_UPDATE_NAME;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
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
import hera.client.internal.HerajFutures;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import java.util.concurrent.Future;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({AccountBaseTemplate.class, EncryptedPrivateKey.class})
public class AccountTemplateTest extends AbstractTestCase {

  protected static final String PASSWORD = randomUUID().toString();

  protected AccountTemplate supplyAccountTemplate(
      final AccountBaseTemplate accountBaseTemplate) {
    final AccountTemplate accountTemplate = new AccountTemplate();
    accountTemplate.accountBaseTemplate = accountBaseTemplate;
    accountTemplate.setContextProvider(ContextProvider.defaultProvider);
    return accountTemplate;
  }

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testGetState() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final Future<AccountState> future = HerajFutures.success(AccountState.newBuilder().build());
    when(base.getStateFunction())
        .thenReturn(new Function1<AccountAddress, Future<AccountState>>() {
          @Override
          public Future<AccountState> apply(AccountAddress t) {
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
    final Future<TxHash> future =
        HerajFutures.success(TxHash.of(BytesValue.of(randomUUID().toString().getBytes())));
    when(base.getCreateNameFunction())
        .thenReturn(new Function3<Signer, String, Long, Future<TxHash>>() {
          @Override
          public Future<TxHash> apply(Signer t1, String t2, Long t3) {
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
    final Future<TxHash> future =
        HerajFutures.success(TxHash.of(BytesValue.of(randomUUID().toString().getBytes())));
    when(base.getUpdateNameFunction()).thenReturn(
        new Function4<Signer, String, AccountAddress, Long, Future<TxHash>>() {
          @Override
          public Future<TxHash> apply(Signer t1, String t2, AccountAddress t3,
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
    final Future<AccountAddress> future = HerajFutures.success(AccountAddress.EMPTY);
    when(base.getGetNameOwnerFunction())
        .thenReturn(new Function2<String, Long, Future<AccountAddress>>() {
          @Override
          public Future<AccountAddress> apply(String t1, Long t2) {
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
    final Future<TxHash> future =
        HerajFutures.success(TxHash.of(BytesValue.of(randomUUID().toString().getBytes())));
    when(base.getStakingFunction()).thenReturn(
        new Function3<Signer, Aer, Long, Future<TxHash>>() {
          @Override
          public Future<TxHash> apply(Signer t1, Aer t2, Long t4) {
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
    final Future<TxHash> future =
        HerajFutures.success(TxHash.of(BytesValue.of(randomUUID().toString().getBytes())));
    when(base.getUnstakingFunction()).thenReturn(
        new Function3<Signer, Aer, Long, Future<TxHash>>() {
          @Override
          public Future<TxHash> apply(Signer t1, Aer t2, Long t4) {
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
    final Future<StakeInfo> future = HerajFutures.success(StakeInfo.newBuilder().build());
    when(base.getStakingInfoFunction())
        .thenReturn(new Function1<AccountAddress, Future<StakeInfo>>() {
          @Override
          public Future<StakeInfo> apply(AccountAddress t) {
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
