/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static hera.test.TestResult.fail;
import static hera.test.TestResult.success;
import static org.slf4j.LoggerFactory.getLogger;

import hera.util.SourcePrinter;
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
   * @param script script to run
   *
   * @return result to be run
   */
  public TestResult run(final String script) {
    try {
      logger.trace("Lua Script:\n{}", new SourcePrinter().apply(script));
      final ScriptEngineManager mgr = new ScriptEngineManager();
      final ScriptEngine engine = mgr.getEngineByName("lua");
      final Object result = engine.eval(script);
      logger.debug("Result: {}", result);
      return success(result);
    } catch (final ScriptException e) {
      final String message = e.getMessage();
      final int lineNumber = e.getLineNumber();
      final int columnNumber = e.getColumnNumber();
      logger.debug("{}:{} - Message: {}", lineNumber, columnNumber, message);
      return fail(message, lineNumber, columnNumber);
    } catch (final LuaError e) {
      final String info = e.getMessage();
      final int messageIndex = info.indexOf(" ");
      final String message = info.substring(messageIndex + 1);
      final int colonIndex = info.indexOf(":");
      final String lineNumberStr = info.substring(colonIndex + 1, messageIndex).trim();
      final int lineNumber = Integer.parseInt(lineNumberStr);
      final int columnNumber = -1;
      logger.debug("{}:{} - Message: {}", lineNumber, columnNumber, info);
      return fail(message, lineNumber, columnNumber);
    }
  }
}
