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
public class Tuple3OrErrorImplTest {

  @Test
  public void testGet() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final String result3 = randomUUID().toString();
    final Tuple3OrError<String, String, String> tuple3OrError =
        seq(() -> success(result1), () -> success(result2), () -> success(result3));
    assertTrue(result1.equals(tuple3OrError.get1()));
    assertTrue(result2.equals(tuple3OrError.get2()));
    assertTrue(result3.equals(tuple3OrError.get3()));
    assertTrue(!tuple3OrError.hasError());
    assertTrue(null == tuple3OrError.getError());
  }

  @Test(expected = Exception.class)
  public void shouldThrowExceptionOn1stFail() {
    final String result = randomUUID().toString();
    final Tuple3OrError<String, String, String> tuple3OrError =
        seq(() -> fail(new UnsupportedOperationException()), () -> success(result),
            () -> success(result));
    assertTrue(null != tuple3OrError.getError());
    tuple3OrError.get1();
  }

  @Test(expected = Exception.class)
  public void shouldThrowExceptionOn2ndFail() {
    final String result = randomUUID().toString();
    final Tuple3OrError<String, String, String> tuple3OrError = seq(() -> success(result),
        () -> fail(new UnsupportedOperationException()), () -> success(result));
    assertTrue(null != tuple3OrError.getError());
    tuple3OrError.get2();
  }

  @Test(expected = Exception.class)
  public void shouldThrowExceptionOn3rdFail() {
    final String result = randomUUID().toString();
    final Tuple3OrError<String, String, String> tuple3OrError = seq(() -> success(result),
        () -> success(result), () -> fail(new UnsupportedOperationException()));
    assertTrue(null != tuple3OrError.getError());
    tuple3OrError.get3();
  }

  @Test
  public void testIfPresentWithTuples() {
    final String expected1 = randomUUID().toString();
    final String expected2 = randomUUID().toString();
    final String expected3 = randomUUID().toString();
    final Tuple3OrError<String, String, String> tuple3OrError =
        seq(() -> success(expected1), () -> success(expected2), () -> success(expected3));
    tuple3OrError.ifPresent((actual1, actual2, actual3) -> {
      assertEquals(expected1, actual1);
      assertEquals(expected2, actual2);
      assertEquals(expected3, actual3);
    });
  }

  @Test
  public void testIfPresentWithError() {
    final String result = randomUUID().toString();
    final Tuple3OrError<String, String, String> tuple3OrError =
        seq(() -> fail(new UnsupportedOperationException()), () -> success(result),
            () -> success(result));
    assertTrue(null != tuple3OrError.getError());
    tuple3OrError.ifPresent((a, b, c) -> fail("Should not called"));
  }

  @Test
  public void testFilterGeneratingTuple() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final String result3 = randomUUID().toString();
    final Tuple3OrError<String, String, String> expected =
        seq(() -> success(result1), () -> success(result2), () -> success(result3));
    final Tuple3OrError<String, String, String> actual =
        expected.filter((a, b, c) -> a.equals(result1) && b.equals(result2) && c.equals(result3));
    assertEquals(expected, actual);
  }

  @Test
  public void testFilterGeneratingNoTuple() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final String result3 = randomUUID().toString();
    final Tuple3OrError<String, String, String> tuple3OrError =
        seq(() -> success(result1), () -> success(result2), () -> success(result3));
    final Tuple3OrError<String, String, String> actual = tuple3OrError
        .filter((a, b, c) -> !a.equals(result1) || !b.equals(result2) || !c.equals(result3));
    assertTrue(actual.hasError());
  }

  @Test
  public void testFilterWithError() {
    final String result = randomUUID().toString();
    final Tuple3OrError<String, String, String> expected = seq(() -> success(result),
        () -> success(result), () -> fail(new UnsupportedOperationException()));
    final Tuple3OrError<String, String, String> actual =
        expected.filter((a, b, c) -> a.isEmpty() && b.isEmpty() && c.isEmpty());
    assertEquals(expected, actual);
  }

  @Test
  public void testFilterWithErrorOnNext() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final String result3 = randomUUID().toString();
    final Tuple3OrError<String, String, String> base =
        seq(() -> success(result1), () -> success(result2), () -> success(result3));
    final Tuple3OrError<String, String, String> actual = base.filter((a, b, c) -> {
      throw new UnsupportedOperationException();
    });
    assertTrue(actual.hasError());
  }

  @Test
  public void testMap() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final String result3 = randomUUID().toString();
    final Tuple3OrError<String, String, String> tuple3OrError =
        seq(() -> success(result1), () -> success(result2), () -> success(result3));
    final int expected = result1.length() + result2.length() + result3.length();
    final int actual =
        tuple3OrError.map((a, b, c) -> a.length() + b.length() + c.length()).getResult();
    assertEquals(expected, actual);
  }

  @Test(expected = Exception.class)
  public void testMapWithError() {
    final String result = randomUUID().toString();
    final Tuple3OrError<String, String, String> tuple3OrError = seq(() -> success(result),
        () -> success(result), () -> fail(new UnsupportedOperationException()));
    tuple3OrError.map((a, b, c) -> a.length() + b.length() + c.length()).getResult();
  }

  @Test(expected = Exception.class)
  public void testMapWithErrorOnNext() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final String result3 = randomUUID().toString();
    final Tuple3OrError<String, String, String> tuple3OrError =
        seq(() -> success(result1), () -> success(result2), () -> success(result3));
    tuple3OrError.map((a, b, c) -> {
      throw new UnsupportedOperationException();
    }).getResult();
  }

  @Test
  public void testEquals() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final String result3 = randomUUID().toString();
    final Tuple3OrError<String, String, String> left =
        seq(() -> success(result1), () -> success(result2), () -> success(result3));
    final Tuple3OrError<String, String, String> right =
        seq(() -> success(result1), () -> success(result2), () -> success(result3));
    assertTrue(left.equals(right));
  }

  @Test
  public void testHashCode() {
    final String result1 = randomUUID().toString();
    final String result2 = randomUUID().toString();
    final String result3 = randomUUID().toString();
    final Tuple3OrError<String, String, String> tuple3OrError =
        seq(() -> success(result1), () -> success(result2), () -> success(result3));
    assertTrue(0 != tuple3OrError.hashCode());
  }

}
