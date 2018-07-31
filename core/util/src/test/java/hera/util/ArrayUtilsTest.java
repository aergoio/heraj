/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ArrayUtilsTest {

  @Test
  public void testIsEmpty() {
    final Object[][] testParameters = new Object[][] {{new String[] {}, true},
        {new String[] {"hello, world"}, false}, {new String[] {null}, false}};

    for (final Object[] testParameter : testParameters) {
      boolean expected = (Boolean) testParameter[1];
      String[] arrays = (String[]) testParameter[0];
      assertEquals(expected, ArrayUtils.isEmpty(arrays));
    }
  }

  @Test
  public void testLength() {
    final Object[][] testParameters = new Object[][] {{new String[] {}, 0},
        {new String[] {"h", "e", "l", "l", "o"}, 5}, {new String[] {null}, 1}};

    for (final Object[] testParameter : testParameters) {
      int expected = (int) testParameter[1];
      String[] arrays = (String[]) testParameter[0];
      assertEquals(expected, ArrayUtils.length(arrays));
    }
  }

  @Test
  public void shouldBeTrueOnNull() {
    assertTrue(ArrayUtils.isEmpty(null));
  }

  @Test
  public void shouldBeZeroOnNull() {
    assertEquals(0, ArrayUtils.length(null));
  }

}
