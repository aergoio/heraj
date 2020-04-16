/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public class ContextHolder {

  protected static final ThreadLocal<Context> cabinet = new ThreadLocal<>();

  /**
   * Get current context.
   *
   * @return a current context
   */
  public static Context current() {
    final Context context = cabinet.get();
    return null != context ? context : EmptyContext.getInstance();
  }

  /**
   * Attach context to current thread scope.
   *
   * @param context a context to attach
   * @return a previous context
   */
  public static Context attach(final Context context) {
    assertNotNull(context, "Context must not null");
    final Context current = current();
    cabinet.set(context);
    return current;
  }

  /**
   * Remove current context.
   *
   * @return stored context
   */
  public static Context remove() {
    final Context current = current();
    cabinet.remove();
    return current;
  }

  private ContextHolder() {

  }

}