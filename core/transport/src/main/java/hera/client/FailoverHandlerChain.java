/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.ValidationUtils.assertNotNull;

import hera.FailoverHandler;
import hera.Invocation;
import hera.Response;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class FailoverHandlerChain implements FailoverHandler {

  protected final List<ComparableFailoverHandler> failoverHandlers;

  FailoverHandlerChain(final List<ComparableFailoverHandler> failoverHandlers) {
    assertNotNull(failoverHandlers);
    final List<ComparableFailoverHandler> temp = new LinkedList<>(failoverHandlers);
    Collections.sort(temp);
    this.failoverHandlers = Collections.unmodifiableList(temp);
  }

  @Override
  public <T> void handle(final Invocation<T> invocation, final Response<T> response) {
    for (final FailoverHandler failoverHandler : failoverHandlers) {
      if (null == response.getError()) {
        return;
      }

      failoverHandler.handle(invocation, response);
    }
  }

}
