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
import hera.api.model.StakingInfo;
import hera.exception.RpcCommitException;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class AccountOperationIT extends AbstractIT {

  @Test
  public void testCreateAndGetName() {
    for (final Account account : supplyAccounts()) {
      final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');

      unlockAccount(account, password);
      aergoClient.getAccountOperation().createName(account, name, account.incrementAndGetNonce());
      lockAccount(account, password);

      waitForNextBlockToGenerate();

      assertEquals(account.getAddress(), aergoClient.getAccountOperation().getNameOwner(name));

      final Account passed = new AccountFactory().create(new AergoKeyGenerator().create());

      unlockAccount(account, password);
      aergoClient.getAccountOperation().updateName(account, name, passed.getAddress(),
          account.incrementAndGetNonce());
      lockAccount(account, password);

      waitForNextBlockToGenerate();

      assertEquals(passed.getAddress(), aergoClient.getAccountOperation().getNameOwner(name));
    }
  }

  @Test
  public void testCreateWithInvalidNonce() {
    for (final Account account : supplyAccounts()) {
      final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');

      unlockAccount(account, password);
      try {
        aergoClient.getAccountOperation().createName(account, name, account.getRecentlyUsedNonce());
        fail();
      } catch (RpcCommitException e) {
        assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
      }
      lockAccount(account, password);
    }
  }

  @Test
  public void testUpdateWithInvalidNonce() {
    for (final Account account : supplyAccounts()) {
      final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');

      unlockAccount(account, password);
      aergoClient.getAccountOperation().createName(account, name, account.incrementAndGetNonce());
      lockAccount(account, password);

      waitForNextBlockToGenerate();

      assertEquals(account.getAddress(), aergoClient.getAccountOperation().getNameOwner(name));

      final Account passed = new AccountFactory().create(new AergoKeyGenerator().create());

      unlockAccount(account, password);
      try {
        aergoClient.getAccountOperation().updateName(account, name, passed.getAddress(),
            account.getRecentlyUsedNonce());
        fail();
      } catch (RpcCommitException e) {
        assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
      }
      lockAccount(account, password);
    }
  }

  @Test
  public void testStakingAndUnstaking() {
    for (final Account account : supplyAccounts()) {
      final AccountState state = aergoClient.getAccountOperation().getState(account);
      unlockAccount(account, password);
      aergoClient.getAccountOperation().stake(account, state.getBalance(),
          account.incrementAndGetNonce());

      waitForNextBlockToGenerate();

      final StakingInfo stakingInfoAfterStaked =
          aergoClient.getAccountOperation().getStakingInfo(account.getAddress());
      assertEquals(account.getAddress(), stakingInfoAfterStaked.getAddress());
      assertEquals(state.getBalance(), stakingInfoAfterStaked.getAmount());

      try {
        aergoClient.getAccountOperation().unstake(account, stakingInfoAfterStaked.getAmount(),
            account.incrementAndGetNonce());
      } catch (Exception e) {
        // good we expected this not enough time has passed to unstake
      }
      lockAccount(account, password);
    }
  }

  @Test
  public void testStakingWithInvalidNonce() {
    for (final Account account : supplyAccounts()) {
      final AccountState state = aergoClient.getAccountOperation().getState(account);
      unlockAccount(account, password);
      try {
        aergoClient.getAccountOperation().stake(account, state.getBalance(),
            account.getRecentlyUsedNonce());
        fail();
      } catch (RpcCommitException e) {
        assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
      }
      lockAccount(account, password);
    }
  }

  @Test
  public void testUnstakingWithInvalidNonce() {
    for (final Account account : supplyAccounts()) {
      final AccountState state = aergoClient.getAccountOperation().getState(account);
      unlockAccount(account, password);

      aergoClient.getAccountOperation().stake(account, state.getBalance(),
          account.incrementAndGetNonce());

      waitForNextBlockToGenerate();

      final StakingInfo stakingInfo =
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
      lockAccount(account, password);
    }
  }

}
