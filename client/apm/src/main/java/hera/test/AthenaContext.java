/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static org.slf4j.LoggerFactory.getLogger;

import lombok.Getter;
import lombok.Setter;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.slf4j.Logger;

public class AthenaContext {

  protected static final ThreadLocal<AthenaContext> cabinet =
      new InheritableThreadLocal<AthenaContext>() {
        @Override
        protected AthenaContext initialValue() {
          return new AthenaContext();
        }
      };

  public static AthenaContext getContext() {
    return cabinet.get();
  }

  protected final transient Logger logger = getLogger(getClass());

  @Getter
  @Setter
  protected TestResultCollector testReporter = new TestResultCollector();

  public OneArgFunction startSuite = new OneArgFunction() {
    @Override
    public LuaValue call(final LuaValue name) {
      testReporter.startSuite(name.tojstring());
      return null;
    }
  };

  public OneArgFunction endSuite = new OneArgFunction() {
    @Override
    public LuaValue call(final LuaValue name) {
      testReporter.endSuite(name.tojstring());
      return null;
    }
  };

  public OneArgFunction startTest = new OneArgFunction() {
    @Override
    public LuaValue call(final LuaValue name) {
      logger.trace("Starting {}...", name);
      testReporter.start(name.tojstring());
      return null;
    }
  };
  public TwoArgFunction endTest = new TwoArgFunction() {
    @Override
    public LuaValue call(final LuaValue name, LuaValue testResult) {
      logger.trace("{} end", name);
      testReporter.end(name.tojstring(), testResult.toboolean());
      return null;
    }
  };
  public TwoArgFunction recordError = new TwoArgFunction() {
    @Override
    public LuaValue call(LuaValue name, LuaValue error) {
      logger.info("{} throw {}", name, error);
      testReporter.error(name.tojstring(), error.tojstring());
      return null;
    }
  };

  public static void clear() {
    cabinet.remove();
  }
}
