/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static hera.test.TestResult.fail;
import static hera.test.TestResult.success;
import static org.slf4j.LoggerFactory.getLogger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.luaj.vm2.LuaError;
import org.slf4j.Logger;

public class LuaRunner {

  protected final transient Logger logger = getLogger(getClass());

  /**
   * Execute lua script.
   *
   * @param source lua script to run
   *
   * @return result to be run
   */
  public TestResult run(final LuaSource source) {
    try {
      logger.trace("Lua Script:\n{}", source);
      final ScriptEngineManager mgr = new ScriptEngineManager();
      final ScriptEngine engine = mgr.getEngineByName("lua");
      final Object result = engine.eval(source.getScript());
      logger.debug("Result: {}", result);
      return success(result);
    } catch (final ScriptException e) {
      final String message = e.getMessage();
      final int lineNumber = e.getLineNumber();
      final int columnNumber = e.getColumnNumber();
      final LuaErrorInformation errorInformation =
          new LuaErrorInformation(message, lineNumber, columnNumber);

      logger.debug("Error: {}", errorInformation);
      return fail(errorInformation);
    } catch (final LuaError e) {
      LuaErrorInformation errorInformation = new LuaErrorInformation(e.getMessage());
      logger.debug("Error: {}", errorInformation);
      return fail(errorInformation);
    }
  }
}
