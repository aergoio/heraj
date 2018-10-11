/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.encode.Base58WithCheckSum;
import hera.api.model.Account;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.client.AccountEitherTemplate;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.util.Base58Utils;
import org.junit.Before;
import org.junit.Test;

public class AccountTemplateIT extends AbstractIT {

  protected AccountEitherTemplate accountTemplate = null;

  @Before
  public void setUp() {
    super.setUp();
    accountTemplate = new AccountEitherTemplate(channel, AergoClientBuilder.getDefaultContext());
  }

  @Test
  public void testCreateRemotelyAndImportLocally() throws Exception {
    final String password = randomUUID().toString();
    final Account createdAccount = accountTemplate.create(password).getResult();
    assertNotNull(createdAccount);

    assertTrue(0 < accountTemplate.list().getResult().size());

    final Account queriedAccount = accountTemplate.get(createdAccount.getAddress()).getResult();
    assertTrue(queriedAccount.getAddress().equals(createdAccount.getAddress()));

    final EncryptedPrivateKey encryptedKey = accountTemplate
        .exportKey(Authentication.of(createdAccount.getAddress(), password)).getResult();
    Base58WithCheckSum encoded =
        () -> Base58Utils.encodeWithCheck(encryptedKey.getBytesValue().getValue());
    final AergoKey imported = AergoKey.of(encoded, password);
    assertNotNull(imported.getPrivateKey());
    assertNotNull(imported.getPublicKey());
    assertNotNull(imported.getAddress());
  }

  @Test
  public void testCreateLocallyAndExportRemotely() throws Exception {
    final String password = randomUUID().toString();
    final AergoKey key = new AergoKeyGenerator().create();
    EncryptedPrivateKey encryptedKey = key.getEncryptedPrivateKey(password);
    accountTemplate.importKey(encryptedKey, password).getResult();
  }

}
