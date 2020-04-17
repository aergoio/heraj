/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public interface Invocation<T> {

  RequestMethod<T> getRequestMethod();

  List<Object> getParameters();

  T invoke() throws Exception;

  Invocation<T> withParameters(List<Object> parameters);

}
