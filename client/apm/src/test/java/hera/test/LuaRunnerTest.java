/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static hera.util.IoUtils.from;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import hera.AbstractTestCase;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.junit.Test;
import org.luaj.vm2.LuaValue;

public class LuaRunnerTest extends AbstractTestCase {
  @Test
  public void shouldNotReservePreviousScript() {
    final LuaRunner runner = new LuaRunner();
    final LuaSource luaSource = new LuaSource("a = 3\nb=3");
    runner.run(luaSource);
    assertEquals(LuaValue.NIL, runner.run(new LuaSource("return b")).getResult());
  }

  @Test
  public void shouldRunInSandBox() throws IOException {
    try (
        final InputStream in = open("lua");
        final Reader reader = new InputStreamReader(in)
    ) {
      final LuaRunner runner = new LuaRunner();
      final LuaSource luaSource = new LuaSource(from(reader));
      assertFalse(runner.run(luaSource).isSuccess());
    }
  }

}