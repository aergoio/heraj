/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.seq;
import static hera.api.tupleorerror.FunctionChain.success;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class FunctionChainTest extends AbstractTestCase {

  @Test
  public void testSeqFor2Args() {
    ResultOrError<Integer> first = success(1 << 0);
    ResultOrError<Integer> second = success(1 << 1);
    ResultOrError<Integer> seqResult = seq(() -> first, () -> second).map((a, b) -> a + b);
    assertEquals((1 << 2) - 1, seqResult.getResult().intValue());
  }

  @Test
  public void testSeqOn2Of1Fail() {
    ResultOrError<Integer> first =
        (ResultOrError<Integer>) fail(new UnsupportedOperationException());
    ResultOrError<Integer> second = success(1 << 1);
    ResultOrError<Integer> seqResult = seq(() -> first, () -> second).map((a, b) -> a + b);
    assertTrue(seqResult.hasError());
  }

  @Test
  public void testSeqOn2Of2Fail() {
    ResultOrError<Integer> first = success(1 << 1);
    ResultOrError<Integer> second =
        (ResultOrError<Integer>) fail(new UnsupportedOperationException());
    ResultOrError<Integer> seqResult = seq(() -> first, () -> second).map((a, b) -> a + b);
    assertTrue(seqResult.hasError());
  }

  @Test
  public void testSeqFor3Args() {
    ResultOrError<Integer> first = success(1 << 0);
    ResultOrError<Integer> second = success(1 << 1);
    ResultOrError<Integer> third = success(1 << 2);
    ResultOrError<Integer> seqResult =
        seq(() -> first, () -> second, () -> third).map((a, b, c) -> a + b + c);
    assertEquals((1 << 3) - 1, seqResult.getResult().intValue());
  }

  @Test
  public void testSeqOn3Of1fail() {
    ResultOrError<Integer> first =
        (ResultOrError<Integer>) fail(new UnsupportedOperationException());
    ResultOrError<Integer> second = success(1 << 1);
    ResultOrError<Integer> third = success(1 << 2);
    ResultOrError<Integer> seqResult =
        seq(() -> first, () -> second, () -> third).map((a, b, c) -> a + b + c);
    assertTrue(seqResult.hasError());
  }

  @Test
  public void testSeqOn3Of2fail() {
    ResultOrError<Integer> first = success(1 << 0);
    ResultOrError<Integer> second =
        (ResultOrError<Integer>) fail(new UnsupportedOperationException());
    ResultOrError<Integer> third = success(1 << 2);
    ResultOrError<Integer> seqResult =
        seq(() -> first, () -> second, () -> third).map((a, b, c) -> a + b + c);
    assertTrue(seqResult.hasError());
  }

  @Test
  public void testSeqOn3Of3fail() {
    ResultOrError<Integer> first = success(1 << 0);
    ResultOrError<Integer> second = success(1 << 1);
    ResultOrError<Integer> third =
        (ResultOrError<Integer>) fail(new UnsupportedOperationException());
    ResultOrError<Integer> seqResult =
        seq(() -> first, () -> second, () -> third).map((a, b, c) -> a + b + c);
    assertTrue(seqResult.hasError());
  }

  @Test
  public void testSeqFor4Args() {
    ResultOrError<Integer> first = success(1 << 0);
    ResultOrError<Integer> second = success(1 << 1);
    ResultOrError<Integer> third = success(1 << 2);
    ResultOrError<Integer> fourth = success(1 << 3);
    ResultOrError<Integer> seqResult = seq(() -> first, () -> second, () -> third, () -> fourth)
        .map((a, b, c, d) -> a + b + c + d);
    assertEquals((1 << 4) - 1, seqResult.getResult().intValue());
  }

  @Test
  public void testSeqOn4Of1fail() {
    ResultOrError<Integer> first =
        (ResultOrError<Integer>) fail(new UnsupportedOperationException());
    ResultOrError<Integer> second = success(1 << 1);
    ResultOrError<Integer> third = success(1 << 2);
    ResultOrError<Integer> fourth = success(1 << 3);
    ResultOrError<Integer> seqResult = seq(() -> first, () -> second, () -> third, () -> fourth)
        .map((a, b, c, d) -> a + b + c + d);
    assertTrue(seqResult.hasError());
  }

  @Test
  public void testSeqOn4Of2fail() {
    ResultOrError<Integer> first = success(1 << 0);
    ResultOrError<Integer> second =
        (ResultOrError<Integer>) fail(new UnsupportedOperationException());
    ResultOrError<Integer> third = success(1 << 2);
    ResultOrError<Integer> fourth = success(1 << 3);
    ResultOrError<Integer> seqResult = seq(() -> first, () -> second, () -> third, () -> fourth)
        .map((a, b, c, d) -> a + b + c + d);
    assertTrue(seqResult.hasError());
  }

  @Test
  public void testSeqOn4Of3fail() {
    ResultOrError<Integer> first = success(1 << 0);
    ResultOrError<Integer> second = success(1 << 1);
    ResultOrError<Integer> third =
        (ResultOrError<Integer>) fail(new UnsupportedOperationException());
    ResultOrError<Integer> fourth = success(1 << 3);
    ResultOrError<Integer> seqResult = seq(() -> first, () -> second, () -> third, () -> fourth)
        .map((a, b, c, d) -> a + b + c + d);
    assertTrue(seqResult.hasError());
  }

  @Test
  public void testSeqOn4Of4fail() {
    ResultOrError<Integer> first = success(1 << 0);
    ResultOrError<Integer> second = success(1 << 1);
    ResultOrError<Integer> third = success(1 << 2);
    ResultOrError<Integer> fourth =
        (ResultOrError<Integer>) fail(new UnsupportedOperationException());
    ResultOrError<Integer> seqResult = seq(() -> first, () -> second, () -> third, () -> fourth)
        .map((a, b, c, d) -> a + b + c + d);
    assertTrue(seqResult.hasError());
  }

}
