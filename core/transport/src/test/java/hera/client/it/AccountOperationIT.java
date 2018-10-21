/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.ClientManagedAccount;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.ServerManagedAccount;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.strategy.NettyConnectStrategy;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class AccountOperationIT extends AbstractIT {

  @Test
  public void testCreateLocally() throws Exception {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .build();

    // create aergokey
    final AergoKey key = new AergoKeyGenerator().create();
    final ClientManagedAccount account = ClientManagedAccount.of(key);
    logger.info("Created account: {}", account);

    assertNotNull(account);

    // close the client
    aergoClient.close();
  }

  @Test
  public void testCreateLocallyAndImport() throws Exception {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .build();

    // create aergokey
    final AergoKey key = new AergoKeyGenerator().create();

    // encrypt private key
    final String password = "password";
    final EncryptedPrivateKey encryptedKey = key.getEncryptedPrivateKey(password);

    // import encrypted private key
    final String newpassword = "newpassword";
    final ServerManagedAccount imported =
        aergoClient.getAccountOperation().importKey(encryptedKey, password, newpassword);
    logger.info("Imported: {}", imported);

    assertEquals(key.getAddress(), imported.getAddress());

    // close the client
    aergoClient.close();
  }

  @Test
  public void testSignLocally() throws Exception {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .build();

    // create an account
    final AergoKey key = new AergoKeyGenerator().create();
    final ClientManagedAccount account = ClientManagedAccount.of(key);

    // make a transaction
    final Transaction transaction = new Transaction();
    transaction.setNonce(account.nextNonce());
    transaction.setAmount(1L);
    transaction.setSender(account);
    transaction.setRecipient(
        AccountAddress.of(() -> "AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg"));
    logger.info("Raw transaction: {}", transaction);

    // sign it
    final Signature signature = aergoClient.getAccountOperation().sign(account, transaction);
    logger.info("Signature: {}", signature);

    assertNotNull(signature.getSign());
    assertNotNull(signature.getTxHash());

    // close the client
    aergoClient.close();
  }



  @Test
  public void testCreateRemotely() throws Exception {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .build();

    // create an account which store an encrypted key in a server
    final String password = "password";
    final ServerManagedAccount created = aergoClient.getAccountOperation().create(password);

    // query account list
    final List<AccountAddress> accountList = aergoClient.getAccountOperation().list();
    logger.info("Account list: {}", accountList);

    Optional<AccountAddress> filtered =
        accountList.stream().filter(a -> a.equals(created.getAddress())).findFirst();
    assertTrue(filtered.isPresent());

    // close the client
    aergoClient.close();
  }

  @Test
  public void testCreateRemotelyAndExport() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .build();

    // create an account which store an encrypted key in a server
    final String password = "password";
    final ServerManagedAccount created = aergoClient.getAccountOperation().create(password);

    // export encrypted key
    final EncryptedPrivateKey encryptedKey = aergoClient.getAccountOperation()
        .exportKey(Authentication.of(created.getAddress(), password));
    logger.info("Exported: {}", encryptedKey);

    assertNotNull(encryptedKey);

    // close the client
    aergoClient.close();
  }

  @Test
  public void testSignRemotely() throws Exception {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .build();

    // create an account
    final String password = "password";
    final ServerManagedAccount account = aergoClient.getAccountOperation().create(password);

    // must unlock before sign when using ServerManagedAccount
    aergoClient.getAccountOperation().unlock(Authentication.of(account.getAddress(), password));

    // make a transaction
    final Transaction transaction = new Transaction();
    transaction.setNonce(account.nextNonce());
    transaction.setAmount(1L);
    transaction.setSender(account);
    transaction.setRecipient(
        AccountAddress.of(() -> "AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg"));
    logger.info("Raw transaction: {}", transaction);

    // sign it
    final Signature signature = aergoClient.getAccountOperation().sign(account, transaction);
    logger.info("Signature: {}", signature);

    assertNotNull(signature.getSign());
    assertNotNull(signature.getTxHash());

    // close the client
    aergoClient.close();
  }

}
