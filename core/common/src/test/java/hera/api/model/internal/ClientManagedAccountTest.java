/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class ClientManagedAccountTest extends AbstractTestCase {

  protected final AergoKeyGenerator keyGenerator = new AergoKeyGenerator();

  @Test
  public void testGetAddress() throws Exception {
    final AergoKey aergoKey = keyGenerator.create();
    final ClientManagedAccount account = ClientManagedAccount.of(aergoKey);
    assertEquals(aergoKey.getAddress(), account.getAddress());
  }

  @Test
  public void testAdapt() throws Exception {
    final AergoKey aergoKey = keyGenerator.create();
    final ClientManagedAccount account = ClientManagedAccount.of(aergoKey);
    assertEquals(account, account.adapt(Account.class).get());
    assertEquals(aergoKey.getAddress(), account.adapt(AccountAddress.class).get());
  }

}
