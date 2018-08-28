/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static org.junit.Assert.assertNull;

import hera.AbstractTestCase;
import org.junit.Test;

public class LuaRunnerTest extends AbstractTestCase {
  @Test
  public void shouldNotReservePreviousScript() {
    final LuaRunner runner = new LuaRunner();
    final LuaSource luaSource = new LuaSource("a = 3\nb=3");
    runner.run(luaSource);
    assertNull(runner.run(new LuaSource("return b")).getResult());
  }
}