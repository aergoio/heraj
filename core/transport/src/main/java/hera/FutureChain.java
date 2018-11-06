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
import hera.util.ExceptionUtils;
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

  // store Exception since getting stack trace element object is too expensive
  protected final Exception stackTraceHolder = new Exception();

  protected final Logger logger = getLogger(getClass());

  protected final ResultOrErrorFuture<R> nextFuture;

  protected final Function<T, R> converter;

  @Override
  public void onSuccess(@Nullable T t) {
    try {
      R converted = converter.apply(t);
      logger.trace("Converted: {}", converted);
      nextFuture.complete(success(converted));
    } catch (Throwable e) {
      e.setStackTrace(ExceptionUtils.concat(e.getStackTrace(), stackTraceHolder.getStackTrace()));
      nextFuture.complete(fail(e));
    }
  }

  @Override
  public void onFailure(Throwable e) {
    final RpcException wrappedError =
        Optional.of(e).filter(StatusRuntimeException.class::isInstance)
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
    wrappedError.setStackTrace(
        ExceptionUtils.concat(wrappedError.getStackTrace(), stackTraceHolder.getStackTrace()));
    nextFuture.complete(fail(wrappedError));
  }

}
