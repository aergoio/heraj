/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;

public class GrpcClientProviderTest extends AbstractTestCase {

  @Test
  public void shouldGetReturnSingleInstanceOnConcurrentRun() throws Exception {
    // given
    final GrpcClientProvider grpcClientProvider = new GrpcClientProvider();
    final int nThreads = Runtime.getRuntime().availableProcessors();
    final ExecutorService executorService = Executors.newFixedThreadPool(2 * nThreads);

    // then
    final List<Future<?>> futures = new LinkedList<>();
    final Map<Integer, Object> clientMap = new ConcurrentHashMap<>();
    final Object dummy = new Object();
    for (int i = 0; i < 10 * nThreads; ++i) {
      final Future<?> future = executorService.submit(new Runnable() {
        @Override
        public void run() {
          clientMap.put(System.identityHashCode(grpcClientProvider.get()), dummy);
        }
      });
      futures.add(future);
    }
    for (final Future<?> future : futures) {
      future.get();
    }
    assertEquals("Should have single client, but has " + clientMap, 1, clientMap.size());
  }

}
