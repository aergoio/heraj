/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ThreadLocalContextProviderTest extends AbstractTestCase {

  @Test
  public void testGetWithoutPreScope() throws Exception {
    final String scope = randomUUID().toString();
    final ContextProvider provider =
        new ThreadLocalContextProvider(EmptyContext.getInstance().withScope(scope), this);
    assertEquals(scope, provider.get().getScope());
  }

  @Test
  public void testGetWithPreScope() throws Exception {
    final String originScope = randomUUID().toString();
    ContextHolder.set(this, EmptyContext.getInstance().withScope(originScope));
    final ContextProvider provider = new ThreadLocalContextProvider(null, this);
    assertEquals(originScope, provider.get().getScope());
  }

}
