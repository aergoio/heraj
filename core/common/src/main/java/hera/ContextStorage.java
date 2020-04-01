package hera;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface ContextStorage<ContextT> {

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
