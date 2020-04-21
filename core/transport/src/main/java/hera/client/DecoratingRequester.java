/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_AFTER_FAILURE;
import static hera.client.ClientContextKeys.GRPC_AFTER_SUCCESS;
import static hera.client.ClientContextKeys.GRPC_BEFORE_REQUEST;
import static hera.client.ClientContextKeys.GRPC_FAILOVER_HANDLER_CHAIN;
import static hera.client.ClientContextKeys.GRPC_REQUEST_TIMEOUT;
import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.ContextHolder;
import hera.FailoverHandler;
import hera.Invocation;
import hera.Requester;
import hera.Response;
import hera.exception.HerajException;
import hera.strategy.InvocationStrategy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

class DecoratingRequester implements Requester {

  protected static final String ORIGIN_LINE = "------------ caused by ------------";

  protected final transient Logger logger = getLogger(getClass());

  protected final Map<String, Invocation<?>> method2Invocation = new ConcurrentHashMap<>();

  protected final Object failoverHandlerLock = new Object();
  protected volatile FailoverHandler cached;

  DecoratingRequester() {

  }

  @Override
  public <T> T request(final Invocation<T> invocation) throws Exception {
    assertNotNull(invocation, "Invocation must not null");
    logger.debug("Request with invocation: {}", invocation);

    Response<T> response;
    final Invocation<T> decorated = withDecorated(invocation);
    logger.trace("Decorated: {}", decorated);
    try {
      final T value = decorated.invoke();
      logger.debug("Success: {}", value);
      response = Response.success(value);
    } catch (Exception e) {
      logger.debug("Failure: {}", e.toString());
      response = Response.fail(e);
      response = handleFailover(decorated, response);
    }

    if (null != response.getError()) {
      // need to adjust stacktrace (current stack + origin stack)
      final Exception error = response.getError();
      error.setStackTrace(concatStackTrace(new Throwable().getStackTrace(),
          error.getStackTrace()));
      throw error;
    }

    return response.getValue();
  }

  @SuppressWarnings("unchecked")
  protected <R> Invocation<R> withDecorated(final Invocation<R> invocation) {
    final String name = invocation.getRequestMethod().getName();
    if (null == name) {
      throw new HerajException("Name of invocation must not null");
    }

    if (!method2Invocation.containsKey(name)) {
      logger.trace("Decorated method is not cached. Make an new one");
      Invocation<R> decorated = withTimeout(invocation);
      decorated = withBefore(decorated);
      decorated = withAfterSuccess(decorated);
      decorated = withAtferFailure(decorated);
      method2Invocation.put(name, decorated);
    }

    final Invocation<R> cached = (Invocation<R>) method2Invocation.get(name);
    return cached.withParameters(invocation.getParameters());
  }

  protected <R> Invocation<R> withTimeout(final Invocation<R> invocation) {
    final Context context = ContextHolder.current();
    final InvocationStrategy strategy = context.get(GRPC_REQUEST_TIMEOUT);
    if (null == strategy) {
      return invocation;
    }
    logger.trace("With timeout: {}", strategy);
    return strategy.apply(invocation);
  }

  protected <R> Invocation<R> withBefore(final Invocation<R> invocation) {
    final Context context = ContextHolder.current();
    final InvocationStrategy strategy = context.get(GRPC_BEFORE_REQUEST);
    if (null == strategy) {
      return invocation;
    }
    logger.trace("With before: {}", strategy);
    return strategy.apply(invocation);
  }

  protected <R> Invocation<R> withAfterSuccess(final Invocation<R> invocation) {
    final Context context = ContextHolder.current();
    final InvocationStrategy strategy = context.get(GRPC_AFTER_SUCCESS);
    if (null == strategy) {
      return invocation;
    }
    logger.trace("With after success: {}", strategy);
    return strategy.apply(invocation);
  }

  protected <R> Invocation<R> withAtferFailure(final Invocation<R> invocation) {
    final Context context = ContextHolder.current();
    final InvocationStrategy strategy = context.get(GRPC_AFTER_FAILURE);
    if (null == strategy) {
      return invocation;
    }
    logger.trace("With after failure: {}", strategy);
    return strategy.apply(invocation);
  }

  protected <T> Response<T> handleFailover(final Invocation<T> invocation,
      final Response<T> response) {
    final FailoverHandler failoverHandler = getFailoverHandler();
    if (null == failoverHandler) {
      return response;
    }
    logger.trace("Handle failover by {}", failoverHandler);
    return failoverHandler.handle(invocation, response);
  }

  protected FailoverHandler getFailoverHandler() {
    if (null == cached) {
      synchronized (failoverHandlerLock) {
        if (null == cached) {
          final Context context = ContextHolder.current();
          cached = context.get(GRPC_FAILOVER_HANDLER_CHAIN);
        }
      }
    }
    return cached;
  }

  protected final StackTraceElement[] concatStackTrace(final StackTraceElement[] current,
      final StackTraceElement[] cause) {
    final StackTraceElement[] concated = new StackTraceElement[current.length + cause.length + 1];
    System.arraycopy(current, 0, concated, 0, current.length);
    concated[current.length] = new StackTraceElement("", ORIGIN_LINE, null, 0);
    System.arraycopy(cause, 0, concated, current.length + 1, cause.length);
    return concated;
  }

}
