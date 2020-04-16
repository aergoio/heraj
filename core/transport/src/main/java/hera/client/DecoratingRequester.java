/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_AFTER_FAILURE;
import static hera.client.ClientContextKeys.GRPC_AFTER_SUCCESS;
import static hera.client.ClientContextKeys.GRPC_BEFORE_REQUEST;
import static hera.client.ClientContextKeys.GRPC_FAILOVER_HANDLER_CHAIN;
import static hera.client.ClientContextKeys.GRPC_REQUEST_TIMEOUT;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.ContextHolder;
import hera.FailoverHandler;
import hera.Invocation;
import hera.Requester;
import hera.Response;
import hera.strategy.InvocationStrategy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor
class DecoratingRequester implements Requester {

  protected final transient Logger logger = getLogger(getClass());

  protected final Map<String, Invocation<?>> method2Invocation = new ConcurrentHashMap<>();

  protected final Object handlerChainLock = new Object();
  protected volatile FailoverHandler cached;

  @Override
  public <T> T request(final Invocation<T> invocation) throws Exception {
    logger.trace("Request with invocation: {}", invocation);

    final Response<T> response = Response.empty();
    final Invocation<T> decorated = withDecorated(invocation);
    logger.trace("Decorated: {}", decorated);
    try {
      final T value = decorated.invoke();
      logger.trace("Success: {}", value);
      response.success(value);
    } catch (Exception e) {
      logger.trace("Failure: {}", e.toString());
      response.fail(e);
      getFailoverHandler().handle(decorated, response);
    }

    if (null != response.getError()) {
      throw response.getError();
    }
    return response.getValue();
  }

  protected FailoverHandler getFailoverHandler() {
    if (null == cached) {
      synchronized (handlerChainLock) {
        if (null == cached) {
          final Context context = ContextHolder.current();
          cached = context.get(GRPC_FAILOVER_HANDLER_CHAIN);
        }
      }
    }
    return cached;
  }

  @SuppressWarnings("unchecked")
  protected <R> Invocation<R> withDecorated(final Invocation<R> invocation) {
    final String name = invocation.getRequestMethod().getName();
    if (!method2Invocation.containsKey(name)) {
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
    return null != strategy ? strategy.apply(invocation) : invocation;
  }

  protected <R> Invocation<R> withBefore(final Invocation<R> invocation) {
    final Context context = ContextHolder.current();
    final InvocationStrategy strategy = context.get(GRPC_BEFORE_REQUEST);
    return null != strategy ? strategy.apply(invocation) : invocation;
  }

  protected <R> Invocation<R> withAfterSuccess(final Invocation<R> invocation) {
    final Context context = ContextHolder.current();
    final InvocationStrategy strategy = context.get(GRPC_AFTER_SUCCESS);
    return null != strategy ? strategy.apply(invocation) : invocation;
  }

  protected <R> Invocation<R> withAtferFailure(final Invocation<R> invocation) {
    final Context context = ContextHolder.current();
    final InvocationStrategy strategy = context.get(GRPC_AFTER_FAILURE);
    return null != strategy ? strategy.apply(invocation) : invocation;
  }

}
