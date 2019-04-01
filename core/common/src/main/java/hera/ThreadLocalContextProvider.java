/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.RequiredArgsConstructor;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class ThreadLocalContextProvider implements ContextProvider {

  protected final Context parentContext;

  protected final Object cabinetKey;

  @Override
  public Context get() {
    final Context context = ContextHolder.get(cabinetKey);
    if (context.equals(EmptyContext.getInstance())) {
      ContextHolder.set(cabinetKey, parentContext);
    }
    return new ThreadLocalContext(cabinetKey);
  }

}
