/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.tupleorerror.FunctionChain.fail;
import static java.util.Optional.of;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.util.concurrent.FutureCallback;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.exception.NotFoundException;
import hera.exception.RpcConnectionException;
import hera.exception.RpcException;
import io.grpc.StatusRuntimeException;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class FutureChain<T, R> implements FutureCallback<T> {

  protected static final String ASYNC_CALL_MESSAGE =
      "============================== Async call ==============================";

  protected final Logger logger = getLogger(getClass());

  // store Exception since getting stack trace element object is too expensive
  protected Exception stackTraceHolder = new Exception();

  protected final ResultOrErrorFuture<R> nextFuture;

  @Setter
  protected Function<T, ResultOrError<R>> successHandler;

  @Setter
  protected Function<? super Throwable, ResultOrError<R>> failureHandler = e -> fail(e);

  @Override
  public void onSuccess(@Nullable T t) {
    ResultOrError<R> handled = successHandler.apply(t);
    if (handled.hasResult()) {
      nextFuture.complete(handled);
    } else {
      failNext(wrapWithRpcException(handled.getError()));
    }
  }

  @Override
  public void onFailure(final Throwable e) {
    ResultOrError<R> handled = failureHandler.apply(e);
    if (handled.hasResult()) {
      nextFuture.complete(handled);
    } else {
      failNext(wrapWithRpcException(handled.getError()));
    }
  }

  protected RpcException wrapWithRpcException(final Throwable e) {
    return of(e).filter(StatusRuntimeException.class::isInstance)
        .map(StatusRuntimeException.class::cast).map(this::convertGrpcBasisException)
        .orElse(e instanceof RpcException ? (RpcException) e : new RpcException(e));
  }

  protected RpcException convertGrpcBasisException(final StatusRuntimeException e) {
    switch (e.getStatus().getCode()) {
      case UNAVAILABLE:
        return new RpcConnectionException(e);
      case NOT_FOUND:
        return new NotFoundException(e);
      default:
        return new RpcException(e);
    }
  }

  protected void failNext(final Throwable wrapped) {
    wrapped.setStackTrace(concatStackTrace(wrapped, stackTraceHolder));
    nextFuture.complete(fail(wrapped));
  }

  protected StackTraceElement[] concatStackTrace(final Throwable asyncStack,
      final Throwable callerStack) {
    final StackTraceElement[] asyncStackTrace = asyncStack.getStackTrace();
    final StackTraceElement[] callerStackTrace = callerStack.getStackTrace();

    // remove higher thread stack : 3
    final int asyncStackLength = asyncStackTrace.length - 3;
    // remove FutureChain <init> stack : 1
    final int callerStackLength = callerStackTrace.length - 1;

    final StackTraceElement[] concated =
        new StackTraceElement[asyncStackLength + 1 + callerStackLength];
    System.arraycopy(asyncStackTrace, 0, concated, 0, asyncStackLength);
    concated[asyncStackLength] = new StackTraceElement(ASYNC_CALL_MESSAGE, "", null, 0);
    System.arraycopy(callerStackTrace, 1, concated, asyncStackLength + 1, callerStackLength);

    return concated;
  }

}
