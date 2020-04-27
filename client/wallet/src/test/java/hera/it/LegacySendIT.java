/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.model.KeyAlias;
import hera.wallet.WalletApiFactory;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LegacySendIT extends AbstractLegacyWalletIT {

  protected static AergoClient aergoClient;

  protected final AergoKey rich = AergoKey
      .of("47DbxpF2GtHPNrqLe6rCGFLVVSY66F1yK6fuETtLALQeCjXbesLtyA1q6XQzSYJNXKn44vwSb", "1234");
  protected AergoKey key;

  @BeforeClass
  public static void before() {
    final TestClientFactory clientFactory = new TestClientFactory();
    aergoClient = clientFactory.get();
  }

  @AfterClass
  public static void after() throws Exception {
    aergoClient.close();
  }

  @Before
  public void setUp() {
    key = new AergoKeyGenerator().create();

    final NonceProvider nonceProvider = new SimpleNonceProvider();
    final AccountState state = aergoClient.getAccountOperation().getState(rich.getAddress());
    logger.debug("Rich state: {}", state);
    nonceProvider.bindNonce(state);
    ;
    aergoClient.getTransactionOperation()
        .sendTx(rich, key.getAddress(), Aer.of("10000", Unit.AERGO),
            nonceProvider.incrementAndGetNonce(rich.getPrincipal()), Fee.INFINITY,
            BytesValue.EMPTY);
    waitForNextBlockToGenerate();
  }

  @Test
  public void shouldSignAndCommitOnUnlocked() throws IOException {
    // given
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    final Authentication auth = Authentication.of(key.getAddress(), password);
    wallet.unlock(auth);
    try {
      // when
      final AergoKey recipient = new AergoKeyGenerator().create();
      final RawTransaction rawTransaction = RawTransaction.newBuilder(wallet.getCachedChainIdHash())
          .from(key.getAddress())
          .to(recipient.getAddress())
          .amount(Aer.of("100", Unit.AER))
          .nonce(wallet.incrementAndGetNonce())
          .build();
      final Transaction signed = wallet.sign(rawTransaction);
      final TxHash hash = wallet.commit(signed);
      waitForNextBlockToGenerate();

      // then
      assertNotNull(wallet.getTransaction(hash));
    } finally {
      wallet.lock(auth);
    }
  }

  @Test
  public void shouldSignFailOnOnLocked() throws Exception {
    // given
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    final Authentication auth = Authentication.of(key.getAddress(), password);
    wallet.unlock(auth);
    wallet.lock(auth);
    try {
      // when
      final AergoKey recipient = new AergoKeyGenerator().create();
      final RawTransaction rawTransaction = RawTransaction.newBuilder(wallet.getCachedChainIdHash())
          .from(key.getAddress())
          .to(recipient.getAddress())
          .amount(Aer.of("100", Unit.AER))
          .nonce(1L)
          .build();
      wallet.sign(rawTransaction);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldSendOnUnlocked() throws IOException {
    // given
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    final Authentication auth = Authentication.of(key.getAddress(), password);
    wallet.unlock(auth);
    try {
      final long preCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState preState = wallet.getAccountState();

      // when
      final AergoKey recipient = new AergoKeyGenerator().create();
      wallet.send(recipient.getAddress(), Aer.of("100", Unit.GAER));
      wallet.send(recipient.getAddress(), Aer.of("100", Unit.GAER));
      wallet.send(recipient.getAddress(), Aer.of("100", Unit.GAER));
      waitForNextBlockToGenerate();

      // then
      final long postCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState postState = wallet.getAccountState();
      validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 3);
    } finally {
      wallet.lock(auth);
    }
  }

  @Test
  public void shouldSendFailOnLocked() throws IOException {
    // given
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    final Authentication auth = Authentication.of(key.getAddress(), password);
    wallet.unlock(auth);
    wallet.lock(auth);
    try {
      // when
      final AergoKey recipient = new AergoKeyGenerator().create();
      wallet.send(recipient.getAddress(), Aer.of("100", Unit.AERGO));
      fail();
    } catch (Throwable e) {
      // then
    }
  }

  @Test
  public void testSendWithNameOnUnlocked() throws IOException {
    // given
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    final Authentication auth = Authentication.of(key.getAddress(), password);
    wallet.unlock(auth);

    final String recipientName = randomUUID().toString().substring(0, 12).replace('-', 'a');
    wallet.createName(recipientName);
    waitForNextBlockToGenerate();
    final AergoKey recipient = new AergoKeyGenerator().create();
    wallet.updateName(recipientName, recipient.getAddress());
    waitForNextBlockToGenerate();
    final long preCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState preState = wallet.getAccountState();
    try {
      // when
      wallet.send(recipientName, Aer.of("100", Unit.GAER));
      wallet.send(recipientName, Aer.of("100", Unit.GAER));
      wallet.send(recipientName, Aer.of("100", Unit.GAER));
      waitForNextBlockToGenerate();

      // then
      final long postCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState postState = wallet.getAccountState();
      validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 3);
    } finally {
      wallet.lock(auth);
    }
  }

  @Test
  public void shouldSendWithNameFailOnlocked() throws IOException {
    // given
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    final Authentication auth = Authentication.of(key.getAddress(), password);
    wallet.unlock(auth);

    final String recipientName = randomUUID().toString().substring(0, 12).replace('-', 'a');
    wallet.createName(recipientName);
    waitForNextBlockToGenerate();
    final AergoKey recipient = new AergoKeyGenerator().create();
    wallet.updateName(recipientName, recipient.getAddress());
    waitForNextBlockToGenerate();

    wallet.lock(auth);
    try {
      // when
      wallet.send(recipientName, Aer.of("100", Unit.AERGO));
      fail();
    } catch (Throwable e) {
      // then
    }
  }

  @Test
  public void shouldSendWithInvalidNameFail() {
    // given
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    final Authentication auth = Authentication.of(key.getAddress(), password);
    wallet.unlock(auth);
    try {
      // when
      final String recipientName = randomUUID().toString().substring(0, 12).replace('-', 'a');
      wallet.send(recipientName, Aer.of("100", Unit.GAER));
      fail();
    } catch (Exception e) {
      // then
    }
  }

}
