/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Private
@ApiStability.Unstable
public interface ContextProvider {

  ContextProvider defaultProvider = new ContextProvider() {
    @Override
    public Context get() {
      return EmptyContext.getInstance();
    }
  };

  Context get();

}
