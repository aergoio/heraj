/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.io.File;
import org.junit.Test;

public class OptionalsTest {
  
  @Test
  public void testCastIf() throws ClassNotFoundException {
    final Object[][] testParameters = new Object[][] {{Byte.class, (byte) 127, true},
        {Short.class, (short) 256, true}, {String.class, "hello, world", true},
        {Integer.class, 512L, false}, {Long.class, new File("hello"), false},
        {StringBuilder.class, new String("world"), false}};

    try {
      Optionals.castIf(null).apply(null).isPresent();
    } catch (Exception e) {
      assertSame(NullPointerException.class, e.getClass());
    }
    
    assertFalse(Optionals.castIf(Byte.class).apply(null).isPresent());
    
    try {
      Optionals.castIf(null).apply(127).isPresent();
    } catch (Exception e) {
      assertSame(NullPointerException.class, e.getClass());
    }

    for (final Object[] testParameter : testParameters) {
      boolean expected = (boolean) testParameter[2];
      Class<?> target = (Class<?>) testParameter[0];
      Object t = Class.forName(testParameter[1].getClass().getName()).cast(testParameter[1]);
      assertEquals(expected, Optionals.castIf(target).apply(t).isPresent());
    }
  }
}
