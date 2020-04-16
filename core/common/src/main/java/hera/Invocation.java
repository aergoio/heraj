/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import java.util.List;

public interface Invocation<T> {

  RequestMethod<T> getRequestMethod();

  List<Object> getParameters();

  T invoke() throws Exception;

  Invocation<T> withParameters(List<Object> parameters);

}
