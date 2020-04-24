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

public class GrpcClientTest extends AbstractTestCase {

  @Test
  public void shouldGetReturnSingleInstanceOnConcurrentRun() throws Exception {
    // given
    final GrpcClient grpcClient = new GrpcClient();
    final int nThreads = Runtime.getRuntime().availableProcessors();
    final ExecutorService executorService = Executors.newFixedThreadPool(2 * nThreads);

    // then
    final List<Future<?>> futures = new LinkedList<>();
    final Map<Integer, Object> blockingStubMap = new ConcurrentHashMap<>();
    final Map<Integer, Object> futureStubMap = new ConcurrentHashMap<>();
    final Map<Integer, Object> streamStubMap = new ConcurrentHashMap<>();
    final Object dummy = new Object();
    for (int i = 0; i < 10 * nThreads; ++i) {
      final Future<?> future = executorService.submit(new Runnable() {
        @Override
        public void run() {
          blockingStubMap.put(System.identityHashCode(grpcClient.getBlockingStub()), dummy);
          futureStubMap.put(System.identityHashCode(grpcClient.getFutureStub()), dummy);
          streamStubMap.put(System.identityHashCode(grpcClient.getStreamStub()), dummy);
        }
      });
      futures.add(future);
    }
    for (final Future<?> future : futures) {
      future.get();
    }
    assertEquals("Should have single blocking stub, but has " + blockingStubMap, 1,
        blockingStubMap.size());
    assertEquals("Should have single future stub, but has " + futureStubMap, 1,
        futureStubMap.size());
    assertEquals("Should have single stream stub, but has " + streamStubMap, 1,
        streamStubMap.size());
  }

  @Test
  public void testClose() {
    final GrpcClient grpcClient = new GrpcClient();
    grpcClient.close();
  }

}
