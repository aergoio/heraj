/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import org.junit.Test;

public class IntRangeTest extends AbstractTestCase {

  @Test
  public void testSelect() {
    final Object[][] testParameters =
        new Object[][] {{1, 5, 10, 15}, {-10, 1, 100, 101}, {-1, 0, 1, 2}, {-2, -1, 0, 1}};
    for (final Object[] testParameter : testParameters) {
      final Integer first = Integer.valueOf((int) testParameter[0]);
      final Integer second = Integer.valueOf((int) testParameter[1]);
      final Integer third = Integer.valueOf((int) testParameter[2]);
      final Integer fourth = Integer.valueOf((int) testParameter[3]);
      final IntRange intRange1 = new IntRange(first, third);
      final IntRange intRange2 = new IntRange(second, fourth);
      final IntRange join = intRange1.select(intRange2);
      logger.debug("Range: {}", join);
      assertEquals(second, join.v1);
      assertEquals(third, join.v2);
    }
  }

}
