/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.api.model.Account;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.StakeInfo;
import hera.exception.RpcCommitException;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class AccountOperationIT extends AbstractIT {

  @Test
  public void testCreateAndGetName() {
    final Account account = supplyLocalAccount();
    final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');

    aergoClient.getAccountOperation().createName(account, name, account.incrementAndGetNonce());

    waitForNextBlockToGenerate();

    assertEquals(account.getAddress(), aergoClient.getAccountOperation().getNameOwner(name));

    final Account passed = new AccountFactory().create(new AergoKeyGenerator().create());

    aergoClient.getAccountOperation().updateName(account, name, passed.getAddress(),
        account.incrementAndGetNonce());

    waitForNextBlockToGenerate();

    assertEquals(passed.getAddress(), aergoClient.getAccountOperation().getNameOwner(name));
  }

  @Test
  public void testCreateWithInvalidNonce() {
    final Account account = supplyLocalAccount();
    final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');

    try {
      aergoClient.getAccountOperation().createName(account, name, account.getRecentlyUsedNonce());
      fail();
    } catch (RpcCommitException e) {
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void testUpdateWithInvalidNonce() {
    final Account account = supplyLocalAccount();
    final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');

    aergoClient.getAccountOperation().createName(account, name, account.incrementAndGetNonce());

    waitForNextBlockToGenerate();

    assertEquals(account.getAddress(), aergoClient.getAccountOperation().getNameOwner(name));

    final Account passed = new AccountFactory().create(new AergoKeyGenerator().create());

    try {
      aergoClient.getAccountOperation().updateName(account, name, passed.getAddress(),
          account.getRecentlyUsedNonce());
      fail();
    } catch (RpcCommitException e) {
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void testStakingAndUnstaking() {
    if (!isDpos()) {
      return;
    }

    final Account account = supplyLocalAccount();
    final AccountState state = aergoClient.getAccountOperation().getState(account);
    aergoClient.getAccountOperation().stake(account, state.getBalance(),
        account.incrementAndGetNonce());

    waitForNextBlockToGenerate();

    final StakeInfo stakingInfoAfterStaked =
        aergoClient.getAccountOperation().getStakingInfo(account.getAddress());
    assertEquals(account.getAddress(), stakingInfoAfterStaked.getAddress());
    assertEquals(state.getBalance(), stakingInfoAfterStaked.getAmount());

    try {
      aergoClient.getAccountOperation().unstake(account, stakingInfoAfterStaked.getAmount(),
          account.incrementAndGetNonce());
    } catch (Exception e) {
      // good we expected this not enough time has passed to unstake
    }
  }

  @Test
  public void testStakingWithInvalidNonce() {
    if (!isDpos()) {
      return;
    }

    final Account account = supplyLocalAccount();
    final AccountState state = aergoClient.getAccountOperation().getState(account);
    try {
      aergoClient.getAccountOperation().stake(account, state.getBalance(),
          account.getRecentlyUsedNonce());
      fail();
    } catch (RpcCommitException e) {
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void testUnstakingWithInvalidNonce() {
    if (!isDpos()) {
      return;
    }

    final Account account = supplyLocalAccount();
    final AccountState state = aergoClient.getAccountOperation().getState(account);

    aergoClient.getAccountOperation().stake(account, state.getBalance(),
        account.incrementAndGetNonce());

    waitForNextBlockToGenerate();

    final StakeInfo stakingInfo =
        aergoClient.getAccountOperation().getStakingInfo(account.getAddress());
    assertEquals(account.getAddress(), stakingInfo.getAddress());
    assertEquals(state.getBalance(), stakingInfo.getAmount());

    try {
      aergoClient.getAccountOperation().unstake(account, stakingInfo.getAmount(),
          account.getRecentlyUsedNonce());
      fail();
    } catch (RpcCommitException e) {
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

}
