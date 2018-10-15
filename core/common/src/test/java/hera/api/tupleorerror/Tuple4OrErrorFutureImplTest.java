/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;
import static hera.api.tupleorerror.FutureFunctionChain.seq;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.util.ThreadUtils;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class Tuple4OrErrorFutureImplTest {

  protected Tuple4OrErrorFuture<String, String, String, String> supplyAllSuccess() {
    return seq(() -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())),
        () -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())),
        () -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())),
        () -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())));
  }

  protected Tuple4OrErrorFuture<String, String, String, String> supply1stFail() {
    return seq(
        () -> ResultOrErrorFutureFactory.supply(() -> fail(new UnsupportedOperationException())),
        () -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())),
        () -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())),
        () -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())));
  }

  protected Tuple4OrErrorFuture<String, String, String, String> supply2ndFail() {
    return seq(() -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())),
        () -> ResultOrErrorFutureFactory.supply(() -> fail(new UnsupportedOperationException())),
        () -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())),
        () -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())));
  }

  protected Tuple4OrErrorFuture<String, String, String, String> supply3rdFail() {
    return seq(() -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())),
        () -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())),
        () -> ResultOrErrorFutureFactory.supply(() -> fail(new UnsupportedOperationException())),
        () -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())));
  }

  protected Tuple4OrErrorFuture<String, String, String, String> supply4thFail() {
    return seq(() -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())),
        () -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())),
        () -> ResultOrErrorFutureFactory.supply(() -> success(randomUUID().toString())),
        () -> ResultOrErrorFutureFactory.supply(() -> fail(new UnsupportedOperationException())));
  }

  @Test
  public void testCancel() {
    Tuple4OrErrorFuture<String, String, String, String> future =
        seq(() -> ResultOrErrorFutureFactory.supply(() -> {
          ThreadUtils.trySleep(10000L);
          return success(randomUUID().toString());
        }), () -> ResultOrErrorFutureFactory.supply(() -> {
          ThreadUtils.trySleep(10000L);
          return success(randomUUID().toString());
        }), () -> ResultOrErrorFutureFactory.supply(() -> {
          ThreadUtils.trySleep(10000L);
          return success(randomUUID().toString());
        }), () -> ResultOrErrorFutureFactory.supply(() -> {
          ThreadUtils.trySleep(10000L);
          return success(randomUUID().toString());
        }));
    future.cancel(false);
    assertTrue(future.isCancelled());
    assertTrue(future.isDone());
  }

  @Test
  public void testGet() {
    Tuple4OrErrorFuture<String, String, String, String> future = supplyAllSuccess();
    assertNotNull(future.get().get1());
    assertNotNull(future.get().get2());
    assertNotNull(future.get().get3());
  }

  @Test
  public void testGetOnErrorOn1stFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply1stFail();

    assertTrue(future.get().hasError());
    try {
      future.get().get1();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get2();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get3();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get4();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
  }

  @Test
  public void testGetOnErrorOn2ndFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply2ndFail();

    assertTrue(future.get().hasError());
    try {
      future.get().get1();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get2();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get3();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get4();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
  }

  @Test
  public void testGetOnErrorOn3rdFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply3rdFail();

    assertTrue(future.get().hasError());
    try {
      future.get().get1();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get2();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get3();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get4();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
  }

  @Test
  public void testGetOnErrorOn4thFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply4thFail();

    assertTrue(future.get().hasError());
    try {
      future.get().get1();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get2();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get3();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get4();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
  }

  @Test
  public void testGetWithTimeout() {
    Tuple4OrErrorFuture<String, String, String, String> future = supplyAllSuccess();

    Tuple4OrError<String, String, String, String> tuple2OrError = future.get(10L, TimeUnit.SECONDS);
    assertTrue(!tuple2OrError.hasError());
    assertNotNull(tuple2OrError.get1());
    assertNotNull(tuple2OrError.get2());
    assertNotNull(tuple2OrError.get3());
  }

  @Test
  public void testGetWithTimeoutOn1stFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply1stFail();
    Tuple4OrError<String, String, String, String> tuple2OrError = future.get(10L, TimeUnit.SECONDS);

    assertTrue(tuple2OrError.hasError());
    try {
      tuple2OrError.get1();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      tuple2OrError.get2();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get3();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get4();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
  }

  @Test
  public void testGetWithTimeoutOn2ndFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply2ndFail();
    Tuple4OrError<String, String, String, String> tuple2OrError = future.get(10L, TimeUnit.SECONDS);

    assertTrue(tuple2OrError.hasError());
    try {
      tuple2OrError.get1();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      tuple2OrError.get2();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get3();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get4();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
  }

  @Test
  public void testGetWithTimeoutOn3rdFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply3rdFail();
    Tuple4OrError<String, String, String, String> tuple2OrError = future.get(10L, TimeUnit.SECONDS);

    assertTrue(tuple2OrError.hasError());
    try {
      tuple2OrError.get1();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      tuple2OrError.get2();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get3();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get4();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
  }

  @Test
  public void testGetWithTimeoutOn4thFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply4thFail();
    Tuple4OrError<String, String, String, String> tuple2OrError = future.get(10L, TimeUnit.SECONDS);

    assertTrue(tuple2OrError.hasError());
    try {
      tuple2OrError.get1();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      tuple2OrError.get2();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get3();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
    try {
      future.get().get4();
      fail();
    } catch (Throwable e) {
      // good we expected this
    }
  }

  @Test
  public void testIfPresent() throws InterruptedException {
    Tuple4OrErrorFuture<String, String, String, String> future = supplyAllSuccess();
    CountDownLatch latch = new CountDownLatch(1);
    ResultOrErrorFuture<Boolean> next = future.ifPresent((a, b, c, d) -> latch.countDown());
    assertTrue(next.get().hasResult());
    assertEquals(0, latch.getCount());
  }

  @Test
  public void testIfPresentOn1stFail() throws InterruptedException {
    Tuple4OrErrorFuture<String, String, String, String> future = supply1stFail();
    CountDownLatch latch = new CountDownLatch(1);
    ResultOrErrorFuture<Boolean> next = future.ifPresent((a, b, c, d) -> latch.countDown());
    assertTrue(next.get().hasError());
    assertEquals(1, latch.getCount());
  }

  @Test
  public void testIfPresentOn2ndFail() throws InterruptedException {
    Tuple4OrErrorFuture<String, String, String, String> future = supply2ndFail();
    CountDownLatch latch = new CountDownLatch(1);
    ResultOrErrorFuture<Boolean> next = future.ifPresent((a, b, c, d) -> latch.countDown());
    assertTrue(next.get().hasError());
    assertEquals(1, latch.getCount());
  }

  @Test
  public void testIfPresentOn3rdFail() throws InterruptedException {
    Tuple4OrErrorFuture<String, String, String, String> future = supply3rdFail();
    CountDownLatch latch = new CountDownLatch(1);
    ResultOrErrorFuture<Boolean> next = future.ifPresent((a, b, c, d) -> latch.countDown());
    assertTrue(next.get().hasError());
    assertEquals(1, latch.getCount());
  }

  @Test
  public void testIfPresentOn4thFail() throws InterruptedException {
    Tuple4OrErrorFuture<String, String, String, String> future = supply4thFail();
    CountDownLatch latch = new CountDownLatch(1);
    ResultOrErrorFuture<Boolean> next = future.ifPresent((a, b, c, d) -> latch.countDown());
    assertTrue(next.get().hasError());
    assertEquals(1, latch.getCount());
  }

  @Test
  public void testIfPresentWithErrorOnNext() throws InterruptedException {
    Tuple4OrErrorFuture<String, String, String, String> future = supplyAllSuccess();
    ResultOrErrorFuture<Boolean> next = future.ifPresent((a, b, c, d) -> {
      throw new UnsupportedOperationException();
    });
    assertTrue(next.get().hasError());
  }

  @Test
  public void testFilterGeneratingResult() {
    Tuple4OrErrorFuture<String, String, String, String> future = supplyAllSuccess();
    Tuple4OrErrorFuture<String, String, String, String> next = future.filter((a, b, c, d) -> true);
    assertNotNull(next.get().get1());
    assertNotNull(next.get().get2());
  }

  @Test
  public void testFilterGeneratingNoResult() {
    Tuple4OrErrorFuture<String, String, String, String> future = supplyAllSuccess();
    Tuple4OrErrorFuture<String, String, String, String> next = future.filter((a, b, c, d) -> false);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testFilterOn1stFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply1stFail();
    Tuple4OrErrorFuture<String, String, String, String> next = future.filter((a, b, c, d) -> true);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testFilterOn2ndFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply2ndFail();
    Tuple4OrErrorFuture<String, String, String, String> next = future.filter((a, b, c, d) -> true);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testFilterOn3rdFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply3rdFail();
    Tuple4OrErrorFuture<String, String, String, String> next = future.filter((a, b, c, d) -> true);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testFilterOn4thFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply4thFail();
    Tuple4OrErrorFuture<String, String, String, String> next = future.filter((a, b, c, d) -> true);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testMap() {
    Tuple4OrErrorFuture<String, String, String, String> future = supplyAllSuccess();
    Tuple4OrErrorFuture<String, String, String, String> next = future.filter((a, b, c, d) -> true);
    assertNotNull(next.get().get1());
    assertNotNull(next.get().get2());
  }

  @Test
  public void testMapOn1stFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply1stFail();
    ResultOrErrorFuture<String> next = future.map((a, b, c, d) -> a);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testMapOn2ndFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply2ndFail();
    ResultOrErrorFuture<String> next = future.map((a, b, c, d) -> a);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testMapOn3rdFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply3rdFail();
    ResultOrErrorFuture<String> next = future.map((a, b, c, d) -> a);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testMapOn4thFail() {
    Tuple4OrErrorFuture<String, String, String, String> future = supply4thFail();
    ResultOrErrorFuture<String> next = future.map((a, b, c, d) -> a);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testMapGeneratingError() {
    Tuple4OrErrorFuture<String, String, String, String> future = supplyAllSuccess();
    ResultOrErrorFuture<String> next = future.map((a, b, c, d) -> {
      throw new UnsupportedOperationException();
    });
    assertTrue(next.get().hasError());
  }

}
