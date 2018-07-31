/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.ValidationUtils.assertEquals;
import static hera.util.ValidationUtils.assertFalse;
import static hera.util.ValidationUtils.assertNotEquals;
import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertNull;
import static hera.util.ValidationUtils.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ValidationUtilsTest extends AbstractTestCase {

  @Test
  public void testAssertEquals() {
    assertEquals(null, null);
    assertEquals("Hello", "Hello");

    try {
      assertEquals("Hello", "Hello1");
      fail();
    } catch (final AssertionError e) {
      // good we expected this
    }
    try {
      assertEquals(null, "Hello1");
      fail();
    } catch (final AssertionError e) {
      // good we expected this
    }
    try {
      assertEquals("Hello", null);
      fail();
    } catch (AssertionError e) {
      // good we expected this
    }
  }


  @Test
  public void testAssertNotEquals() {
    assertNotEquals(null, "Hello");
    assertNotEquals("Hello", null);
    assertNotEquals("Hello", "Hello1");

    try {
      assertNotEquals(null, null);
      fail();
    } catch (final AssertionError e) {
      // good we expected this
    }
    
    try {
      assertNotEquals("Hello", "Hello");
      fail();
    } catch (final AssertionError e) {
      // good we expected this
    }
  }

  @Test
  public void testAssertTrue() {
    assertTrue(true);

    try {
      assertTrue(false);
      fail();
    } catch (final AssertionError e) {
      // good we expected this
    }
  }

  @Test
  public void testAssertFalse() {
    assertFalse(false);

    try {
      assertFalse(true);
      fail();
    } catch (final AssertionError e) {
      // good we expected this
    }
  }

  @Test
  public void testAssertNull() {
    assertNull(null);

    try {
      assertNull("Hello");
      fail();
    } catch (final AssertionError e) {
      // good we expected this
    }
  }

  @Test
  public void testAssertNotNull() {
    assertNotNull("Hello");

    try {
      assertNotNull(null);
      fail();
    } catch (final AssertionError e) {
      // good we expected this
    }
  }
}
