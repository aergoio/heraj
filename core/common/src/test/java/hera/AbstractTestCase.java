/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.util.ThreadUtils;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

@SuppressWarnings("rawtypes")
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({
    "javax.crypto.*",
    "javax.management.*",
    "javax.net.ssl.*",
    "javax.security.*",
    "org.bouncycastle.*"})
public abstract class AbstractTestCase {
  protected static final int N_TEST = 100;

  protected final transient Logger logger = getLogger(getClass());

  protected final Executor executor = Executors.newFixedThreadPool(4);

  protected <T> ResultOrErrorFuture<T> supplySuccess(Supplier<T> supplier) {
    return ResultOrErrorFutureFactory.supply(supplier, executor);
  }

  protected <T> ResultOrErrorFuture<T> supplySuccess(Supplier<T> supplier, long timeout) {
    return ResultOrErrorFutureFactory.supply(() -> {
      ThreadUtils.trySleep(timeout);
      return supplier.get();
    }, executor);
  }

  protected ResultOrErrorFuture supplyFailure() {
    return ResultOrErrorFutureFactory.supply(() -> {
      throw new UnsupportedOperationException();
    }, executor);
  }

  protected ResultOrErrorFuture supplyFailure(long timeout) {
    return ResultOrErrorFutureFactory.supply(() -> {
      ThreadUtils.trySleep(timeout);
      throw new UnsupportedOperationException();
    }, executor);
  }

}
