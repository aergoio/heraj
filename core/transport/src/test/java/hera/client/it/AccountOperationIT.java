/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import hera.api.model.Account;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.strategy.NettyConnectStrategy;
import org.junit.Test;

public class AccountOperationIT extends AbstractIT {

  @Test
  public void testCreateAndExport() {
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .build();

    final String password = randomUUID().toString();
    final Account created = aergoClient.getAccountOperation().create(password);
    final EncryptedPrivateKey encryptedKey = aergoClient.getAccountOperation()
        .exportKey(Authentication.of(created.getAddress(), password));
    logger.info("Exported: {}", encryptedKey);
    assertNotNull(encryptedKey);

    aergoClient.close();
  }

  @Test
  public void testCreateLocallyAndImport() throws Exception {
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .build();

    final String password = randomUUID().toString();
    final String newPassword = randomUUID().toString();
    final AergoKey key = new AergoKeyGenerator().create();
    final EncryptedPrivateKey encryptedKey = key.getEncryptedPrivateKey(password);
    final Account imported =
        aergoClient.getAccountOperation().importKey(encryptedKey, password, newPassword);
    assertEquals(key.getAddress(), imported.getAddress());
    logger.info("Imported: {}", imported);

    aergoClient.close();
  }

}
