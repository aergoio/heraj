/*
 * @copyright defined in LICENSE.txt
 */

package hera;

public interface FailoverHandlerChain {

  <T> void handle(Invocation<T> invocation, Response<T> response);

}
