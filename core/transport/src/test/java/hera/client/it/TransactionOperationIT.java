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
import hera.api.model.BytesValue;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.RpcCommitException;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class TransactionOperationIT extends AbstractIT {

  protected final Aer amount = Aer.of("100", Unit.GAER);

  @Test
  public void testCommit() {
    final Account account = supplyLocalAccount();
    final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());
    final AccountState preState = aergoClient.getAccountOperation().getState(account);

    final RawTransaction rawTransaction =
        RawTransaction.newBuilder(aergoClient.getCachedChainIdHash())
            .from(account)
            .to(recipient)
            .amount(amount)
            .nonce(account.incrementAndGetNonce())
            .build();

    final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);

    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

    final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertTrue(false == notConfirmed.isConfirmed());

    waitForNextBlockToGenerate();

    final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    final AccountState refreshed = aergoClient.getAccountOperation().getState(account);
    assertTrue(true == confirmed.isConfirmed());

    verifyState(preState, refreshed);

    try {
      aergoClient.getTransactionOperation().commit(confirmed);
      fail();
    } catch (RpcCommitException e) {
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void testCommitWithNameSender() {
    final Account account = supplyLocalAccount();
    final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());

    // give sender an name
    final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');
    aergoClient.getAccountOperation().createName(account, name,
        account.incrementAndGetNonce());

    waitForNextBlockToGenerate();

    // snapshot pre state
    final AccountState preState = aergoClient.getAccountOperation().getState(account);

    final RawTransaction rawTransaction =
        RawTransaction.newBuilder(aergoClient.getCachedChainIdHash())
            .from(name)
            .to(recipient)
            .amount(amount)
            .nonce(account.incrementAndGetNonce())
            .build();

    final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);

    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

    final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertTrue(false == notConfirmed.isConfirmed());

    waitForNextBlockToGenerate();

    final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    final AccountState refreshed = aergoClient.getAccountOperation().getState(account);
    assertTrue(true == confirmed.isConfirmed());
    verifyState(preState, refreshed);
  }

  @Test
  public void testCommitWithNameRecipient() {
    final Account account = supplyLocalAccount();

    // give recipient an name
    final Account recipient = supplyLocalAccount();
    final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');
    aergoClient.getAccountOperation().createName(recipient, name,
        recipient.incrementAndGetNonce());

    waitForNextBlockToGenerate();

    // snapshot pre state
    final AccountState preState = aergoClient.getAccountOperation().getState(account);

    final RawTransaction rawTransaction =
        RawTransaction.newBuilder(aergoClient.getCachedChainIdHash())
            .from(account)
            .to(name)
            .amount(amount)
            .nonce(account.incrementAndGetNonce())
            .build();

    final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);

    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

    final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertTrue(false == notConfirmed.isConfirmed());

    waitForNextBlockToGenerate();

    final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    final AccountState refreshed = aergoClient.getAccountOperation().getState(account);
    assertTrue(true == confirmed.isConfirmed());
    verifyState(preState, refreshed);
  }

  @Test
  public void testCommitOnEmptyAmount() {
    final Account account = supplyLocalAccount();
    final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());

    // snapshot pre state
    final AccountState preState = aergoClient.getAccountOperation().getState(account);

    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(account)
        .to(recipient)
        .amount(Aer.EMPTY)
        .nonce(account.incrementAndGetNonce())
        .build();

    final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);

    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

    final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertTrue(false == notConfirmed.isConfirmed());

    waitForNextBlockToGenerate();

    final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    final AccountState refreshed = aergoClient.getAccountOperation().getState(account);
    assertTrue(true == confirmed.isConfirmed());
    assertEquals(preState.getNonce() + 1, refreshed.getNonce());
    verifyState(preState, refreshed);
  }

  @Test
  public void testCommitOnInvalidSender() {
    final Account account = supplyLocalAccount();
    final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());

    final AccountAddress invalidSender = AccountAddress.of(BytesValue.EMPTY);
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(invalidSender)
        .to(recipient)
        .amount(amount)
        .nonce(account.incrementAndGetNonce())
        .build();

    try {
      aergoClient.getAccountOperation().sign(account, rawTransaction);
      if (null == account.getKey()) {
        fail();
      }
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testCommitOnInvalidRecipient() {
    final Account account = supplyLocalAccount();
    final AccountAddress invalidRecipient = AccountAddress.of(BytesValue.EMPTY);
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(account)
        .to(invalidRecipient)
        .amount(amount)
        .nonce(account.incrementAndGetNonce())
        .build();

    final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);

    try {
      aergoClient.getTransactionOperation().commit(signed);
      fail();
    } catch (RpcCommitException e) {
      // good we expected this
    }
  }

  @Test
  public void testCommitOnInvalidNonce() {
    final Account account = supplyLocalAccount();
    final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());
    final RawTransaction rawTransaction =
        RawTransaction.newBuilder(aergoClient.getCachedChainIdHash())
            .from(account)
            .to(recipient)
            .amount(amount)
            .nonce(account.getRecentlyUsedNonce()) // invalid
            .build();

    final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);

    try {
      aergoClient.getTransactionOperation().commit(signed);
      fail();
    } catch (RpcCommitException e) {
      // good we expected this
    }
  }

  @Test
  public void testCommitOnInvalidSignature() {
    final Account account = supplyLocalAccount();
    final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());
    final RawTransaction rawTransaction =
        RawTransaction.newBuilder(aergoClient.getCachedChainIdHash())
            .from(account)
            .to(recipient)
            .amount(amount)
            .nonce(account.getRecentlyUsedNonce()) // invalid
            .build();

    // sign with recipient
    final Transaction signed = aergoClient.getAccountOperation().sign(recipient, rawTransaction);

    try {
      aergoClient.getTransactionOperation().commit(signed);
      fail();
    } catch (RpcCommitException e) {
      // good we expected this
    }
  }

  @Test
  public void testCommitOnStaked() {
    final Account account = supplyLocalAccount();
    final AccountState state = aergoClient.getAccountOperation().getState(account);

    aergoClient.getAccountOperation().stake(account, state.getBalance(),
        account.incrementAndGetNonce());

    waitForNextBlockToGenerate();

    final Account recipient = new AccountFactory().create(new AergoKeyGenerator().create());
    final RawTransaction rawTransaction =
        RawTransaction.newBuilder(aergoClient.getCachedChainIdHash())
            .from(account)
            .to(recipient)
            .amount(amount) // staked amount
            .nonce(account.incrementAndGetNonce())
            .build();

    final Transaction signed = aergoClient.getAccountOperation().sign(account, rawTransaction);

    try {
      aergoClient.getTransactionOperation().commit(signed);
      fail();
    } catch (RpcCommitException e) {
      // good we expected this
    }
  }
}
