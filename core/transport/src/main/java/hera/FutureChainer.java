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
import io.grpc.Status;
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
  public void onFailure(Throwable throwable) {
    logger.trace("Error: {}", throwable);
    if (throwable instanceof StatusRuntimeException) {
      StatusRuntimeException e = (StatusRuntimeException) throwable;
      if (Optional.ofNullable(e.getStatus()).map(Status::getCode)
          .filter(code -> Status.NOT_FOUND.getCode() == code).isPresent()) {
        nextFuture.complete(fail(new NotFoundException(throwable)));
      }
    }
    nextFuture.complete(fail(throwable));
  }

}
