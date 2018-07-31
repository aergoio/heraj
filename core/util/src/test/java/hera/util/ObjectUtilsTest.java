/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.ObjectUtils.equal;
import static hera.util.ObjectUtils.guid;
import static hera.util.ObjectUtils.nvl;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ObjectUtilsTest extends AbstractTestCase {

  @Test
  public void testGetInt() {
    final Object[][] testParameters =
        new Object[][] {{new byte[] {0, 0, 0, 0}, 0}, {new byte[] {127, 0, 0, 1}, 2130706433},
            {new byte[] {(byte) 192, (byte) 168, 0, 1}, -1062731775}};

    try {
      ObjectUtils.getInt(null);
    } catch (Exception e) {
      assertSame(NullPointerException.class, e.getClass());
    }

    for (final Object[] testParameter : testParameters) {
      int expected = (int) testParameter[1];
      byte[] bytes = (byte[]) testParameter[0];
      assertEquals(expected, ObjectUtils.getInt(bytes));
    }
  }

  @Test
  public void testHex() {
    final Object[][] testParameters =
        new Object[][] {{1, "00000001"}, {2, "00000002"}, {3, "00000003"}, {10, "0000000a"},
            {12, "0000000c"}, {25, "00000019"}, {50, "00000032"}, {1024, "00000400"}};

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      int value = (int) testParameter[0];
      assertEquals(expected, ObjectUtils.hex(value));
    }
  }

  @Test
  public void testGuid() throws Exception {
    for (int i = 0; i < N_TEST; ++i) {
      assertNotNull(i + " th test", guid(this));
    }
  }

  @Test
  public void testEqual() {
    final Object[][] testParameters = new Object[][] {{new String("abc"), new String("abc"), true},
        {new String("def"), new String("ghi"), false}, {150, 150L, false}};

    assertTrue(equal(null, null));
    assertFalse(equal(null, randomUUID().toString()));
    assertFalse(equal(randomUUID().toString(), null));

    for (final Object[] testParameter : testParameters) {
      boolean expected = (boolean) testParameter[2];
      Object obj1 = (Object) testParameter[0];
      Object obj2 = (Object) testParameter[1];
      assertEquals(expected, ObjectUtils.equal(obj1, obj2));
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  public void testCompare() {
    final Object[][] testParameters = new Object[][] {{"a", "a", 0}, {7, 1, 1}, {"ab", "abc", -1}};

    assertEquals(-1, ObjectUtils.compare(null, "a"));

    for (final Object[] testParameter : testParameters) {
      int expected = (int) testParameter[2];
      Comparable o1 = (Comparable) testParameter[0];
      Object o2 = (Object) testParameter[1];
      assertEquals(expected, ObjectUtils.compare(o1, o2));
    }
  }

  /**
   * Test ObjectUtils.nvl.
   *
   * @throws Exception
   *
   * @see ObjectUtils#nvl(Object, Object)
   */
  @Test
  public void testNvl() throws Exception {
    assertNull(nvl(null, null));

    for (int i = 0; i < 100; ++i) {
      assertNotNull(ObjectUtils.nvl(randomUUID().toString(), randomUUID().toString()));
    }

    for (int i = 0; i < 100; ++i) {
      assertNotNull(ObjectUtils.nvl(randomUUID().toString(), randomUUID().toString(),
          randomUUID().toString()));
    }
  }
}
