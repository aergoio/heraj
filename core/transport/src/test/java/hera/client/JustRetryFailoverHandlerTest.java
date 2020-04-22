/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.RequestMethod;
import hera.Response;
import hera.api.model.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import org.junit.Test;

public class JustRetryFailoverHandlerTest extends AbstractTestCase {

  @Test
  public void testHandle() {
    // given
    final int count = 5;
    final int stop = 2;
    final JustRetryFailoverHandler justRetryFailoverHandler = new JustRetryFailoverHandler(count,
        Time.of(100L, TimeUnit.MILLISECONDS));
    final AtomicInteger countDown = new AtomicInteger(count);
    final String expected = randomUUID().toString();
    final RequestMethod<Object> requestMethod = new RequestMethod<Object>() {

      @Getter
      protected final String name = randomUUID().toString();

      @Override
      protected Object runInternal(List<Object> parameters) throws Exception {
        countDown.decrementAndGet();
        if (stop == countDown.get()) {
          return expected;
        }

        throw new UnsupportedOperationException();
      }
    };

    // then
    final Response<Object> response = Response.fail(new UnsupportedOperationException());
    final Response<Object> handled = justRetryFailoverHandler
        .handle(new TestInvocation<>(requestMethod), response);
    assertEquals(expected, handled.getValue());
    assertEquals(stop, countDown.get());
  }

  @Test
  public void shouldNotRunOnSuccess() {
    // given
    final int count = 3;
    final JustRetryFailoverHandler justRetryFailoverHandler = new JustRetryFailoverHandler(count,
        Time.of(100L, TimeUnit.MILLISECONDS));
    final AtomicInteger countDown = new AtomicInteger(count);
    final RequestMethod<String> requestMethod = new RequestMethod<String>() {

      @Getter
      protected final String name = randomUUID().toString();

      @Override
      protected String runInternal(List<Object> parameters) throws Exception {
        countDown.decrementAndGet();
        return null;
      }
    };

    // then
    final String expected = randomUUID().toString();
    final Response<String> response = Response.success(expected);
    final Response<String> handled = justRetryFailoverHandler
        .handle(new TestInvocation<>(requestMethod), response);
    assertEquals(expected, handled.getValue());
    assertEquals(count, countDown.get());
  }

  @Test
  public void shouldKeepErrorOnNoSuccess() {
    // given
    final int count = 3;
    final JustRetryFailoverHandler justRetryFailoverHandler = new JustRetryFailoverHandler(count,
        Time.of(100L, TimeUnit.MILLISECONDS));
    final AtomicInteger countDown = new AtomicInteger(count);
    final RequestMethod<Object> requestMethod = new RequestMethod<Object>() {

      @Getter
      protected final String name = randomUUID().toString();

      @Override
      protected Object runInternal(List<Object> parameters) throws Exception {
        countDown.decrementAndGet();
        throw new UnsupportedOperationException();
      }
    };

    // then
    final Response<Object> response = Response.fail(new UnsupportedOperationException());
    final Response<Object> handled = justRetryFailoverHandler
        .handle(new TestInvocation<>(requestMethod), response);
    assertNotNull(handled.getError());
    assertEquals(0, countDown.get());
  }

}
