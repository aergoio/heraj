/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.AbstractTestCase;
import hera.FailoverHandler;
import hera.Invocation;
import hera.RequestMethod;
import hera.Response;
import hera.api.model.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class JustRetryFailoverHandlerTest extends AbstractTestCase {

  @Test
  public void testRetry() throws Exception {
    final AtomicInteger count = new AtomicInteger(3);
    final FailoverHandler retryStrategy =
        new JustRetryFailoverHandler(count.get(), Time.of(100L, TimeUnit.MILLISECONDS));
    final RequestMethod<Integer> requestMethod = new RequestMethod<Integer>() {

      @Override
      public String getName() {
        return null;
      }

      @Override
      protected Integer runInternal(final List<Object> parameters) throws Exception {
        if (0 < count.get()) {
          count.decrementAndGet();
          throw new IllegalStateException();
        }

        return 0;
      }

    };
    final Invocation<Integer> invocation = requestMethod.toInvocation();
    final Response<Integer> response = Response.empty();
    retryStrategy.handle(invocation, response);
  }

}
