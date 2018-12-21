/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.api.tupleorerror.FunctionChain.of;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import hera.EmptyContext;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import java.util.concurrent.Executors;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@SuppressWarnings("unchecked")
@BenchmarkMode(Mode.Throughput)
public class FutureChainBenchmark {

  @State(Scope.Benchmark)
  public static class User {

    protected final ListeningExecutorService service =
        MoreExecutors.listeningDecorator(Executors.newWorkStealingPool());

    public void onSuccess() {
      ListenableFuture<Integer> originFuture = service.submit(() -> null);
      ResultOrErrorFuture<String> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();
      FutureChain<Integer, String> callback =
          new FutureChain<>(nextFuture, EmptyContext.getInstance());
      callback.setSuccessHandler(v -> of(() -> null));
      addCallback(originFuture, callback, directExecutor());

      nextFuture.get().getResult();
    }

    public void onFailure() {
      ListenableFuture<Integer> originFuture = service.submit(() -> {
        throwError();
        return null;
      });
      ResultOrErrorFuture<String> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();
      FutureChain<Integer, String> callback =
          new FutureChain<>(nextFuture, EmptyContext.getInstance());
      callback.setSuccessHandler(v -> of(() -> null));
      addCallback(originFuture, callback, directExecutor());

      try {
        nextFuture.get().getResult();
      } catch (Exception e) {
        // good we expected this
      }
    }

    protected void throwError() {
      throw new UnsupportedOperationException();
    }

  }

  @Benchmark
  public void onSuccess(final User user) {
    user.onSuccess();
  }

  @Benchmark
  public void onFailure(final User user) {
    user.onFailure();
  }
}


