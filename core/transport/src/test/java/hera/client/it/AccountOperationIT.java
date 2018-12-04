/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.Optional;
import org.junit.Test;

public class AccountOperationIT extends AbstractIT {

  @Test
  public void testCreateLocally() throws Exception {
    final Account account = createClientAccount();
    logger.info("Created account: {}", account);
    assertNotNull(account);

    Optional<AccountAddress> filtered = aergoClient.getKeyStoreOperation().list().stream()
        .filter(a -> a.equals(account.getAddress())).findFirst();
    assertTrue(!filtered.isPresent());
  }

  @Test
  public void testCreateRemotely() {
    final String password = randomUUID().toString();
    final Account account = createServerAccount(password);
    logger.info("Created account: {}", account);

    Optional<AccountAddress> filtered = aergoClient.getKeyStoreOperation().list().stream()
        .filter(a -> a.equals(account.getAddress())).findFirst();
    assertTrue(filtered.isPresent());
  }

  @Test
  public void testSignLocally() throws Exception {
    final Account account = createClientAccount();

    final Transaction transaction = buildTransaction(account);
    signTransaction(account, transaction);

    assertNotNull(transaction.getSignature().getSign());
    assertNotNull(transaction.getSignature().getTxHash());
  }

  @Test
  public void testSignRemotely() {
    final String password = randomUUID().toString();
    final Account account = createServerAccount(password);

    final Transaction transaction = buildTransaction(account);

    unlockAccount(account, password);
    signTransaction(account, transaction);
    lockAccount(account, password);

    assertNotNull(transaction.getSignature().getSign());
    assertNotNull(transaction.getSignature().getTxHash());
  }

  @Test
  public void testCreateLocallyAndImport() throws Exception {
    final AergoKey key = new AergoKeyGenerator().create();

    final String password = randomUUID().toString();
    final EncryptedPrivateKey encryptedKey = key.getEncryptedPrivateKey(password);

    final String newpassword = randomUUID().toString();
    final Account imported =
        aergoClient.getKeyStoreOperation().importKey(encryptedKey, password, newpassword);

    assertEquals(key.getAddress(), imported.getAddress());
  }

  @Test
  public void testCreateRemotelyAndExport() {
    final String password = randomUUID().toString();
    final Account created = createServerAccount(password);

    final EncryptedPrivateKey encryptedKey = aergoClient.getKeyStoreOperation()
        .exportKey(Authentication.of(created.getAddress(), password));

    assertNotNull(encryptedKey);
  }

}
