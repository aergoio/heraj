/*
 * @copyright defined in LICENSE.txt
 */

package hera;

public final class ContextHolder {

  public static ThreadLocal<Context> threadLocal = ThreadLocal.withInitial(() -> new ContextConc());

  public static void set(final Context context) {
    threadLocal.set(context);
  }

  public static Context get() {
    return threadLocal.get();
  }

}
