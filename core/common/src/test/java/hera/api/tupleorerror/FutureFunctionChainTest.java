/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;
import static hera.api.tupleorerror.FutureFunctionChain.seq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class FutureFunctionChainTest {

  @Test
  public void testSeqFor2Future() {
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 0)));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 1)));
    ResultOrErrorFuture<Integer> future = seq(() -> future1, () -> future2).map((a, b) -> a + b);
    assertEquals((1 << 2) - 1, future.get().getResult().intValue());
  }

  @Test
  public void testSeqOfFutureOn2Of1Fail() {
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFutureFactory.supply(() -> fail(new UnsupportedOperationException()));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 1)));
    ResultOrErrorFuture<Integer> future = seq(() -> future1, () -> future2).map((a, b) -> a + b);
    assertTrue(future.get().hasError());
  }

  @Test
  public void testSeqOfFutureOn2Of2Fail() {
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 0)));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFutureFactory.supply(() -> fail(new UnsupportedOperationException()));
    ResultOrErrorFuture<Integer> future = seq(() -> future1, () -> future2).map((a, b) -> a + b);
    assertTrue(future.get().hasError());
  }

  @Test
  public void testSeqFor3Future() {
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 0)));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 1)));
    ResultOrErrorFuture<Integer> future3 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 2)));
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3).map((a, b, c) -> a + b + c);
    assertEquals((1 << 3) - 1, future.get().getResult().intValue());
  }

  @Test
  public void testSeqOfFutureOn3Of1Fail() {
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFutureFactory.supply(() -> fail(new UnsupportedOperationException()));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 1)));
    ResultOrErrorFuture<Integer> future3 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 2)));
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3).map((a, b, c) -> a + b + c);
    assertTrue(future.get().hasError());
  }

  @Test
  public void testSeqOfFutureOn3Of2Fail() {
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 0)));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFutureFactory.supply(() -> fail(new UnsupportedOperationException()));
    ResultOrErrorFuture<Integer> future3 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 2)));
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3).map((a, b, c) -> a + b + c);
    assertTrue(future.get().hasError());
  }

  @Test
  public void testSeqOfFutureOn3Of3Fail() {
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 0)));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 1)));
    ResultOrErrorFuture<Integer> future3 =
        ResultOrErrorFutureFactory.supply(() -> fail(new UnsupportedOperationException()));
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3).map((a, b, c) -> a + b + c);
    assertTrue(future.get().hasError());
  }

  @Test
  public void testSeqFor4Future() {
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 0)));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 1)));
    ResultOrErrorFuture<Integer> future3 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 2)));
    ResultOrErrorFuture<Integer> future4 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 3)));
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3, () -> future4)
            .map((a, b, c, d) -> a + b + c + d);
    assertEquals((1 << 4) - 1, future.get().getResult().intValue());
  }

  @Test
  public void testSeqOfFutureOn4Of1Fail() {
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFutureFactory.supply(() -> fail(new UnsupportedOperationException()));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 1)));
    ResultOrErrorFuture<Integer> future3 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 2)));
    ResultOrErrorFuture<Integer> future4 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 3)));
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3, () -> future4)
            .map((a, b, c, d) -> a + b + c + d);
    assertTrue(future.get().hasError());
  }

  @Test
  public void testSeqOfFutureOn4Of2Fail() {
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 0)));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFutureFactory.supply(() -> fail(new UnsupportedOperationException()));
    ResultOrErrorFuture<Integer> future3 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 2)));
    ResultOrErrorFuture<Integer> future4 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 3)));
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3, () -> future4)
            .map((a, b, c, d) -> a + b + c + d);
    assertTrue(future.get().hasError());
  }

  @Test
  public void testSeqOfFutureOn4Of3Fail() {
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 0)));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 1)));
    ResultOrErrorFuture<Integer> future3 =
        ResultOrErrorFutureFactory.supply(() -> fail(new UnsupportedOperationException()));
    ResultOrErrorFuture<Integer> future4 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 3)));
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3, () -> future4)
            .map((a, b, c, d) -> a + b + c + d);
    assertTrue(future.get().hasError());
  }

  @Test
  public void testSeqOfFutureOn4Of4Fail() {
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 0)));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 1)));
    ResultOrErrorFuture<Integer> future3 =
        ResultOrErrorFutureFactory.supply(() -> success(new Integer(1 << 2)));
    ResultOrErrorFuture<Integer> future4 =
        ResultOrErrorFutureFactory.supply(() -> fail(new UnsupportedOperationException()));
    ResultOrErrorFuture<Integer> future =
        seq(() -> future1, () -> future2, () -> future3, () -> future4)
            .map((a, b, c, d) -> a + b + c + d);
    assertTrue(future.get().hasError());
  }

}
