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
          return new HashMap<>();
        }
      };

  public static void set(final Object keyObject, final Context context) {
    threadLocal.get().put(identityHashCode(keyObject), context);
  }

  public static Context get(final Object keyObject) {
    return threadLocal.get().getOrDefault(identityHashCode(keyObject),
        EmptyContext.getInstance());
  }

}
