/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_AFTER_FAILURE;
import static hera.client.ClientContextKeys.GRPC_AFTER_SUCCESS;
import static hera.client.ClientContextKeys.GRPC_BEFORE_REQUEST;
import static hera.client.ClientContextKeys.GRPC_FAILOVER_HANDLER_CHAIN;
import static hera.client.ClientContextKeys.GRPC_REQUEST_TIMEOUT;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.Context;
import hera.ContextHolder;
import hera.EmptyContext;
import hera.FailoverHandler;
import hera.Invocation;
import hera.RequestMethod;
import hera.Requester;
import hera.Response;
import hera.strategy.InvocationStrategy;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.Test;

public class DecoratingRequesterTest extends AbstractTestCase {

  protected Context context;

  {
    context = EmptyContext.getInstance()
        .withValue(GRPC_REQUEST_TIMEOUT, new InvocationStrategy() {
          @Override
          public <T> Invocation<T> apply(Invocation<T> invocation) {
            return invocation;
          }
        })
        .withValue(GRPC_BEFORE_REQUEST, new InvocationStrategy() {
          @Override
          public <T> Invocation<T> apply(Invocation<T> invocation) {
            return invocation;
          }
        })
        .withValue(GRPC_AFTER_SUCCESS, new InvocationStrategy() {
          @Override
          public <T> Invocation<T> apply(Invocation<T> invocation) {
            return invocation;
          }
        })
        .withValue(GRPC_AFTER_FAILURE, new InvocationStrategy() {
          @Override
          public <T> Invocation<T> apply(Invocation<T> invocation) {
            return invocation;
          }
        })
        .withValue(GRPC_FAILOVER_HANDLER_CHAIN, new FailoverHandler() {
          @Override
          public <T> void handle(Invocation<T> invocation, Response<T> response) {

          }
        });
  }

  @Test
  public void testSuccessRequestOnNoContext() throws Exception {
    final Requester requester = new DecoratingRequester();
    final String expected = randomUUID().toString();
    final String name = randomUUID().toString();
    final String actual = requester.request(new TestInvocation<>(new RequestMethod<String>() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      protected String runInternal(final List<Object> parameters) throws Exception {
        return expected;
      }
    }));
    assertEquals(expected, actual);
  }

  @Test
  public void testFailRequestOnNoContext() throws Exception {
    final Requester requester = new DecoratingRequester();
    final Exception expected = new IllegalStateException();
    final String name = randomUUID().toString();
    final TestInvocation<String> invocation = new TestInvocation<>(new RequestMethod<String>() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      protected String runInternal(final List<Object> parameters) throws Exception {
        throw expected;
      }
    });

    try {
      requester.request(invocation);
      fail("Should throw exception");
    } catch (Exception actual) {
      assertEquals(expected, actual);
    }
  }

  @Test
  public void testSuccessRequestOnContext() throws Throwable {
    runOnOtherThread(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        try {
          ContextHolder.attach(context);
          final Requester requester = new DecoratingRequester();
          final String expected = randomUUID().toString();
          final String name = randomUUID().toString();
          final String actual = requester.request(new TestInvocation<>(new RequestMethod<String>() {
            @Override
            public String getName() {
              return name;
            }

            @Override
            protected String runInternal(final List<Object> parameters) throws Exception {
              return expected;
            }
          }));
          assertEquals(expected, actual);
          return null;
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testFailureRequestOnContext() throws Exception {
    final Exception expected = new IllegalStateException();
    try {
      runOnOtherThread(new Callable<Object>() {
        @Override
        public Object call() throws Exception {
          try {
            ContextHolder.attach(context);
            final Requester requester = new DecoratingRequester();
            final String name = randomUUID().toString();
            final String ret = requester
                .request(new TestInvocation<>(new RequestMethod<String>() {
                  @Override
                  public String getName() {
                    return name;
                  }

                  @Override
                  protected String runInternal(final List<Object> parameters) throws Exception {
                    throw expected;
                  }
                }));
            fail("Should be throw error");
            return null;
          } finally {
            ContextHolder.remove();
          }
        }
      });
    } catch (Exception actual) {
      assertEquals(expected, actual);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }



}
