/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.model.Account;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.util.HexUtils;
import org.junit.Before;
import org.junit.Test;

public class AccountTemplateIT extends AbstractIT {

  protected AccountEitherTemplate accountTemplate = null;

  @Before
  public void setUp() {
    super.setUp();
    accountTemplate = new AccountEitherTemplate(channel);
  }

  @Test
  public void testCreateAndExport() {
    final String password = randomUUID().toString();
    final Account createdAccount = accountTemplate.create(password).getResult();
    assertNotNull(createdAccount);

    assertTrue(0 < accountTemplate.list().getResult().size());

    final Account queriedAccount = accountTemplate.get(createdAccount.getAddress()).getResult();
    assertTrue(queriedAccount.getAddress().equals(createdAccount.getAddress()));

    final EncryptedPrivateKey encryptedKey = accountTemplate
        .exportKey(Authentication.of(createdAccount.getAddress(), password)).getResult();
  }
}
