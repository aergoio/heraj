/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.ContextHolder;
import hera.Invocation;
import hera.RequestMethod;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Time;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
@ToString
@RequiredArgsConstructor
public class TimeoutStrategy implements InvocationStrategy {

  @ToString.Exclude
  protected final transient Logger logger = getLogger(getClass());

  protected final Time timeout;

  @Override
  public <T> Invocation<T> apply(final Invocation<T> invocation) {
    assertNotNull(invocation, "Invocation must not null");
    return new TimeoutInvocation<T>(timeout, invocation.getRequestMethod(),
        invocation.getParameters());
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @ToString
  @EqualsAndHashCode
  private class TimeoutInvocation<T> implements Invocation<T> {

    protected final Time timeout;

    @Getter
    protected final RequestMethod<T> requestMethod;

    @Getter
    protected final List<Object> parameters;

    @Override
    public T invoke() throws Exception {
      try {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Context context = ContextHolder.current();
        final Future<T> future = executorService.submit(new Callable<T>() {
          @Override
          public T call() throws Exception {
            try {
              // need to attach
              ContextHolder.attach(context);
              return requestMethod.invoke(parameters);
            } finally {
              ContextHolder.remove();
            }
          }
        });
        return future.get(timeout.getValue(), timeout.getUnit());
      } catch (Exception e) {
        if (e instanceof TimeoutException) {
          logger.debug("Request timed out within {}", timeout);
          throw e;
        } else if (e instanceof ExecutionException) {
          throw (Exception) e.getCause();
        } else {
          throw e;
        }
      }
    }

    @Override
    public Invocation<T> withParameters(final List<Object> parameters) {
      assertNotNull(parameters, "Parameters must not null");
      return new TimeoutInvocation<>(timeout, requestMethod, parameters);
    }
  }

}
