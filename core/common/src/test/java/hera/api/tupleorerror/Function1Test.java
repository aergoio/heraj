/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import org.junit.Test;

public class Function1Test extends AbstractTestCase {

  @Test
  public void testAndThen() {
    Function1<String, String> f = t -> t;
    Function1<String, Integer> composed = f.andThen(s -> s.length());
    final String arg = randomUUID().toString();
    assertEquals(arg.length(), composed.apply(arg).intValue());
  }

}
