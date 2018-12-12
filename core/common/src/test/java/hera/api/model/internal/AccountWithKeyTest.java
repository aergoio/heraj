/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class AccountWithKeyTest extends AbstractTestCase {

  protected final AergoKeyGenerator keyGenerator = new AergoKeyGenerator();

  @Test
  public void testGetAddress() throws Exception {
    final AergoKey aergoKey = keyGenerator.create();
    final Account account = new AccountWithKey(aergoKey);
    assertEquals(aergoKey.getAddress(), account.getAddress());
  }

}
