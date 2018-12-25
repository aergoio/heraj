/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Private
@ApiStability.Unstable
@FunctionalInterface
public interface ContextProvider {

  ContextProvider defaultProvider = () -> EmptyContext.getInstance();

  Context get();

}
