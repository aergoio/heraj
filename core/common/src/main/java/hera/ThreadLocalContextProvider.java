/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class ThreadLocalContextProvider implements ContextProvider {

  protected final Logger logger = getLogger(getClass());

  protected final Context baseContext;

  protected final Object cabinetKey;

  @Override
  public Context get() {
    final Context context = ContextHolder.get(cabinetKey);
    if (context.equals(EmptyContext.getInstance())) {
      logger.debug("Context in current thread is empty. Set context from base: {}",
          baseContext);
      final Context candidate = baseContext instanceof ThreadLocalContext
          ? ((ThreadLocalContext) baseContext).getCabinetContext()
          : baseContext;
      ContextHolder.set(cabinetKey, candidate);
    }
    return new ThreadLocalContext(cabinetKey);
  }

}