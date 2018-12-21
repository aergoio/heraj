/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.Account;
import hera.api.model.AccountFactory;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class KeyStoreOperationIT extends AbstractIT {

  @Test
  public void testCreateAndExport() {
    final Account account = aergoClient.getKeyStoreOperation().create(password);
    assertTrue(aergoClient.getKeyStoreOperation().list().contains(account.getAddress()));

    final Authentication authentication = Authentication.of(account.getAddress(), password);
    final EncryptedPrivateKey exported =
        aergoClient.getKeyStoreOperation().exportKey(authentication);
    final AergoKey key = AergoKey.of(exported, password);
    assertEquals(key.getAddress(), account.getAddress());
  }

  @Test
  public void testCreateAndExportWithInvalidAuthentication() {
    final Account account = aergoClient.getKeyStoreOperation().create(password);
    assertTrue(aergoClient.getKeyStoreOperation().list().contains(account.getAddress()));

    final Authentication authentication =
        Authentication.of(account.getAddress(), randomUUID().toString());
    try {
      aergoClient.getKeyStoreOperation().exportKey(authentication);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testImportAndExport() {
    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    final String oldPassword = randomUUID().toString();
    final String newPassword = randomUUID().toString();
    final EncryptedPrivateKey encrypted = account.getKey().export(oldPassword);
    final Account imported =
        aergoClient.getKeyStoreOperation().importKey(encrypted, oldPassword, newPassword);

    assertTrue(aergoClient.getKeyStoreOperation().list().contains(account.getAddress()));
    assertEquals(account.getAddress(), imported.getAddress());

    final Authentication authentication = Authentication.of(imported.getAddress(), newPassword);
    final EncryptedPrivateKey exported =
        aergoClient.getKeyStoreOperation().exportKey(authentication);
    final AergoKey key = AergoKey.of(exported, newPassword);
    assertEquals(account.getAddress(), key.getAddress());
  }

}
