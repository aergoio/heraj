/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;
import static org.slf4j.LoggerFactory.getLogger;
import com.google.common.util.concurrent.FutureCallback;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.exception.NotFoundException;
import hera.exception.RpcConnectionException;
import hera.exception.RpcException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class FutureChain<T, R> implements FutureCallback<T> {

  protected static final String ASYNC_CALL_MESSAGE =
      "============================== Async=call ==============================";

  protected final Logger logger = getLogger(getClass());

  // store Exception since getting stack trace element object is too expensive
  protected Exception stackTraceHolder = new Exception();

  protected final ResultOrErrorFuture<R> nextFuture;

  protected final Function<T, R> successHandler;

  @Override
  public void onSuccess(@Nullable T t) {
    try {
      R handled = successHandler.apply(t);
      logger.trace("Success converted: {}", handled);
      nextFuture.complete(success(handled));
    } catch (final Throwable e) {
      failNext(new RpcException(e));
    }
  }

  @Override
  public void onFailure(final Throwable e) {
    final RpcException wrapped = Optional.of(e).filter(StatusRuntimeException.class::isInstance)
        .map(StatusRuntimeException.class::cast).filter(s -> null != s.getStatus())
        .map(StatusRuntimeException::getStatus).map(Status::getCode).map(c -> {
          switch (c) {
            case UNAVAILABLE:
              return new RpcConnectionException(e);
            case NOT_FOUND:
              return new NotFoundException(e);
            default:
              return new RpcException(e);
          }
        }).orElse(e instanceof RpcException ? (RpcException) e : new RpcException(e));
    failNext(wrapped);
  }

  protected void failNext(final Throwable wrapped) {
    wrapped.setStackTrace(concatStackTrace(wrapped, stackTraceHolder));
    nextFuture.complete(fail(wrapped));
  }

  protected StackTraceElement[] concatStackTrace(final Throwable higher, final Throwable lower) {
    final StackTraceElement[] higherTrace = higher.getStackTrace();
    final StackTraceElement[] lowerTrace = lower.getStackTrace();

    final int highLength = higherTrace.length - 3; // remove higher thread stack : 3
    final int lowerLength = lowerTrace.length - 1; // remove FutureChain <init> stack : 1

    final StackTraceElement[] concated = new StackTraceElement[highLength + 1 + lowerLength];
    System.arraycopy(higherTrace, 0, concated, 0, highLength);
    concated[highLength] = new StackTraceElement(ASYNC_CALL_MESSAGE, "", null, 0);
    System.arraycopy(lowerTrace, 1, concated, highLength + 1, lowerLength);

    return concated;
  }

}
