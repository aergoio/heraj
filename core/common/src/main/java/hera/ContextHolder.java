/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.lang.System.identityHashCode;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.HashMap;
import java.util.Map;

@ApiAudience.Public
@ApiStability.Unstable
public class ContextHolder {

  protected static final ThreadLocal<Map<Integer, Context>> threadLocal =
      new ThreadLocal<Map<Integer, Context>>() {
        @Override
        public Map<Integer, Context> initialValue() {
          return new HashMap<Integer, Context>();
        }
      };

  public static void set(final Object keyObject, final Context context) {
    threadLocal.get().put(identityHashCode(keyObject), context);
  }

  /**
   * Get context of {@code keyObject}.
   *
   * @param keyObject a context key
   * @return a context
   */
  public static Context get(final Object keyObject) {
    final int key = identityHashCode(keyObject);
    final Context context = threadLocal.get().get(key);
    return null != context ? context : EmptyContext.getInstance();
  }

}
