/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.success;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class ResultOrErrorFutureImplTest extends AbstractTestCase {

  @Test(timeout = 1000L)
  public void testCancel() {
    ResultOrErrorFuture<String> future = supplySuccess(() -> randomUUID().toString(), 10000L);
    future.cancel();
    assertTrue(future.isCancelled());
    assertTrue(future.isDone());
  }

  @Test
  public void testGet() {
    ResultOrErrorFuture<String> future = supplySuccess(() -> randomUUID().toString());
    assertTrue(future.get().hasResult());
  }

  @Test
  public void testGetOnError() {
    ResultOrErrorFuture<String> future = supplyFailure();
    assertTrue(future.get().hasError());
  }

  @Test
  public void testGetWithTimeout() {
    ResultOrErrorFuture<String> future = supplySuccess(() -> randomUUID().toString());
    assertTrue(future.get(10L, TimeUnit.SECONDS).hasResult());
  }

  @Test
  public void testGetWithTimeoutOnError() {
    ResultOrErrorFuture<String> future = supplyFailure();
    assertTrue(future.get(10L, TimeUnit.SECONDS).hasError());
  }

  @Test
  public void testComplete() {
    ResultOrErrorFuture<String> future = ResultOrErrorFutureFactory.supplyEmptyFuture();
    assertTrue(future.complete(success(randomUUID().toString())));
    assertTrue(future.get().hasResult());
  }

  @Test
  public void testIfPresent() {
    ResultOrErrorFuture<String> future = supplySuccess(() -> randomUUID().toString());
    CountDownLatch latch = new CountDownLatch(1);
    ResultOrErrorFuture<Boolean> next = future.ifPresent(s -> latch.countDown());
    assertTrue(next.get().hasResult());
    assertEquals(0, latch.getCount());
  }

  @Test
  public void testIfPresentWithError() {
    ResultOrErrorFuture<String> future = supplyFailure();
    CountDownLatch latch = new CountDownLatch(1);
    ResultOrErrorFuture<Boolean> next = future.ifPresent(s -> latch.countDown());
    assertTrue(next.get().hasError());
    assertEquals(1, latch.getCount());
  }

  @Test
  public void testIfPresentWithErrorOnNext() {
    ResultOrErrorFuture<String> future = supplyFailure();
    ResultOrErrorFuture<Boolean> next = future.ifPresent(s -> {
      throw new UnsupportedOperationException();
    });
    assertTrue(next.get().hasError());
  }

  @Test
  public void testFilterGeneratingResult() {
    ResultOrErrorFuture<String> future = supplySuccess(() -> randomUUID().toString());
    ResultOrErrorFuture<String> next = future.filter(a -> true);
    assertTrue(next.get().hasResult());
  }

  @Test
  public void testFilterGeneratingNoResult() {
    ResultOrErrorFuture<String> future = supplySuccess(() -> randomUUID().toString());
    ResultOrErrorFuture<String> next = future.filter(a -> false);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testFilterWithError() {
    ResultOrErrorFuture<String> future = supplyFailure();
    ResultOrErrorFuture<String> next = future.filter(a -> true);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testMap() {
    final String data = randomUUID().toString();
    ResultOrErrorFuture<String> future = supplySuccess(() -> data);
    ResultOrErrorFuture<Integer> next = future.map(String::length);
    assertEquals(data.length(), next.get().getResult().intValue());
  }

  @Test
  public void testMapWithError() {
    ResultOrErrorFuture<String> future = supplyFailure();
    ResultOrErrorFuture<Integer> next = future.map(String::length);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testMapWithErrorOnNext() {
    final String data = randomUUID().toString();
    ResultOrErrorFuture<String> future = ResultOrErrorFutureFactory.supply(() -> data);
    ResultOrErrorFuture<Integer> next = future.map(s -> {
      throw new UnsupportedOperationException();
    });
    assertTrue(next.get().hasError());
  }

  @Test
  public void testFlatMap() {
    ResultOrErrorFuture<String> future = supplySuccess(() -> randomUUID().toString());
    ResultOrErrorFuture<Object> next =
        future.flatMap(s -> ResultOrErrorFutureFactory.supply(() -> success(s.length())));
    assertTrue(next.get().hasResult());
  }

  @Test
  public void testFlatMapWithError() {
    ResultOrErrorFuture<String> future = supplyFailure();
    ResultOrErrorFuture<Object> next =
        future.flatMap(s -> ResultOrErrorFutureFactory.supply(() -> success(s.length())));
    assertTrue(next.get().hasError());
  }

  @Test
  public void testFlatMapWithErrorOnNext() {
    ResultOrErrorFuture<String> future = supplySuccess(() -> randomUUID().toString());
    ResultOrErrorFuture<Object> next = future.flatMap(s -> {
      throw new UnsupportedOperationException();
    });
    assertTrue(next.get().hasError());
  }

}
