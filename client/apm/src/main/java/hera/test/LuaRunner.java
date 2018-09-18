/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static org.luaj.vm2.lib.jse.JsePlatform.standardGlobals;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Arrays;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JsePlatform;
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
    final StringWriter stringWriter = new StringWriter();
    final TestResult testResult = new TestResult();
    try {
      logger.trace("Lua Script:\n{}", source);
      final ScriptEngineManager mgr = new ScriptEngineManager();
      final ScriptEngine engine = mgr.getEngineByName("lua");

      final Globals userGlobals = new Globals();
      userGlobals.load(new JseBaseLib());
      userGlobals.load(new PackageLib());
      userGlobals.load(new Bit32Lib());
      userGlobals.load(new TableLib());
      userGlobals.load(new StringLib());
      userGlobals.load(new JseMathLib());
      userGlobals.load(new LuaDebug());

      engine.getContext().setWriter(stringWriter);
      //final Object result = userGlobals.load(source.getScript()).call();
      final Object result = standardGlobals().load(source.getScript(), "main", userGlobals).call();

      //final Object result = engine.eval(source.getScript());
      logger.debug("Result: {}", result);
      testResult.setResult(result);
      return testResult;
      /*
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
      } catch (final ParseException e1) {
        final String message = e.getMessage();
        final int lineNumber = e.getLineNumber();
        final int columnNumber = e.getColumnNumber();
        errorInformation = new LuaErrorInformation(message, lineNumber, columnNumber);
      }
      return apply(testResult, source, errorInformation);
      */
    } catch (final LuaError e) {
      logger.trace("=== LuaError ===", e);
      final LuaErrorInformation errorInformation = new LuaErrorInformation(e.getMessage());
      return apply(testResult, source, errorInformation);
    } finally {
      final String output = stringWriter.toString();
      logger.debug("Output: {}", output);
      testResult.setOutput(output);
    }
  }

  protected TestResult apply(
      final TestResult testResult,
      final LuaSource source,
      final LuaErrorInformation errorInformation) {
    logger.debug("Error: {}", errorInformation);
    final int lineNumber = errorInformation.getLineNumber();
    testResult.setError(errorInformation);
    if (0 < lineNumber) {
      final String codeSnippet =
          source.toString(lineNumber - 10, lineNumber + 10, Arrays.asList(lineNumber));
      testResult.setCodeSnippet(codeSnippet);
    }

    return testResult;
  }
}
