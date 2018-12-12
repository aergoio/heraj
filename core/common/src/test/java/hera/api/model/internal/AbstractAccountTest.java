/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.BytesValue;
import java.math.BigInteger;
import org.junit.Test;

public class AbstractAccountTest {

  @Test
  public void testBindState() {
    final long nonce = 3;
    final BigInteger balance = BigInteger.valueOf(7L);

    final AbstractAccount account = spy(AbstractAccount.class);
    final AccountState state =
        new AccountState(AccountAddress.of(BytesValue.EMPTY), nonce, balance);

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
  public void testIncrementAndGetNonce() {
    final long nonce = 10;
    final AbstractAccount account = spy(AbstractAccount.class);
    account.setNonce(nonce);
    final long expected = nonce + 1;
    assertEquals(expected, account.incrementAndGetNonce());
    assertEquals(expected, account.getNonce());
  }

}
