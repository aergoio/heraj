/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FutureFunctionChain.seq;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class Tuple3OrErrorFutureImplTest extends AbstractTestCase {

  protected Tuple3OrErrorFuture<String, String, String> supplyAllSuccess() {
    return seq(() -> supplySuccess(() -> randomUUID().toString()),
        () -> supplySuccess(() -> randomUUID().toString()),
        () -> supplySuccess(() -> randomUUID().toString()));
  }

  protected Tuple3OrErrorFuture<String, String, String> supply1stFail() {
    return seq(() -> supplyFailure(), () -> supplySuccess(() -> randomUUID().toString()),
        () -> supplySuccess(() -> randomUUID().toString()));
  }

  protected Tuple3OrErrorFuture<String, String, String> supply2ndFail() {
    return seq(() -> supplySuccess(() -> randomUUID().toString()), () -> supplyFailure(),
        () -> supplySuccess(() -> randomUUID().toString()));
  }

  protected Tuple3OrErrorFuture<String, String, String> supply3rdFail() {
    return seq(() -> supplySuccess(() -> randomUUID().toString()),
        () -> supplySuccess(() -> randomUUID().toString()), () -> supplyFailure());

  }

  @Test(timeout = 1000L)
  public void testCancel() {
    Tuple3OrErrorFuture<String, String, String> future =
        seq(() -> supplySuccess(() -> randomUUID().toString(), 10000L),
            () -> supplySuccess(() -> randomUUID().toString(), 10000L),
            () -> supplySuccess(() -> randomUUID().toString(), 10000L));
    future.cancel();
    assertTrue(future.isCancelled());
    assertTrue(future.isDone());
  }

  @Test
  public void testGet() {
    Tuple3OrErrorFuture<String, String, String> future = supplyAllSuccess();
    assertNotNull(future.get().get1());
    assertNotNull(future.get().get2());
    assertNotNull(future.get().get3());
  }

  @Test
  public void testGetOnErrorOn1stFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply1stFail();

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
  }

  @Test
  public void testGetOnErrorOn2ndFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply2ndFail();

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
  }

  @Test
  public void testGetOnErrorOn3rdFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply3rdFail();

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
  }

  @Test
  public void testGetWithTimeout() {
    Tuple3OrErrorFuture<String, String, String> future = supplyAllSuccess();

    Tuple3OrError<String, String, String> tuple2OrError = future.get(10L, TimeUnit.SECONDS);
    assertTrue(!tuple2OrError.hasError());
    assertNotNull(tuple2OrError.get1());
    assertNotNull(tuple2OrError.get2());
    assertNotNull(tuple2OrError.get3());
  }

  @Test
  public void testGetWithTimeoutOn1stFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply1stFail();
    Tuple3OrError<String, String, String> tuple2OrError = future.get(10L, TimeUnit.SECONDS);

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
  }

  @Test
  public void testGetWithTimeoutOn2ndFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply2ndFail();
    Tuple3OrError<String, String, String> tuple2OrError = future.get(10L, TimeUnit.SECONDS);

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
  }

  @Test
  public void testGetWithTimeoutOn3rdFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply3rdFail();
    Tuple3OrError<String, String, String> tuple2OrError = future.get(10L, TimeUnit.SECONDS);

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
  }

  @Test
  public void testIfPresent() {
    Tuple3OrErrorFuture<String, String, String> future = supplyAllSuccess();
    CountDownLatch latch = new CountDownLatch(1);
    ResultOrErrorFuture<Boolean> next = future.ifPresent((a, b, c) -> latch.countDown());
    assertTrue(next.get().hasResult());
    assertEquals(0, latch.getCount());
  }

  @Test
  public void testIfPresentOn1stFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply1stFail();
    CountDownLatch latch = new CountDownLatch(1);
    ResultOrErrorFuture<Boolean> next = future.ifPresent((a, b, c) -> latch.countDown());
    assertTrue(next.get().hasError());
    assertEquals(1, latch.getCount());
  }

  @Test
  public void testIfPresentOn2ndFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply2ndFail();
    CountDownLatch latch = new CountDownLatch(1);
    ResultOrErrorFuture<Boolean> next = future.ifPresent((a, b, c) -> latch.countDown());
    assertTrue(next.get().hasError());
    assertEquals(1, latch.getCount());
  }

  @Test
  public void testIfPresentOn3rdFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply3rdFail();
    CountDownLatch latch = new CountDownLatch(1);
    ResultOrErrorFuture<Boolean> next = future.ifPresent((a, b, c) -> latch.countDown());
    assertTrue(next.get().hasError());
    assertEquals(1, latch.getCount());
  }

  @Test
  public void testIfPresentWithErrorOnNext() {
    Tuple3OrErrorFuture<String, String, String> future = supplyAllSuccess();
    ResultOrErrorFuture<Boolean> next = future.ifPresent((a, b, c) -> {
      throw new UnsupportedOperationException();
    });
    assertTrue(next.get().hasError());
  }

  @Test
  public void testFilterGeneratingResult() {
    Tuple3OrErrorFuture<String, String, String> future = supplyAllSuccess();
    Tuple3OrErrorFuture<String, String, String> next = future.filter((a, b, c) -> true);
    assertNotNull(next.get().get1());
    assertNotNull(next.get().get2());
  }

  @Test
  public void testFilterGeneratingNoResult() {
    Tuple3OrErrorFuture<String, String, String> future = supplyAllSuccess();
    Tuple3OrErrorFuture<String, String, String> next = future.filter((a, b, c) -> false);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testFilterOn1stFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply1stFail();
    Tuple3OrErrorFuture<String, String, String> next = future.filter((a, b, c) -> true);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testFilterOn2ndFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply2ndFail();
    Tuple3OrErrorFuture<String, String, String> next = future.filter((a, b, c) -> true);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testFilterOn3rdFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply3rdFail();
    Tuple3OrErrorFuture<String, String, String> next = future.filter((a, b, c) -> true);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testMap() {
    Tuple3OrErrorFuture<String, String, String> future = supplyAllSuccess();
    Tuple3OrErrorFuture<String, String, String> next = future.filter((a, b, c) -> true);
    assertNotNull(next.get().get1());
    assertNotNull(next.get().get2());
  }

  @Test
  public void testMapOn1stFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply1stFail();
    ResultOrErrorFuture<String> next = future.map((a, b, c) -> a);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testMapOn2ndFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply2ndFail();
    ResultOrErrorFuture<String> next = future.map((a, b, c) -> a);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testMapOn3rdFail() {
    Tuple3OrErrorFuture<String, String, String> future = supply3rdFail();
    ResultOrErrorFuture<String> next = future.map((a, b, c) -> a);
    assertTrue(next.get().hasError());
  }

  @Test
  public void testMapGeneratingError() {
    Tuple3OrErrorFuture<String, String, String> future = supplyAllSuccess();
    ResultOrErrorFuture<String> next = future.map((a, b, c) -> {
      throw new UnsupportedOperationException();
    });
    assertTrue(next.get().hasError());
  }

}
