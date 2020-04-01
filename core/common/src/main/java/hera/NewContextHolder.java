/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public class NewContextHolder {

  protected static final ThreadLocal<NewContext> cabinet = new ThreadLocal<>();

  /**
   * Get current context.
   *
   * @return a current context
   */
  public static NewContext get() {
    final NewContext context = cabinet.get();
    return null != context ? context : NewEmptyContext.getInstance();
  }

  /**
   * Attach context to current thread scope.
   *
   * @param context a context to attach
   * @return a previous context
   */
  public static NewContext put(final NewContext context) {
    assertNotNull(context, "Context must not null");
    final NewContext current = get();
    cabinet.set(context);
    return current;
  }

}