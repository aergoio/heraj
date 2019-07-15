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
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.AergoSignVerifier;
import java.util.List;
import org.junit.Test;

public class KeyStoreOperationIT extends AbstractIT {

  @Test
  public void shouldCreateSuccessfully() {
    // given
    final String password = randomUUID().toString();
    final List<AccountAddress> before = aergoClient.getKeyStoreOperation().list();

    // when
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
    final Authentication auth = new Authentication(created, password);
    boolean unlockResult = aergoClient.getKeyStoreOperation().unlock(auth);

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
    final Authentication auth = new Authentication(created, invalidPassword);
    boolean unlockResult = aergoClient.getKeyStoreOperation().unlock(auth);

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
    final Authentication auth = new Authentication(created, invalidPassword);

    try {
      // when
      aergoClient.getKeyStoreOperation().exportKey(auth);
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
    final String newPassword = randomUUID().toString();
    final EncryptedPrivateKey encrypted = key.export(oldPassword);

    // when
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
    final String newPassword = randomUUID().toString();
    final EncryptedPrivateKey encrypted = key.export(oldPassword);

    try {
      // when
      final String invalidPassword = randomUUID().toString();
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
    final String newPassword = randomUUID().toString();
    final EncryptedPrivateKey encrypted = key.export(oldPassword);

    // when
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
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(created)
        .to(created)
        .amount(Aer.GIGA_ONE)
        .nonce(1L)
        .build();
    final Authentication auth = new Authentication(created, password);
    aergoClient.getKeyStoreOperation().unlock(auth);

    // when
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
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(created)
        .to(created)
        .amount(Aer.GIGA_ONE)
        .nonce(1L)
        .build();

    try {
      // when
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
