/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import org.junit.Test;

public class PairTest {

  @Test
  public void testHashCode() {
    final Object[][] testParameters = new Object[][] {{"Hello", null, 69609650},
        {null, "Hello", 69609650}, {"Hello", "World", 153375780}, {"World", "Hello", 153375780}};

    for (final Object[] testParameter : testParameters) {
      int expected = (Integer) testParameter[2];
      String v1 = (String) testParameter[0];
      String v2 = (String) testParameter[1];
      assertEquals(expected, new Pair<String, String>(v1, v2).hashCode());
    }
  }

  @Test
  public void testEquals() {
    final Pair<String, String> pair = new Pair<String, String>("Hello", "World");
    final Object[][] testParameters = new Object[][] {{new String(), false},
        {new HashMap<String, String>(), false}, {new Pair<String, String>("Hello", "World"), true}};

    for (final Object[] testParameter : testParameters) {
      boolean expected = (Boolean) testParameter[1];
      Object obj = (Object) testParameter[0];
      assertEquals(expected, pair.equals(obj));
    }
  }

  @Test
  public void shouldBeZeroOnNull() {
    assertEquals(0, new Pair<String, String>(null, null).hashCode());
  }
}
