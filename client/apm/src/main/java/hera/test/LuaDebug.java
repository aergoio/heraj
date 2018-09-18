package hera.test;

import static org.slf4j.LoggerFactory.getLogger;

import hera.exception.LuaStackOverflowException;
import java.util.Stack;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.DebugLib;
import org.slf4j.Logger;

public class LuaDebug extends DebugLib {

  protected final transient Logger logger = getLogger(getClass());

  protected Stack<LuaClosure> callStack = new Stack<>();

  @Override
  public void onCall(LuaFunction f) {
    logger.debug("Function: {}", f);
    super.onCall(f);
  }

  @Override
  public void onCall(LuaClosure c, Varargs varargs, LuaValue[] stack) {
    logger.trace("Stack size: {}", callStack.size());
    if (128 < callStack.size()) {
      throw new LuaStackOverflowException();
    }
    logger.debug("Closure: {}, Arguments: {}, Stack: {}", c, varargs, stack);
    super.onCall(c, varargs, stack);
    callStack.push(c);
    logger.debug("END of Call");
  }

  @Override
  public Varargs onInvoke(Varargs args) {
    logger.debug("Arguments: {}", args);
    return super.onInvoke(args);
  }

  @Override
  public void onReturn() {
    callStack.pop();
    super.onReturn();
  }

  @Override
  public void onInstruction(int pc, Varargs v, int top) {
    logger.debug("PROGRAM COUNTER: {}, Arguments: {}, Top: {}", pc, v, top);
    super.onInstruction(pc, v, top);
  }
}
