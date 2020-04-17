/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Private
@ApiStability.Unstable
public interface Requester {

  <T> T request(Invocation<T> invocation) throws Exception;

}
