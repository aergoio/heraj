/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ThreadUtilsTest {
  @Test
  public void testTrySleep() {
    long currentTimeMillis = System.currentTimeMillis();
    ThreadUtils.trySleep(3000);
    assertEquals(currentTimeMillis  + 3000, System.currentTimeMillis(), 0);
  }
}


