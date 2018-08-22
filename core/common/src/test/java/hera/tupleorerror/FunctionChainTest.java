/*
 * @copyright defined in LICENSE.txt
 */

package hera.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.seq;
import static hera.api.tupleorerror.FunctionChain.seqf;
import static hera.api.tupleorerror.FunctionChain.success;
import static org.junit.Assert.assertEquals;

import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import org.junit.Test;

public class FunctionChainTest {

  @Test
  public void testSeqfFor2Future() {
    ResultOrErrorFuture<Integer> future0 =
        ResultOrErrorFuture.supply(() -> success(new Integer(1)));
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFuture.supply(() -> success(new Integer(2)));
    ResultOrErrorFuture<Integer> future =
        seqf(() -> future0, () -> future1).thenApply((a, b) -> a + b);
    assertEquals(3, future.get().getResult().intValue());
  }

  @Test
  public void testSeqfFor3Future() {
    ResultOrErrorFuture<Integer> future0 =
        ResultOrErrorFuture.supply(() -> success(new Integer(1)));
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFuture.supply(() -> success(new Integer(2)));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFuture.supply(() -> success(new Integer(4)));
    ResultOrErrorFuture<Integer> future =
        seqf(() -> future0, () -> future1, () -> future2).thenApply((a, b, c) -> a + b + c);
    assertEquals(7, future.get().getResult().intValue());
  }

  @Test
  public void testSeqfFor4Future() {
    ResultOrErrorFuture<Integer> future0 =
        ResultOrErrorFuture.supply(() -> success(new Integer(1)));
    ResultOrErrorFuture<Integer> future1 =
        ResultOrErrorFuture.supply(() -> success(new Integer(2)));
    ResultOrErrorFuture<Integer> future2 =
        ResultOrErrorFuture.supply(() -> success(new Integer(4)));
    ResultOrErrorFuture<Integer> future3 =
        ResultOrErrorFuture.supply(() -> success(new Integer(8)));
    ResultOrErrorFuture<Integer> future =
        seqf(() -> future0, () -> future1, () -> future2, () -> future3)
            .thenApply((a, b, c, d) -> a + b + c + d);
    assertEquals(15, future.get().getResult().intValue());
  }

  @Test
  public void testSeqFor2Args() {
    ResultOrError<Integer> seqResult =
        seq(() -> success(new Integer(1)), () -> success(new Integer(2)))
            .thenApply((a, b) -> a + b);
    assertEquals(3, seqResult.getResult().intValue());
  }

  @Test
  public void testSeqFor3Args() {
    ResultOrError<Integer> seqResult =
        seq(() -> success(new Integer(1)), () -> success(new Integer(2)),
            () -> success(new Integer(4))).thenApply((a, b, c) -> a + b + c);
    assertEquals(7, seqResult.getResult().intValue());
  }

  @Test
  public void testSeqFor4Args() {
    ResultOrError<Integer> seqResult = seq(() -> success(new Integer(1)),
        () -> success(new Integer(2)), () -> success(new Integer(4)), () -> success(new Integer(8)))
            .thenApply((a, b, c, d) -> a + b + c + d);
    assertEquals(15, seqResult.getResult().intValue());
  }

}
