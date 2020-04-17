package hera;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Private
@ApiStability.Unstable
public interface ContextStorage<ContextT extends Context> {

  /**
   * Get current context value.
   *
   * @return a current context
   */
  ContextT get();

  /**
   * Put context to storage.
   *
   * @param context a context
   * @return a pre-context
   */
  ContextT put(ContextT context);

}
