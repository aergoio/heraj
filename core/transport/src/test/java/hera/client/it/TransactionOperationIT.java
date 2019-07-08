/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.BytesValue;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.RpcCommitException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class TransactionOperationIT extends AbstractIT {

  protected final Aer amount = Aer.of("100", Unit.GAER);

  @Test
  public void testCommit() {
    final AergoKey key = createNewKey();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final AccountState preState = aergoClient.getAccountOperation().getState(key.getAddress());

    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(key.getAddress())
        .to(recipient)
        .amount(amount)
        .nonce(1L)
        .build();

    final Transaction signed = key.sign(rawTransaction);
    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

    final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertTrue(false == notConfirmed.isConfirmed());

    waitForNextBlockToGenerate();

    final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    final AccountState refreshed = aergoClient.getAccountOperation().getState(key.getAddress());
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
    final AergoKey key = createNewKey();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();

    // give sender an name
    final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');
    aergoClient.getAccountOperation().createName(key, name,
        nonceProvider.incrementAndGetNonce(key.getAddress()));

    waitForNextBlockToGenerate();

    // snapshot pre state
    final AccountState preState = aergoClient.getAccountOperation().getState(key.getAddress());

    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(name)
        .to(recipient)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
        .build();

    final Transaction signed = key.sign(rawTransaction);

    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

    final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertTrue(false == notConfirmed.isConfirmed());

    waitForNextBlockToGenerate();

    final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    final AccountState refreshed = aergoClient.getAccountOperation().getState(key.getAddress());
    assertTrue(true == confirmed.isConfirmed());
    verifyState(preState, refreshed);
  }

  @Test
  public void testCommitWithNameRecipient() {
    final AergoKey key = createNewKey();

    // give recipient an name
    final AergoKey recipient = createNewKey();
    final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');
    aergoClient.getAccountOperation().createName(recipient, name,
        nonceProvider.incrementAndGetNonce(recipient.getAddress()));

    waitForNextBlockToGenerate();

    // snapshot pre state
    final AccountState preState = aergoClient.getAccountOperation().getState(key.getAddress());

    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(key.getAddress())
        .to(name)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
        .build();

    final Transaction signed = key.sign(rawTransaction);

    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

    final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertTrue(false == notConfirmed.isConfirmed());

    waitForNextBlockToGenerate();

    final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    final AccountState refreshed = aergoClient.getAccountOperation().getState(key.getAddress());
    assertTrue(true == confirmed.isConfirmed());
    verifyState(preState, refreshed);
  }

  @Test
  public void testCommitOnEmptyAmount() {
    final AergoKey key = createNewKey();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();

    // snapshot pre state
    final AccountState preState = aergoClient.getAccountOperation().getState(key.getAddress());

    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(key.getAddress())
        .to(recipient)
        .amount(Aer.EMPTY)
        .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
        .build();

    final Transaction signed = key.sign(rawTransaction);

    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

    final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertTrue(false == notConfirmed.isConfirmed());

    waitForNextBlockToGenerate();

    final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    final AccountState refreshed = aergoClient.getAccountOperation().getState(key.getAddress());
    assertTrue(true == confirmed.isConfirmed());
    assertEquals(preState.getNonce() + 1, refreshed.getNonce());
    verifyState(preState, refreshed);
  }

  @Test
  public void testCommitOnInvalidSender() {
    final AergoKey key = createNewKey();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();

    final AccountAddress invalidSender = AccountAddress.of(BytesValue.EMPTY);
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(invalidSender)
        .to(recipient)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
        .build();

    try {
      final Transaction signed = key.sign(rawTransaction);
      aergoClient.getTransactionOperation().commit(signed);
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testCommitOnInvalidRecipient() {
    final AergoKey key = createNewKey();
    final AccountAddress invalidRecipient = AccountAddress.of(BytesValue.EMPTY);
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(key.getAddress())
        .to(invalidRecipient)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
        .build();

    final Transaction signed = key.sign(rawTransaction);

    try {
      aergoClient.getTransactionOperation().commit(signed);
      fail();
    } catch (RpcCommitException e) {
      // good we expected this
    }
  }

  @Test
  public void testCommitOnInvalidNonce() {
    final AergoKey key = createNewKey();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final RawTransaction rawTransaction =
        RawTransaction.newBuilder(aergoClient.getCachedChainIdHash())
            .from(key.getAddress())
            .to(recipient)
            .amount(amount)
            .nonce(0L)
            .build();

    final Transaction signed = key.sign(rawTransaction);

    try {
      aergoClient.getTransactionOperation().commit(signed);
      fail();
    } catch (RpcCommitException e) {
      // good we expected this
    }
  }

  @Test
  public void testCommitOnInvalidSignature() {
    final AergoKey key = createNewKey();
    final AergoKey recipient = createNewKey();
    final RawTransaction rawTransaction =
        RawTransaction.newBuilder(aergoClient.getCachedChainIdHash())
            .from(key.getAddress())
            .to(recipient.getAddress())
            .amount(amount)
            .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
            .build();

    // sign with recipient
    final Transaction signed = recipient.sign(rawTransaction);

    try {
      aergoClient.getTransactionOperation().commit(signed);
      fail();
    } catch (RpcCommitException e) {
      // good we expected this
    }
  }

  @Test
  public void testCommitOnStaked() {
    final AergoKey key = createNewKey();
    final AccountState state = aergoClient.getAccountOperation().getState(key.getAddress());

    aergoClient.getAccountOperation().stake(key, state.getBalance(),
        nonceProvider.incrementAndGetNonce(key.getAddress()));

    waitForNextBlockToGenerate();

    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final RawTransaction rawTransaction =
        RawTransaction.newBuilder(aergoClient.getCachedChainIdHash())
            .from(key.getAddress())
            .to(recipient)
            .amount(amount) // staked amount
            .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
            .build();

    final Transaction signed = key.sign(rawTransaction);

    try {
      aergoClient.getTransactionOperation().commit(signed);
      fail();
    } catch (RpcCommitException e) {
      // good we expected this
    }
  }
}
