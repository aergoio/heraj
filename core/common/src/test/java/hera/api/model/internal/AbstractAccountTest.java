/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import org.junit.Test;

public class AbstractAccountTest {

  @Test
  public void testBindState() {
    final long nonce = 3;
    final Aer balance = Aer.ONE;

    final AbstractAccount account = spy(AbstractAccount.class);
    final AccountState state = AccountState.newBuilder()
        .address(AccountAddress.of(BytesValue.EMPTY))
        .nonce(nonce)
        .balance(balance)
        .build();

    account.bindState(state);
    assertEquals(nonce, account.getRecentlyUsedNonce());
  }

  @Test
  public void testSetAndGetNonce() {
    final Object[][] testParameters =
        new Object[][] {{-10L, 0L}, {-1L, 0L}, {0L, 0L}, {1L, 1L}, {3L, 3L}};

    final AbstractAccount account = spy(AbstractAccount.class);
    for (Object[] testParameter : testParameters) {
      final long nonce = (Long) testParameter[0];
      final long expected = (Long) testParameter[1];
      account.setNonce(nonce);
      assertEquals(expected, account.getRecentlyUsedNonce());
    }
  }

  @Test
  public void testIncrementAndGetNonce() {
    final long nonce = 10;
    final AbstractAccount account = spy(AbstractAccount.class);
    account.setNonce(nonce);
    final long expected = nonce + 1;
    assertEquals(expected, account.incrementAndGetNonce());
    assertEquals(expected, account.getRecentlyUsedNonce());
  }

}
