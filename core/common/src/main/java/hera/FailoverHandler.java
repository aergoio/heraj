/*
 * @copyright defined in LICENSE.txt
 */

package hera;

public interface FailoverHandler {

  <T> void handle(Requester requester, Response<T> response,
      FailoverHandlerChain failoverHandlerChain);

}
