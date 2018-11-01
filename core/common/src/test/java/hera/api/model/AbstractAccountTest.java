/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

import org.junit.Test;

public class AbstractAccountTest {

  @Test
  public void testBindState() {
    final long nonce = 3;
    final long balance = 7;

    final AbstractAccount account = spy(AbstractAccount.class);
    final AccountState state = new AccountState();
    state.setNonce(nonce);
    state.setBalance(balance);

    account.bindState(state);
    assertEquals(nonce, account.getNonce());
  }

  @Test
  public void testSetAndGetNonce() {
    final Object[][] testParameters =
        new Object[][] {{-10L, 0L}, {-1L, 0L}, {0L, 0L}, {1L, 1L}, {3L, 3L}};

    final AbstractAccount account = spy(AbstractAccount.class);
    for (Object[] testParameter : testParameters) {
      final long nonce = (long) testParameter[0];
      final long expected = (long) testParameter[1];
      account.setNonce(nonce);
      assertEquals(expected, account.getNonce());
    }
  }

  @Test
  public void testGetNextNonce() {
    final long nonce = 10;
    final AbstractAccount account = spy(AbstractAccount.class);
    account.setNonce(nonce);
    assertEquals(nonce + 1, account.nextNonce());
    assertEquals(nonce, account.getNonce());
  }

  @Test
  public void testIncrementNnoce() {
    final long nonce = 10;
    final AbstractAccount account = spy(AbstractAccount.class);
    account.setNonce(nonce);
    account.incrementNonce();
    assertEquals(nonce + 1, account.getNonce());
  }

}
