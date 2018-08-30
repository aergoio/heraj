/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static hera.test.TestResult.fail;
import static hera.test.TestResult.success;
import static org.slf4j.LoggerFactory.getLogger;

import java.text.MessageFormat;
import java.text.ParseException;
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
      logger.trace("=== ScriptException ===", e);
      final String rawMessage = e.getMessage();
      final String pattern =
          "eval threw javax.script.ScriptException: [string \"script\"]:{0}: {1}";
      final MessageFormat messageFormat = new MessageFormat(pattern);
      LuaErrorInformation errorInformation = null;
      try {
        final Object[] args = messageFormat.parse(rawMessage);
        final String rowStr = (String) args[0];
        final String message = (String) args[1];
        final int lineNumber = Integer.parseInt(rowStr);
        errorInformation = new LuaErrorInformation(message, lineNumber, -1);
      } catch (ParseException e1) {
        final String message = e.getMessage();
        final int lineNumber = e.getLineNumber();
        final int columnNumber = e.getColumnNumber();
        errorInformation = new LuaErrorInformation(message, lineNumber, columnNumber);
      }
      return apply(source, errorInformation);
    } catch (final LuaError e) {
      logger.trace("=== LuaError ===", e);
      final LuaErrorInformation errorInformation = new LuaErrorInformation(e.getMessage());
      return apply(source, errorInformation);

    }
  }

  protected TestResult apply(LuaSource source, LuaErrorInformation errorInformation) {
    logger.debug("Error: {}", errorInformation);
    final int lineNumber = errorInformation.getLineNumber();
    if (lineNumber < 0) {
      return fail(errorInformation, null);
    } else {
      return fail(errorInformation, source.toString(lineNumber - 10, lineNumber + 10));
    }
  }
}
