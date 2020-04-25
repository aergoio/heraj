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
import hera.api.model.Aer.Unit;
import hera.api.model.Fee;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.TxReceipt;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.exception.CommitException;
import hera.exception.CommitException.CommitStatus;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TransactionOperationIT extends AbstractIT {

  protected static AergoClient aergoClient;

  protected final Fee fee = Fee.ZERO;
  protected final NonceProvider nonceProvider = new SimpleNonceProvider();
  protected final AergoKey rich = AergoKey
      .of("47GZYhinmvoUFtDvD8toTtceCbtb8Ry7jq5dTLzqqk6FHY11DW7BzdqDfsU3mjJpyTpiBQgmT", "1234");
  protected AergoKey key;

  @BeforeClass
  public static void before() {
    final TestClientFactory clientFactory = new TestClientFactory();
    aergoClient = clientFactory.get();
  }

  @AfterClass
  public static void after() {
    aergoClient.close();
  }

  @Before
  public void setUp() {
    key = new AergoKeyGenerator().create();
    final AccountState state = aergoClient.getAccountOperation().getState(rich.getAddress());
    logger.debug("Rich state: {}", state);
    nonceProvider.bindNonce(state);
    aergoClient.getTransactionOperation()
        .sendTx(rich, key.getAddress(), Aer.of("10000", Unit.AERGO),
            nonceProvider.incrementAndGetNonce(rich.getPrincipal()), Fee.INFINITY);
    waitForNextBlockToGenerate();
  }

  protected void fund(final Identity identity) {
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(rich.getPrincipal())
        .to(identity)
        .amount(Aer.of("10000", Unit.AERGO))
        .nonce(nonceProvider.incrementAndGetNonce(rich.getPrincipal()))
        .build();
    final Transaction signed = rich.sign(rawTransaction);
    logger.debug("Fill tx: ", signed);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();
  }

  @Test
  public void shouldSendAergoByCommit() {
    // when
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.AERGO_ONE;
    final AccountState preState = aergoClient.getAccountOperation().getState(recipient);
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(key.getAddress())
        .to(recipient)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
        .fee(fee)
        .build();
    final Transaction signed = key.sign(rawTransaction);
    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();

    // then
    final AccountState refreshed = aergoClient.getAccountOperation().getState(recipient);
    assertEquals(preState.getBalance().add(amount), refreshed.getBalance());
    final TxReceipt txReceipt = aergoClient.getTransactionOperation().getTxReceipt(txHash);
    assertEquals(recipient, txReceipt.getAccountAddress());
    assertEquals("SUCCESS", txReceipt.getStatus());
    assertEquals(txHash, txReceipt.getTxHash());
  }

  @Test
  public void shouldNotConfirmedJustAfterCommit() {
    // when
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(key.getAddress())
        .to(recipient)
        .amount(Aer.AERGO_ONE)
        .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
        .fee(fee)
        .build();
    final Transaction signed = key.sign(rawTransaction);
    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);

    // then
    final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertTrue(false == notConfirmed.isConfirmed());
  }

  @Test
  public void shouldSendAergoByNameSender() {
    // given
    final String name = randomName();
    aergoClient.getAccountOperation().createNameTx(key, name,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.AERGO_ONE;
    final AccountState preState = aergoClient.getAccountOperation().getState(recipient);

    // when
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(name)
        .to(recipient)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
        .fee(fee)
        .build();
    final Transaction signed = key.sign(rawTransaction);
    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();

    // then
    final AccountState refreshed =
        aergoClient.getAccountOperation().getState(recipient);
    assertEquals(preState.getBalance().add(amount), refreshed.getBalance());
    final TxReceipt txReceipt = aergoClient.getTransactionOperation().getTxReceipt(txHash);
    assertEquals(recipient, txReceipt.getAccountAddress());
    assertEquals("SUCCESS", txReceipt.getStatus());
    assertEquals(txHash, txReceipt.getTxHash());
  }

  @Test
  public void shouldSendAergoByNameRecipient() {
    // given
    final AergoKey recipient = new AergoKeyGenerator().create();
    fund(recipient.getAddress());
    final String name = randomName();
    aergoClient.getAccountOperation().createNameTx(recipient, name,
        nonceProvider.incrementAndGetNonce(recipient.getAddress()));
    waitForNextBlockToGenerate();
    final Aer amount = Aer.AERGO_ONE;
    final AccountState preState =
        aergoClient.getAccountOperation().getState(recipient.getAddress());

    // when
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(key.getAddress())
        .to(name)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
        .fee(fee)
        .build();
    final Transaction signed = key.sign(rawTransaction);
    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();

    // then
    final AccountState refreshed =
        aergoClient.getAccountOperation().getState(recipient.getAddress());
    assertEquals(preState.getBalance().add(amount), refreshed.getBalance());
    final TxReceipt txReceipt = aergoClient.getTransactionOperation().getTxReceipt(txHash);
    assertEquals(recipient.getAddress(), txReceipt.getAccountAddress());
    assertEquals("SUCCESS", txReceipt.getStatus());
    assertEquals(txHash, txReceipt.getTxHash());
  }

  @Test
  public void shouldCommitOnEmptyAmount() {
    // given
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.EMPTY;
    final AccountState preState = aergoClient.getAccountOperation().getState(recipient);

    // when
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(key.getAddress())
        .to(recipient)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
        .fee(fee)
        .build();
    final Transaction signed = key.sign(rawTransaction);
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
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.AERGO_ONE;
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(key.getAddress())
        .to(recipient)
        .amount(amount)
        .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
        .fee(fee)
        .build();
    final Transaction signed = key.sign(rawTransaction);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();

    try {
      // when
      aergoClient.getTransactionOperation().commit(signed);
      fail();
    } catch (CommitException e) {
      // then
      assertEquals(CommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldNotCommitOnInvalidRecipient() {
    // given
    final AccountAddress recipient = AccountAddress.EMPTY;
    final Aer amount = Aer.AERGO_ONE;

    try {
      // when
      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .chainIdHash(aergoClient.getCachedChainIdHash())
          .from(key.getAddress())
          .to(recipient)
          .amount(amount)
          .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
          .fee(fee)
          .build();
      final Transaction signed = key.sign(rawTransaction);
      aergoClient.getTransactionOperation().commit(signed);
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldNotCommitOnLowNonce() {
    // given
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.AERGO_ONE;

    try {
      // when
      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .chainIdHash(aergoClient.getCachedChainIdHash())
          .from(key.getAddress())
          .to(recipient)
          .amount(amount)
          .nonce(0L)
          .fee(fee)
          .build();
      final Transaction signed = key.sign(rawTransaction);
      aergoClient.getTransactionOperation().commit(signed);
    } catch (CommitException e) {
      // then
      assertEquals(CommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldCommitFailOnInvalidSignature() {
    try {
      // when
      final AergoKey recipient = new AergoKeyGenerator().create();
      fund(recipient.getAddress());
      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .chainIdHash(aergoClient.getCachedChainIdHash())
          .from(key.getAddress())
          .to(recipient.getAddress())
          .amount(Aer.AERGO_ONE)
          .nonce(nonceProvider.incrementAndGetNonce(key.getAddress()))
          .fee(fee)
          .build();
      // sign with recipient
      final Transaction signed = recipient.sign(rawTransaction);
      aergoClient.getTransactionOperation().commit(signed);
      fail();
    } catch (CommitException e) {
      // then
    }
  }

  @Test
  public void shouldSendTx() {
    // when
    final AergoKey sender = new AergoKeyGenerator().create();
    fund(sender.getAddress());
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer expected = Aer.AERGO_ONE;
    final TxHash txHash = aergoClient.getTransactionOperation()
        .sendTx(sender, recipient, expected, nonceProvider
            .incrementAndGetNonce(sender.getAddress()), Fee.INFINITY);
    waitForNextBlockToGenerate();

    // then
    final AccountState actual = aergoClient.getAccountOperation().getState(recipient);
    assertEquals(expected, actual.getBalance());
    final TxReceipt txReceipt = aergoClient.getTransactionOperation().getTxReceipt(txHash);
    assertEquals(recipient, txReceipt.getAccountAddress());
    assertEquals("SUCCESS", txReceipt.getStatus());
    assertEquals(txHash, txReceipt.getTxHash());
  }

  @Test
  public void shouldSendTxFailOnNoAmount() {
    try {
      // when
      final AergoKey sender = new AergoKeyGenerator().create();
      final AergoKey recipient = new AergoKeyGenerator().create();
      final Aer expected = Aer.AERGO_ONE;
      aergoClient.getTransactionOperation()
          .sendTx(sender, recipient.getAddress(), expected, nonceProvider
              .incrementAndGetNonce(sender.getAddress()), Fee.INFINITY);
      fail();
    } catch (CommitException e) {
      // then
      assertEquals(CommitStatus.INSUFFICIENT_BALANCE, e.getCommitStatus());
    }
  }

}
