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
import io.grpc.StatusRuntimeException;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class FutureChainer<T, R> implements FutureCallback<T> {

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
      nextFuture.complete(fail(e));
    }
  }

  @Override
  public void onFailure(Throwable e) {
    logger.trace("Error: {}", e);
    final RpcException convertedError = Optional.of(findStatusRuntimeException(e))
        .filter(t -> t instanceof StatusRuntimeException).map(StatusRuntimeException.class::cast)
        .filter(s -> null != s.getStatus()).map(s -> s.getStatus().getCode()).map(c -> {
          switch (c) {
            case UNAVAILABLE:
              return new RpcConnectionException(e);
            case NOT_FOUND:
              return new NotFoundException(e);
            default:
              return new RpcException(e);
          }
        }).orElse(e instanceof RpcException ? (RpcException) e : new RpcException(e));
    nextFuture.complete(fail(convertedError));
  }

  protected Throwable findStatusRuntimeException(Throwable e) {
    Throwable cause = e;
    while (null != cause.getCause()) {
      if (cause instanceof StatusRuntimeException) {
        break;
      }
      cause = cause.getCause();
    }
    return cause;
  }

}
