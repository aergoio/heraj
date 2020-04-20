/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.Invocation;
import hera.Response;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.junit.Test;

public class FailoverHandlerChainTest extends AbstractTestCase {

  @Test
  public void testCreate() {
    // given
    final ComparableFailoverHandler p1 = new ComparableFailoverHandler() {
      @Getter
      protected final int priority = 1;

      @Override
      public <T> void handle(Invocation<T> invocation, Response<T> response) {

      }
    };
    final ComparableFailoverHandler p2 = new ComparableFailoverHandler() {
      @Getter
      protected final int priority = 2;

      @Override
      public <T> void handle(Invocation<T> invocation, Response<T> response) {

      }
    };
    final ComparableFailoverHandler p3 = new ComparableFailoverHandler() {
      @Getter
      protected final int priority = 3;

      @Override
      public <T> void handle(Invocation<T> invocation, Response<T> response) {

      }
    };
    final List<ComparableFailoverHandler> failoverHandlers = asList(p2, p3, p1);

    // then
    final FailoverHandlerChain failoverHandlerChain = new FailoverHandlerChain(failoverHandlers);
    final List<ComparableFailoverHandler> actual = failoverHandlerChain.failoverHandlers;
    final List<ComparableFailoverHandler> expected = asList(p1, p2, p3);
    assertEquals(expected, actual);
  }

  @Test
  public void testHandle() {
    // given
    final Set<Object> usedSet = new HashSet<>();
    final Object expected = randomUUID().toString();
    final ComparableFailoverHandler p1 = new ComparableFailoverHandler() {
      @Getter
      protected final int priority = 1;

      @Override
      public <T> void handle(Invocation<T> invocation, Response<T> response) {
        usedSet.add(this);
      }
    };
    final ComparableFailoverHandler p2 = new ComparableFailoverHandler() {
      @Getter
      protected final int priority = 2;

      @SuppressWarnings("unchecked")
      @Override
      public <T> void handle(Invocation<T> invocation, Response<T> response) {
        usedSet.add(this);
        response.success((T) expected);
      }
    };
    final ComparableFailoverHandler p3 = new ComparableFailoverHandler() {
      @Getter
      protected final int priority = 3;

      @Override
      public <T> void handle(Invocation<T> invocation, Response<T> response) {
        fail("Should not run");
      }
    };
    final List<ComparableFailoverHandler> failoverHandlers = asList(p2, p3, p1);

    // then
    final FailoverHandlerChain failoverHandlerChain = new FailoverHandlerChain(failoverHandlers);
    final Response<Object> response = Response.empty();
    response.fail(new UnsupportedOperationException());
    failoverHandlerChain.handle(null, response);
    assertEquals(expected, response.getValue());
    assertEquals(2, usedSet.size());
  }

}
