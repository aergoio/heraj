/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import org.junit.Test;

public class Function4Test extends AbstractTestCase {

  @Test
  public void testAndThen() {
    Function4<String, String, String, String, String> f = (t1, t2, t3, t4) -> t1 + t2 + t3 + t4;
    Function4<String, String, String, String, Integer> composed = f.andThen(s -> s.length());
    final String arg0 = randomUUID().toString();
    final String arg1 = randomUUID().toString();
    final String arg2 = randomUUID().toString();
    final String arg3 = randomUUID().toString();
    assertEquals((arg0 + arg1 + arg2 + arg3).length(),
        composed.apply(arg0, arg1, arg2, arg3).intValue());
  }

}
