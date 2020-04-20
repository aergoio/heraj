/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.FailoverHandler;
import hera.Invocation;
import hera.Response;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;

class FailoverHandlerChain implements FailoverHandler {

  protected final transient Logger logger = getLogger(getClass());

  protected final List<ComparableFailoverHandler> failoverHandlers;

  FailoverHandlerChain(final List<ComparableFailoverHandler> failoverHandlers) {
    assertNotNull(failoverHandlers, "FailoverHandlers must not null");
    logger.trace("Failover handlers without order: {}", failoverHandlers);
    final List<ComparableFailoverHandler> sorted = new LinkedList<>(failoverHandlers);
    Collections.sort(sorted);
    logger.trace("Sorted failover handlers : {}", sorted);
    this.failoverHandlers = Collections.unmodifiableList(sorted);
  }

  @Override
  public <T> void handle(final Invocation<T> invocation, final Response<T> response) {
    logger.debug("Handle {} with failover handler chain (handlers: {})", response.getError(),
        this.failoverHandlers);
    for (final FailoverHandler failoverHandler : this.failoverHandlers) {
      if (null == response.getError()) {
        return;
      }

      failoverHandler.handle(invocation, response);
    }
  }

}
