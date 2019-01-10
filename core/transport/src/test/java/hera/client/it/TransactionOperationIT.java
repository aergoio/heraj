/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.CommitException;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class TransactionOperationIT extends AbstractIT {

  protected final Aer amount = Aer.of("100", Unit.GAER);

  @Test
  public void testCommit() {
    for (final Account account : supplyAccounts()) {
      final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());
      final AccountState preState = aergoClient.getAccountOperation().getState(account);

      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(account)
          .to(recipient)
          .amount(amount)
          .nonce(account.incrementAndGetNonce())
          .fee(fee)
          .build();

      unlockAccount(account, password);
      final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);
      lockAccount(account, password);

      final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

      final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
      assertTrue(false == notConfirmed.isConfirmed());

      waitForNextBlockToGenerate();

      final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
      final AccountState refreshed = aergoClient.getAccountOperation().getState(account);
      assertTrue(true == confirmed.isConfirmed());
      verifyState(preState, refreshed, amount);
    }
  }

  @Test
  public void testCommitWithNameSender() {
    for (final Account account : supplyAccounts()) {
      final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());

      // give sender an name
      unlockAccount(account, password);
      final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');
      aergoClient.getAccountOperation().createName(account, name,
          account.incrementAndGetNonce());
      lockAccount(account, password);

      waitForNextBlockToGenerate();

      // snapshot pre state
      final AccountState preState = aergoClient.getAccountOperation().getState(account);

      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(name)
          .to(recipient)
          .amount(amount)
          .nonce(account.incrementAndGetNonce())
          .fee(fee)
          .build();

      unlockAccount(account, password);
      final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);
      lockAccount(account, password);

      final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

      final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
      assertTrue(false == notConfirmed.isConfirmed());

      waitForNextBlockToGenerate();

      final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
      final AccountState refreshed = aergoClient.getAccountOperation().getState(account);
      assertTrue(true == confirmed.isConfirmed());
      verifyState(preState, refreshed, amount);
    }
  }

  @Test
  public void testCommitWithNameRecipient() {
    for (final Account account : supplyAccounts()) {

      // give recipient an name
      final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());
      final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');
      aergoClient.getAccountOperation().createName(recipient, name,
          recipient.incrementAndGetNonce());

      waitForNextBlockToGenerate();

      // snapshot pre state
      final AccountState preState = aergoClient.getAccountOperation().getState(account);

      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(account)
          .to(name)
          .amount(amount)
          .nonce(account.incrementAndGetNonce())
          .fee(fee)
          .build();

      unlockAccount(account, password);
      final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);
      lockAccount(account, password);

      final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

      final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
      assertTrue(false == notConfirmed.isConfirmed());

      waitForNextBlockToGenerate();

      final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
      final AccountState refreshed = aergoClient.getAccountOperation().getState(account);
      assertTrue(true == confirmed.isConfirmed());
      verifyState(preState, refreshed, amount);
    }
  }

  @Test
  public void testCommitOnEmptyAmount() {
    for (final Account account : supplyAccounts()) {
      final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());

      // snapshot pre state
      final AccountState preState = aergoClient.getAccountOperation().getState(account);

      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(account)
          .to(recipient)
          .amount(null)
          .nonce(account.incrementAndGetNonce())
          .fee(fee)
          .build();

      unlockAccount(account, password);
      final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);
      lockAccount(account, password);

      final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

      final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
      assertTrue(false == notConfirmed.isConfirmed());

      waitForNextBlockToGenerate();

      final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
      final AccountState refreshed = aergoClient.getAccountOperation().getState(account);
      assertTrue(true == confirmed.isConfirmed());
      assertEquals(preState.getNonce() + 1, refreshed.getNonce());
      assertEquals(preState.getBalance().subtract(fee.getPrice()), refreshed.getBalance());
    }
  }

  @Test
  public void testCommitOnInvalidSender() {
    for (final Account account : supplyAccounts()) {
      final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());

      final AccountAddress invalidSender = null;
      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(invalidSender)
          .to(recipient)
          .amount(amount)
          .nonce(account.incrementAndGetNonce())
          .fee(fee)
          .build();

      unlockAccount(account, password);
      try {
        aergoClient.getAccountOperation().sign(account, rawTransaction);
        if (null == account.getKey()) {
          fail();
        }
      } catch (Exception e) {
        // good we expected this
      } finally {
        lockAccount(account, password);
      }
    }
  }

  @Test
  public void testCommitOnInvalidRecipient() {
    for (final Account account : supplyAccounts()) {
      final AccountAddress invalidRecipient = null;
      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(account)
          .to(invalidRecipient)
          .amount(amount)
          .nonce(account.incrementAndGetNonce())
          .fee(fee)
          .build();

      unlockAccount(account, password);
      final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);
      lockAccount(account, password);

      try {
        aergoClient.getTransactionOperation().commit(signed);
        fail();
      } catch (CommitException e) {
        // good we expected this
      }
    }
  }

  @Test
  public void testCommitOnInvalidNonce() {
    for (final Account account : supplyAccounts()) {
      final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());
      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(account)
          .to(recipient)
          .amount(amount)
          .nonce(account.getNonce()) // invalid
          .fee(fee)
          .build();

      unlockAccount(account, password);
      final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);
      lockAccount(account, password);

      try {
        aergoClient.getTransactionOperation().commit(signed);
        fail();
      } catch (CommitException e) {
        // good we expected this
      }
    }
  }

  @Test
  public void testCommitOnInvalidSignature() {
    for (final Account account : supplyAccounts()) {
      final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());
      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(account)
          .to(recipient)
          .amount(amount)
          .nonce(account.getNonce()) // invalid
          .fee(fee)
          .build();

      // sign with recipient
      unlockAccount(recipient, password);
      final Transaction signed = aergoClient.getAccountOperation().sign(recipient, rawTransaction);
      lockAccount(recipient, password);

      try {
        aergoClient.getTransactionOperation().commit(signed);
        fail();
      } catch (CommitException e) {
        // good we expected this
      }
    }
  }

  @Test
  public void testCommitOnStaked() {
    for (final Account account : supplyAccounts()) {
      final AccountState state = aergoClient.getAccountOperation().getState(account);

      unlockAccount(account, password);
      aergoClient.getAccountOperation().stake(account, state.getBalance(),
          account.incrementAndGetNonce());
      lockAccount(account, password);

      waitForNextBlockToGenerate();

      final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());
      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(account)
          .to(recipient)
          .amount(amount) // staked amount
          .nonce(account.incrementAndGetNonce())
          .fee(fee)
          .build();

      unlockAccount(account, password);
      final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);
      lockAccount(account, password);

      try {
        aergoClient.getTransactionOperation().commit(signed);
        fail();
      } catch (CommitException e) {
        // good we expected this
      }
    }
  }

  @Test
  public void testCommitOnLockedKeyStoreAccount() {
    final Account account = aergoClient.getKeyStoreOperation().create(password);
    final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(account)
        .to(recipient)
        .amount(amount)
        .nonce(account.getNonce()) // invalid
        .fee(fee)
        .build();

    // unlockAccount(account, password);
    try {
      aergoClient.getAccountOperation().sign(account, rawTransaction);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testSendWithKeyStoreAccount() {
    final Account account = aergoClient.getKeyStoreOperation().create(password);
    final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());

    // snapshot pre state
    final AccountState preState = aergoClient.getAccountOperation().getState(account);

    unlockAccount(account, password);
    final TxHash txHash = aergoClient.getTransactionOperation().send(account.getAddress(),
        recipient.getAddress(), amount);
    lockAccount(account, password);

    final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertTrue(false == notConfirmed.isConfirmed());

    waitForNextBlockToGenerate();

    final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    final AccountState refreshed = aergoClient.getAccountOperation().getState(account);
    assertTrue(true == confirmed.isConfirmed());
    assertEquals(preState.getNonce() + 1, refreshed.getNonce());
    assertEquals(preState.getBalance().subtract(amount.add(Fee.getDefaultFee().getPrice())),
        refreshed.getBalance());
  }

  @Test
  public void testSendOnLockedKeyStoreAccount() {
    final Account account = aergoClient.getKeyStoreOperation().create(password);
    final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());

    try {
      // unlockAccount(account, password);
      aergoClient.getTransactionOperation().send(account.getAddress(),
          recipient.getAddress(), amount);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

}
