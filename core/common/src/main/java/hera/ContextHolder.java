/*
 * @copyright defined in LICENSE.txt
 */

package hera;

public class ContextHolder {

  protected static final ThreadLocal<Context> threadLocal =
      ThreadLocal.withInitial(() -> EmptyContext.getInstance());

  public static Context newContext() {
    return EmptyContext.getInstance();
  }

  public static void set(final Context context) {
    threadLocal.set(context);
  }

  public static Context get() {
    return threadLocal.get();
  }

}
