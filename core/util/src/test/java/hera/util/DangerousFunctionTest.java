/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import java.util.function.Function;
import org.junit.Test;

public class DangerousFunctionTest extends AbstractTestCase {

  @Test
  public void testToSafe() {
    final DangerousFunction<Object, String> dangerousFunction = o -> o.toString();
    final Function<Object, String> function = dangerousFunction.toSafe();
    assertNotNull(function);
    assertNotNull(function.apply(new Object()));
  }

  @Test(expected = IllegalStateException.class)
  public void testToSafeWithException() {
    final DangerousFunction<Object, String> dangerousFunction = o -> {
      throw new UnsupportedOperationException();
    };
    final Function<Object, String> function = dangerousFunction.toSafe();
    assertNotNull(function);
    function.apply(new Object());
  }

}
