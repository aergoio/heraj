/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static types.AergoRPCServiceGrpc.newBlockingStub;

import hera.api.model.Account;
import org.junit.Before;
import org.junit.Test;

public class AccountTemplateIT extends AbstractIT {

  protected AccountTemplate accountTemplate = null;

  @Before
  public void setUp() {
    super.setUp();
    accountTemplate = new AccountTemplate(newBlockingStub(channel));
  }

  @Test
  public void testCreate() {
    final String password = randomUUID().toString();
    final Account account = accountTemplate.create(password);
    assertNotNull(account);
    assertTrue(0 < accountTemplate.list().size());
  }
}
