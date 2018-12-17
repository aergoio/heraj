/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import org.junit.Test;

public class AccountFactoryTest {

  @Test
  public void testCreateWithKey() {
    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key);
    assertNotNull(account);
  }

  @Test
  public void testCreateWithAddress() {
    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key.getAddress());
    assertNotNull(account);
  }

  @Test
  public void testCreateWithAddressAndSigner() {
    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key.getAddress(), mock(Signer.class));
    assertNotNull(account);
  }

}
