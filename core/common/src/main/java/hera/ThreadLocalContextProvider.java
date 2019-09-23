/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class ThreadLocalContextProvider implements ContextProvider {

  protected final Logger logger = getLogger(getClass());

  protected final Object cabinetKey;

  protected Context baseContext;

  public ThreadLocalContextProvider(final Context baseContext, final Object cabinetKey) {
    this.baseContext = baseContext;
    this.cabinetKey = cabinetKey;
  }

  @Override
  public Context get() {
    final Context context = ContextHolder.get(cabinetKey);
    if (context.equals(EmptyContext.getInstance())) {
      logger.debug("Context in current thread is empty. Set context from base: {}",
          this.baseContext);
      ContextHolder.set(cabinetKey, this.baseContext);
    }
    return ContextHolder.get(cabinetKey);
  }

  @Override
  public void put(final Context context) {
    this.baseContext = context;
    ContextHolder.set(cabinetKey, context);
  }

}
