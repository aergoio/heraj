/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.util.concurrent.FutureCallback;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class FutureChainer<T, R> implements FutureCallback<T> {

  protected final Logger logger = getLogger(getClass());

  @Getter
  protected final CompletableFuture<R> nextFuture;

  protected final Function<T, R> converter;


  @Override
  public void onSuccess(@Nullable T t) {
    R converted = converter.apply(t);
    nextFuture.complete(converted);
  }

  @Override
  public void onFailure(Throwable throwable) {
    logger.trace("Error: {}", throwable);
    nextFuture.completeExceptionally(throwable);
  }

}
