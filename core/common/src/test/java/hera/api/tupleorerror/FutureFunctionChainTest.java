/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FutureFunctionChain.seq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class FutureFunctionChainTest extends AbstractTestCase {

  protected final long successTimeout = 10000L;

  protected final long failureTimeout = 100L;

  @Test
  public void testSeqFor2Future() {
    ResultOrErrorFuture<Integer> future1 = supplySuccess(() -> 1 << 0);
    ResultOrErrorFuture<Integer> future2 = supplySuccess(() -> 1 << 1);
    ResultOrErrorFuture<Integer> future = seq(() -> future1, () -> future2).map((a, b) -> a + b);
    assertEquals((1 << 2) - 1, future.get().getResult().intValue());
  }

  @Test(timeout = successTimeout)
  public void testSeqFor2FutureOn1stFail() {
    final long startTime = System.currentTimeMillis();
    ResultOrErrorFuture<Integer> future1 = supplyFailure(failureTimeout);
    ResultOrErrorFuture<Integer> future2 = supplySuccess(() -> 1 << 1, successTimeout);
    ResultOrErrorFuture<Integer> future = seq(() -> future1, () -> future2).map((a, b) -> a + b);
    assertTrue(future.get().hasError());
    assertTrue(startTime + failureTimeout < System.currentTimeMillis());
  }

  @Test(timeout = successTimeout)
  public void testSeqFor2FutureOn2ndFail() {
    final long startTime = System.currentTimeMillis();
    ResultOrErrorFuture<Integer> future1 = supplySuccess(() -> 1 << 0, successTimeout);
    ResultOrErrorFuture<Integer> future2 = supplyFailure(failureTimeout);
    ResultOrErrorFuture<Integer> future = seq(() -> future1, () -> future2).map((a, b) -> a + b);
    assertTrue(future.get().hasError());
    assertTrue(startTime + failureTimeout < System.currentTimeMillis());
  }

  @Test
  public void testSeqFor3Future() {
    ResultOrErrorFuture<Integer> future1 = supplySuccess(() -> 1 << 0);
    ResultOrErrorFuture<Integer> future2 = supplySuccess(() -> 1 << 1);
    ResultOrErrorFuture<Integer> future3 = supplySuccess(() -> 1 << 2);
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3).map((a, b, c) -> a + b + c);
    assertEquals((1 << 3) - 1, future.get().getResult().intValue());
  }

  @Test(timeout = successTimeout)
  public void testSeqFor3FutureOn1stFail() {
    final long startTime = System.currentTimeMillis();
    ResultOrErrorFuture<Integer> future1 = supplyFailure(failureTimeout);
    ResultOrErrorFuture<Integer> future2 = supplySuccess(() -> 1 << 1, successTimeout);
    ResultOrErrorFuture<Integer> future3 = supplySuccess(() -> 1 << 2, successTimeout);
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3).map((a, b, c) -> a + b + c);
    assertTrue(future.get().hasError());
    assertTrue(startTime + failureTimeout < System.currentTimeMillis());
  }

  @Test(timeout = successTimeout)
  public void testSeqFor3FutureOn2ndFail() {
    final long startTime = System.currentTimeMillis();
    ResultOrErrorFuture<Integer> future1 = supplySuccess(() -> 1 << 0, successTimeout);
    ResultOrErrorFuture<Integer> future2 = supplyFailure(failureTimeout);
    ResultOrErrorFuture<Integer> future3 = supplySuccess(() -> 1 << 2, successTimeout);
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3).map((a, b, c) -> a + b + c);
    assertTrue(future.get().hasError());
    assertTrue(startTime + failureTimeout < System.currentTimeMillis());
  }

  @Test(timeout = successTimeout)
  public void testSeqFor3FutureOn3rdFail() {
    final long startTime = System.currentTimeMillis();
    ResultOrErrorFuture<Integer> future1 = supplySuccess(() -> 1 << 0, successTimeout);
    ResultOrErrorFuture<Integer> future2 = supplySuccess(() -> 1 << 1, successTimeout);
    ResultOrErrorFuture<Integer> future3 = supplyFailure(failureTimeout);
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3).map((a, b, c) -> a + b + c);
    assertTrue(future.get().hasError());
    assertTrue(startTime + failureTimeout < System.currentTimeMillis());
  }

  @Test
  public void testSeqFor4Future() {
    ResultOrErrorFuture<Integer> future1 = supplySuccess(() -> 1 << 0);
    ResultOrErrorFuture<Integer> future2 = supplySuccess(() -> 1 << 1);
    ResultOrErrorFuture<Integer> future3 = supplySuccess(() -> 1 << 2);
    ResultOrErrorFuture<Integer> future4 = supplySuccess(() -> 1 << 3);
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3, () -> future4)
            .map((a, b, c, d) -> a + b + c + d);
    assertEquals((1 << 4) - 1, future.get().getResult().intValue());
  }

  @Test(timeout = successTimeout)
  public void testSeqFor4FutureOn1stFail() {
    final long startTime = System.currentTimeMillis();
    ResultOrErrorFuture<Integer> future1 = supplyFailure(failureTimeout);
    ResultOrErrorFuture<Integer> future2 = supplySuccess(() -> 1 << 1, successTimeout);
    ResultOrErrorFuture<Integer> future3 = supplySuccess(() -> 1 << 2, successTimeout);
    ResultOrErrorFuture<Integer> future4 = supplySuccess(() -> 1 << 3, successTimeout);
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3, () -> future4)
            .map((a, b, c, d) -> a + b + c + d);
    assertTrue(future.get().hasError());
    assertTrue(startTime + failureTimeout < System.currentTimeMillis());
  }

  @Test(timeout = successTimeout)
  public void testSeqFor4FutureOn2ndFail() {
    final long startTime = System.currentTimeMillis();
    ResultOrErrorFuture<Integer> future1 = supplySuccess(() -> 1 << 0, successTimeout);
    ResultOrErrorFuture<Integer> future2 = supplyFailure(failureTimeout);
    ResultOrErrorFuture<Integer> future3 = supplySuccess(() -> 1 << 2, successTimeout);
    ResultOrErrorFuture<Integer> future4 = supplySuccess(() -> 1 << 3, successTimeout);
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3, () -> future4)
            .map((a, b, c, d) -> a + b + c + d);
    assertTrue(future.get().hasError());
    assertTrue(startTime + failureTimeout < System.currentTimeMillis());
  }

  @Test(timeout = successTimeout)
  public void testSeqFor4FutureOn3rdFail() {
    final long startTime = System.currentTimeMillis();
    ResultOrErrorFuture<Integer> future1 = supplySuccess(() -> 1 << 0, successTimeout);
    ResultOrErrorFuture<Integer> future2 = supplySuccess(() -> 1 << 1, successTimeout);
    ResultOrErrorFuture<Integer> future3 = supplyFailure(failureTimeout);
    ResultOrErrorFuture<Integer> future4 = supplySuccess(() -> 1 << 3, successTimeout);
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3, () -> future4)
            .map((a, b, c, d) -> a + b + c + d);
    assertTrue(future.get().hasError());
    assertTrue(startTime + failureTimeout < System.currentTimeMillis());
  }

  @Test(timeout = successTimeout)
  public void testSeqFor4FutureOn4thFail() {
    final long startTime = System.currentTimeMillis();
    ResultOrErrorFuture<Integer> future1 = supplySuccess(() -> 1 << 0, successTimeout);
    ResultOrErrorFuture<Integer> future2 = supplySuccess(() -> 1 << 1, successTimeout);
    ResultOrErrorFuture<Integer> future3 = supplySuccess(() -> 1 << 2, successTimeout);
    ResultOrErrorFuture<Integer> future4 = supplyFailure(failureTimeout);
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3, () -> future4)
            .map((a, b, c, d) -> a + b + c + d);
    assertTrue(future.get().hasError());
    assertTrue(startTime + failureTimeout < System.currentTimeMillis());
  }

}
