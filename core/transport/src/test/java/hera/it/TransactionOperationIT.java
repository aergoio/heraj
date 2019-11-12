/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.RpcCommitException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class TransactionOperationIT extends AbstractIT {

  @Test
  public void shouldSendAergoByCommit() {
    // given
    final AergoKey senderKey = createNewKey();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.AERGO_ONE;
    final AccountState preState = aergoClient.getAccountOperation().getState(recipient);

    // when
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(senderKey.getAddress())
        .to(recipient)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(senderKey.getAddress()))
        .build();
    final Transaction signed = senderKey.sign(rawTransaction);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();

    // then
    final AccountState refreshed = aergoClient.getAccountOperation().getState(recipient);
    assertEquals(preState.getBalance().add(amount), refreshed.getBalance());
  }

  @Test
  public void shouldNotConfirmedJustAfterCommit() {
    // given
    final AergoKey senderKey = createNewKey();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();

    // when
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(senderKey.getAddress())
        .to(recipient)
        .amount(Aer.AERGO_ONE)
        .nonce(nonceProvider.incrementAndGetNonce(senderKey.getAddress()))
        .build();
    final Transaction signed = senderKey.sign(rawTransaction);
    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

    // then
    final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertTrue(false == notConfirmed.isConfirmed());
  }

  @Test
  public void shouldSendAergoByNameSender() {
    // given
    final AergoKey senderKey = createNewKey();
    // create name
    final String name = randomName();
    aergoClient.getAccountOperation().createName(senderKey, name,
        nonceProvider.incrementAndGetNonce(senderKey.getAddress()));
    waitForNextBlockToGenerate();
    // before state
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.AERGO_ONE;
    final AccountState preState = aergoClient.getAccountOperation().getState(recipient);

    // when
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(name)
        .to(recipient)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(senderKey.getAddress()))
        .build();
    final Transaction signed = senderKey.sign(rawTransaction);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();

    // then
    final AccountState refreshed =
        aergoClient.getAccountOperation().getState(recipient);
    assertEquals(preState.getBalance().add(amount), refreshed.getBalance());
  }

  @Test
  public void shouldSendAergoByNameRecipient() {
    // given
    final AergoKey senderKey = createNewKey();
    final AergoKey recipient = createNewKey();
    // create name
    final String name = randomName();
    aergoClient.getAccountOperation().createName(recipient, name,
        nonceProvider.incrementAndGetNonce(recipient.getAddress()));
    waitForNextBlockToGenerate();
    // before state
    final Aer amount = Aer.AERGO_ONE;
    final AccountState preState =
        aergoClient.getAccountOperation().getState(recipient.getAddress());

    // when
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(senderKey.getAddress())
        .to(name)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(senderKey.getAddress()))
        .build();
    final Transaction signed = senderKey.sign(rawTransaction);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();

    // then
    final AccountState refreshed =
        aergoClient.getAccountOperation().getState(recipient.getAddress());
    assertEquals(preState.getBalance().add(amount), refreshed.getBalance());
  }

  @Test
  public void shouldCommitOnEmptyAmount() {
    // given
    final AergoKey senderKey = createNewKey();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.EMPTY;
    final AccountState preState = aergoClient.getAccountOperation().getState(recipient);

    // when
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(senderKey.getAddress())
        .to(recipient)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(senderKey.getAddress()))
        .build();
    final Transaction signed = senderKey.sign(rawTransaction);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();

    // then
    final AccountState refreshed =
        aergoClient.getAccountOperation().getState(recipient);
    assertEquals(preState.getBalance(), refreshed.getBalance());
  }

  @Test
  public void shouldNotCommitOnAlreadyCommitedTx() {
    // given
    final AergoKey senderKey = createNewKey();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.AERGO_ONE;
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(senderKey.getAddress())
        .to(recipient)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(senderKey.getAddress()))
        .build();
    final Transaction signed = senderKey.sign(rawTransaction);
    aergoClient.getTransactionOperation().commit(signed);

    try {
      // when
      aergoClient.getTransactionOperation().commit(signed);
      fail();
    } catch (RpcCommitException e) {
      // then
      // TODO: when to use TX_ALREADY_EXISTS?
      // assertEquals(RpcCommitException.CommitStatus.TX_ALREADY_EXISTS, e.getCommitStatus());
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldNotCommitOnInvalidRecipient() {
    // given
    final AergoKey senderKey = createNewKey();
    final AccountAddress recipient = AccountAddress.EMPTY;
    final Aer amount = Aer.AERGO_ONE;

    try {
      // when
      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .chainIdHash(aergoClient.getCachedChainIdHash())
          .from(senderKey.getAddress())
          .to(recipient)
          .amount(amount)
          .nonce(nonceProvider.incrementAndGetNonce(senderKey.getAddress()))
          .build();
      final Transaction signed = senderKey.sign(rawTransaction);
      aergoClient.getTransactionOperation().commit(signed);
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldNotCommitOnLowNonce() {
    // given
    final AergoKey senderKey = createNewKey();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.AERGO_ONE;

    try {
      // when
      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .chainIdHash(aergoClient.getCachedChainIdHash())
          .from(senderKey.getAddress())
          .to(recipient)
          .amount(amount)
          .nonce(0L)
          .build();
      final Transaction signed = senderKey.sign(rawTransaction);
      aergoClient.getTransactionOperation().commit(signed);
    } catch (RpcCommitException e) {
      // then
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldCommitFailOnInvalidSignature() {
    // given
    final AergoKey senderKey = createNewKey();
    final AergoKey recipient = createNewKey();

    try {
      // when
      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .chainIdHash(aergoClient.getCachedChainIdHash())
          .from(senderKey.getAddress())
          .to(recipient.getAddress())
          .amount(Aer.AERGO_ONE)
          .nonce(nonceProvider.incrementAndGetNonce(senderKey.getAddress()))
          .build();
      // sign with recipient
      final Transaction signed = recipient.sign(rawTransaction);
      aergoClient.getTransactionOperation().commit(signed);
      fail();
    } catch (RpcCommitException e) {
      // then
      // assertEquals(RpcCommitException.CommitStatus.TX_INVALID_SIGNATURE, e.getCommitStatus());
    }
  }

}
