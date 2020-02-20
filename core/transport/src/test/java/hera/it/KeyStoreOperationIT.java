/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.AergoSignVerifier;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeyStoreOperationIT extends AbstractIT {

  protected static AergoClient aergoClient;

  protected final NonceProvider nonceProvider = new SimpleNonceProvider();
  protected final AergoKey rich = AergoKey
      .of("486NtKUZPWxZj6n8m2axqyGggHJTJmusgLWnFW1s2ckvTGNsy5K5R6LZssRN2hijJQ2pbFGgk", "1234");
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

    final AccountState state = aergoClient.getAccountOperation().getState(rich.getAddress());
    logger.debug("Rich state: {}", state);
    nonceProvider.bindNonce(state);;
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(rich.getPrincipal())
        .to(key.getAddress())
        .amount(Aer.of("10000", Unit.AERGO))
        .nonce(nonceProvider.incrementAndGetNonce(rich.getPrincipal()))
        .build();
    final Transaction signed = rich.sign(rawTransaction);
    logger.debug("Fill tx: ", signed);
    aergoClient.getTransactionOperation().commit(signed);
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
  public void shouldCreateSuccessfully() {
    // when
    final String password = randomUUID().toString();
    final List<AccountAddress> before = aergoClient.getKeyStoreOperation().list();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);

    // then
    final List<AccountAddress> after = aergoClient.getKeyStoreOperation().list();
    assertEquals(before.size() + 1, after.size());
    assertTrue(false == before.contains(created));
    assertTrue(after.contains(created));
  }

  @Test
  public void shouldUnlockOnValidAuth() {
    // given
    final String password = randomUUID().toString();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);

    // when
    final Authentication authentication = new Authentication(created, password);
    boolean unlockResult = aergoClient.getKeyStoreOperation().unlock(authentication);

    // then
    assertTrue(unlockResult);
  }

  @Test
  public void shouldUnlockFailOnInvalidAuth() {
    // given
    final String password = randomUUID().toString();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);

    // when
    final String invalidPassword = randomUUID().toString();
    final Authentication authentication = new Authentication(created, invalidPassword);
    boolean unlockResult = aergoClient.getKeyStoreOperation().unlock(authentication);

    // then
    assertTrue(false == unlockResult);
  }

  @Test
  public void shouldLockOnValidAuth() {
    // given
    final String password = randomUUID().toString();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);
    final Authentication valid = new Authentication(created, password);
    aergoClient.getKeyStoreOperation().unlock(valid);

    // when
    final boolean lockResult = aergoClient.getKeyStoreOperation().lock(valid);

    // then
    assertTrue(lockResult);
  }

  @Test
  public void shouldLockFailOnInvalidAuth() {
    // given
    final String password = randomUUID().toString();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);
    final Authentication valid = new Authentication(created, password);
    aergoClient.getKeyStoreOperation().unlock(valid);

    // when
    final Authentication invalid = new Authentication(created, randomUUID().toString());
    final boolean lockResult = aergoClient.getKeyStoreOperation().lock(invalid);

    // then
    assertTrue(false == lockResult);
  }

  @Test
  public void shouldExportCreatedOne() {
    // given
    final String password = randomUUID().toString();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);

    // when
    final Authentication auth = new Authentication(created, password);
    final EncryptedPrivateKey exported = aergoClient.getKeyStoreOperation().exportKey(auth);

    // then
    final AergoKey decrypted = AergoKey.of(exported, password);
    assertEquals(decrypted.getAddress(), created);
  }

  @Test
  public void shouldExportFailOnInvalidAuth() {
    // given
    final String password = randomUUID().toString();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);
    final String invalidPassword = randomUUID().toString();
    final Authentication authentication = new Authentication(created, invalidPassword);

    try {
      // when
      aergoClient.getKeyStoreOperation().exportKey(authentication);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldImportOnValidPassword() {
    // given
    final AergoKey key = new AergoKeyGenerator().create();
    final String oldPassword = randomUUID().toString();
    final EncryptedPrivateKey encrypted = key.export(oldPassword);

    // when
    final String newPassword = randomUUID().toString();
    final AccountAddress imported =
        aergoClient.getKeyStoreOperation().importKey(encrypted, oldPassword, newPassword);

    // then
    final List<AccountAddress> stored = aergoClient.getKeyStoreOperation().list();
    assertTrue(stored.contains(imported));
  }

  @Test
  public void shouldImportFailOnInvalidPassword() {
    // given
    final AergoKey key = new AergoKeyGenerator().create();
    final String oldPassword = randomUUID().toString();
    final EncryptedPrivateKey encrypted = key.export(oldPassword);

    try {
      // when
      final String invalidPassword = randomUUID().toString();
      final String newPassword = randomUUID().toString();
      aergoClient.getKeyStoreOperation().importKey(encrypted, invalidPassword, newPassword);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldUnlockWithImportOne() {
    // given
    final AergoKey key = new AergoKeyGenerator().create();
    final String oldPassword = randomUUID().toString();
    final EncryptedPrivateKey encrypted = key.export(oldPassword);

    // when
    final String newPassword = randomUUID().toString();
    final AccountAddress imported =
        aergoClient.getKeyStoreOperation().importKey(encrypted, oldPassword, newPassword);
    final Authentication auth = new Authentication(imported, newPassword);
    final boolean unlockResult = aergoClient.getKeyStoreOperation().unlock(auth);

    // then
    assertTrue(unlockResult);
  }

  @Test
  public void shouldSignUnlockedOne() {
    // given
    final String password = randomUUID().toString();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);
    final Authentication auth = new Authentication(created, password);
    aergoClient.getKeyStoreOperation().unlock(auth);

    // when
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(created)
        .to(created)
        .amount(Aer.GIGA_ONE)
        .nonce(1L)
        .build();
    final Transaction signed = aergoClient.getKeyStoreOperation().sign(rawTransaction);

    // then
    final boolean verifyResult = new AergoSignVerifier().verify(signed);
    assertTrue(verifyResult);
  }

  @Test
  public void shouldSignFailOnLockedOne() {
    // given
    final String password = randomUUID().toString();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);

    try {
      // when
      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .chainIdHash(aergoClient.getCachedChainIdHash())
          .from(created)
          .to(created)
          .amount(Aer.GIGA_ONE)
          .nonce(1L)
          .build();
      aergoClient.getKeyStoreOperation().sign(rawTransaction);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldSendOnUnlockedOne() {
    // given
    final String password = randomUUID().toString();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);
    fund(created);
    waitForNextBlockToGenerate();
    aergoClient.getKeyStoreOperation().unlock(Authentication.of(created, password));

    // when
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    aergoClient.getTransactionOperation().send(created, recipient, Aer.GIGA_ONE);
    waitForNextBlockToGenerate();

    // then
    final AccountState refreshed = aergoClient.getAccountOperation().getState(recipient);
    assertEquals(Aer.GIGA_ONE, refreshed.getBalance());
  }

  @Test
  public void shouldSendFailOnLockedOne() {
    // given
    final String password = randomUUID().toString();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);
    fund(created);
    waitForNextBlockToGenerate();

    try {
      // when
      final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
      aergoClient.getTransactionOperation().send(created, recipient, Aer.GIGA_ONE);
      fail();
    } catch (Exception e) {
      // then
    }
  }

}
