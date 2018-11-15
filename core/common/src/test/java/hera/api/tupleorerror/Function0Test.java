/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import org.junit.Test;

public class Function0Test extends AbstractTestCase {

  @Test
  public void testAndThen() {
    final String ret = randomUUID().toString();
    Function0<String> f = () -> ret;
    Function0<Integer> composed = f.andThen(s -> s.length());

    final Integer expected = ret.length();
    assertEquals(expected, composed.apply());
  }

}
