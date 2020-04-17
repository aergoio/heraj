/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Private
@ApiStability.Unstable
public class WriteSynchronizedContextStorage<ContextT extends Context> implements
    ContextStorage<ContextT> {

  protected final Object lock = new Object();

  protected ContextT current;

  @Override
  public ContextT get() {
    return current;
  }

  @Override
  public ContextT put(final ContextT context) {
    synchronized (lock) {
      final ContextT stored = current;
      current = context;
      return stored;
    }
  }

}
