/*
 * @copyright defined in LICENSE.txt
 */

package hera;

public interface FailoverHandler {

  <T> void handle(Invocation<T> invocation, Response<T> response);

}
