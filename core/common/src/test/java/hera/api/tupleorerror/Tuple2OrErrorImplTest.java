/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.seq;
import static hera.api.tupleorerror.FunctionChain.success;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class Tuple2OrErrorImplTest {

  @Test
  public void testGet() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final Tuple2OrError<String, String> tuple2OrError =
        seq(() -> success(result1), () -> success(result2));
    assertTrue(result1.equals(tuple2OrError.get1()));
    assertTrue(result2.equals(tuple2OrError.get2()));
    assertTrue(!tuple2OrError.hasError());
    assertTrue(null == tuple2OrError.getError());
  }

  @Test(expected = Exception.class)
  public void shouldThrowExceptionOn1stFail() {
    final String result = randomUUID().toString();
    final Tuple2OrError<String, String> tuple2OrError =
        seq(() -> fail(new UnsupportedOperationException()), () -> success(result));
    assertTrue(null != tuple2OrError.getError());
    tuple2OrError.get1();
  }

  @Test(expected = Exception.class)
  public void shouldThrowExceptionOn2ndFail() {
    final String result = randomUUID().toString();
    final Tuple2OrError<String, String> tuple2OrError =
        seq(() -> success(result), () -> fail(new UnsupportedOperationException()));
    assertTrue(null != tuple2OrError.getError());
    tuple2OrError.get2();
  }

  @Test
  public void testIfPresentWithTuples() {
    final String expected1 = randomUUID().toString();
    final String expected2 = randomUUID().toString();
    final Tuple2OrError<String, String> tuple2OrError =
        seq(() -> success(expected1), () -> success(expected2));
    tuple2OrError.ifPresent((actual1, actual2) -> {
      assertEquals(expected1, actual1);
      assertEquals(expected2, actual2);
    });
  }

  @Test
  public void testIfPresentWithError() {
    final String result = randomUUID().toString();
    final Tuple2OrError<String, String> tuple2OrError =
        seq(() -> fail(new UnsupportedOperationException()), () -> success(result));
    assertTrue(null != tuple2OrError.getError());
    tuple2OrError.ifPresent((a, b) -> fail("Should not called"));
  }

  @Test
  public void testFilterGeneratingTuple() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final Tuple2OrError<String, String> expected =
        seq(() -> success(result1), () -> success(result2));
    final Tuple2OrError<String, String> actual =
        expected.filter((a, b) -> a.equals(result1) && b.equals(result2));
    assertEquals(expected, actual);
  }

  @Test
  public void testFilterGeneratingNoTuple() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final Tuple2OrError<String, String> tuple2OrError =
        seq(() -> success(result1), () -> success(result2));
    final Tuple2OrError<String, String> actual =
        tuple2OrError.filter((a, b) -> !a.equals(result1) || !b.equals(result2));
    assertTrue(actual.hasError());
  }

  @Test
  public void testFilterWithError() {
    final String result = randomUUID().toString();
    final Tuple2OrError<String, String> expected =
        seq(() -> success(result), () -> fail(new UnsupportedOperationException()));
    final Tuple2OrError<String, String> actual =
        expected.filter((a, b) -> a.isEmpty() && b.isEmpty());
    assertEquals(expected, actual);
  }

  @Test
  public void testMap() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final Tuple2OrError<String, String> tuple2OrError =
        seq(() -> success(result1), () -> success(result2));
    final int expected = result1.length() + result2.length();
    final int actual = tuple2OrError.map((a, b) -> a.length() + b.length()).getResult();
    assertEquals(expected, actual);
  }

  @Test(expected = Exception.class)
  public void testMapWithError() {
    final String result = randomUUID().toString();
    final Tuple2OrError<String, String> tuple2OrError =
        seq(() -> success(result), () -> fail(new UnsupportedOperationException()));
    tuple2OrError.map((a, b) -> a.length() + b.length()).getResult();
  }

  @Test
  public void testEquals() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final Tuple2OrError<String, String> left = seq(() -> success(result1), () -> success(result2));
    final Tuple2OrError<String, String> right = seq(() -> success(result1), () -> success(result2));
    assertTrue(left.equals(right));
  }

  @Test
  public void testHashCode() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final Tuple2OrError<String, String> tuple2OrError =
        seq(() -> success(result1), () -> success(result2));
    assertTrue(0 != tuple2OrError.hashCode());
  }

}
