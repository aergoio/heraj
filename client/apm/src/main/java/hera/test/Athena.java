package hera.test;/*
 * @copyright defined in LICENSE.txt
 */

import static org.slf4j.LoggerFactory.getLogger;

import hera.test.AthenaContext;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.slf4j.Logger;

public class Athena extends TwoArgFunction {

  protected final transient Logger logger = getLogger(getClass());

  @Override
  public LuaValue call(final LuaValue modname, final LuaValue env) {
    logger.debug("Loading {}...", modname);
    LuaValue library = tableOf();
    final AthenaContext context = AthenaContext.getContext();

    library.set("startSuite", context.startSuite);
    library.set("endSuite", context.endSuite);
    library.set("startTest", context.startTest);
    library.set("endTest", context.endTest);
    library.set("recordError", context.recordError);
    env.set("TestReporter", library);
    return library;
  }
}
