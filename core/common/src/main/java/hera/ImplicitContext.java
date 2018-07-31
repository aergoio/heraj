/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import java.util.function.Consumer;

/**
 * Holder for default context in heraj.
 */
public class ImplicitContext {

  public static Context context = new Context();

  /**
   * Configure default context.
   * <p>
   *   old context is replaced.
   * </p>
   * @param configurer object to configure context
   */
  public static void configure(Consumer<Context> configurer) {
    final Context context = new Context();
    configurer.accept(context);
    ImplicitContext.context = context;
  }

  /**
   * Return default context.
   *
   * @return default context
   */
  public static Context get() {
    return context;
  }
}
