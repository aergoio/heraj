/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class ResultOrErrorImplTest {

  @Test
  public void testGetResult() {
    final String result = randomUUID().toString();
    final ResultOrError<String> resultOrError = success(result);
    assertTrue(resultOrError.hasResult());
    assertTrue(result.equals(resultOrError.getResult()));
    assertTrue(!resultOrError.hasError());
    assertTrue(null == resultOrError.getError());
  }

  @Test(expected = Exception.class)
  public void shouldThrowExceptionWhenNoResult() {
    final ResultOrError<String> resultOrError = fail(new UnsupportedOperationException());
    resultOrError.getResult();
  }

  @Test
  public void testGetError() {
    final UnsupportedOperationException error = new UnsupportedOperationException();
    final ResultOrError<String> resultOrError = fail(error);
    assertTrue(!resultOrError.hasResult());
    assertTrue(resultOrError.hasError());
    assertTrue(error.equals(resultOrError.getError()));
  }

  @Test
  public void testIfPresent() {
    final String result = randomUUID().toString();
    final ResultOrError<String> resultOrError = success(result);
    resultOrError.ifPresent(actual -> assertEquals(result, actual));
  }

  @Test
  public void testIfPresentWithError() {
    final ResultOrError<String> resultOrError = fail(new UnsupportedOperationException());
    resultOrError.ifPresent(actual -> fail("Should not called"));
  }

  @Test
  public void testFilterGeneratingResult() {
    final String result = randomUUID().toString();
    final ResultOrError<String> resultOrError = success(result);
    resultOrError.filter(s -> s.equals(result)).getResult();
  }

  @Test(expected = Exception.class)
  public void testFilterGeneratingNoResult() {
    final String result = randomUUID().toString();
    final ResultOrError<String> resultOrError = success(result);
    resultOrError.filter(s -> !s.equals(result)).getResult();
  }

  @Test
  public void testFilterWithError() {
    final ResultOrError<String> expected = fail(new UnsupportedOperationException());
    final ResultOrError<String> actual = expected.filter(s -> s.isEmpty());
    assertEquals(expected, actual);
  }

  @Test
  public void testMap() {
    final String result = randomUUID().toString();
    final ResultOrError<String> resultOrError = success(result);
    final int expected = result.length();
    final int actual = resultOrError.map(s -> s.length()).getResult();
    assertEquals(expected, actual);
  }

  @Test(expected = Exception.class)
  public void testMapWithError() {
    final ResultOrError<String> resultOrError = fail(new UnsupportedOperationException());
    resultOrError.map(s -> s.length()).getResult();
  }

  @Test
  public void testFlatMap() {
    final String expected = randomUUID().toString();
    final ResultOrError<String> resultOrError = success(randomUUID().toString());
    final String actual = resultOrError.flatMap(s -> success(expected)).getResult();
    assertEquals(expected, actual);
  }

  @Test(expected = Exception.class)
  public void testFlatMapWithError() {
    final ResultOrError<String> resultOrError = fail(new UnsupportedOperationException());
    resultOrError.flatMap(s -> fail(new UnsupportedOperationException())).getResult();
  }

  @Test
  public void testOrElseWithResult() {
    final String expected = randomUUID().toString();
    final String alternative = randomUUID().toString();
    final ResultOrError<String> resultOrError = success(expected);
    final String actual = resultOrError.orElse(alternative);
    assertEquals(expected, actual);
  }

  @Test
  public void testOrElseWithError() {
    final String expected = randomUUID().toString();
    final ResultOrError<String> resultOrError = fail(new UnsupportedOperationException());
    final String actual = resultOrError.orElse(expected);
    assertEquals(expected, actual);
  }

  @Test
  public void testOrElseGetWithResult() {
    final String expected = randomUUID().toString();
    final String alternative = randomUUID().toString();
    final ResultOrError<String> resultOrError = success(expected);
    final String actual = resultOrError.orElseGet(() -> alternative);
    assertEquals(expected, actual);
  }

  @Test
  public void testOrElseGetWithError() {
    final String expected = randomUUID().toString();
    final ResultOrError<String> resultOrError = fail(new UnsupportedOperationException());
    final String actual = resultOrError.orElseGet(() -> expected);
    assertEquals(expected, actual);
  }

  @Test
  public void testGetOrThrowsWithResult() {
    final String expected = randomUUID().toString();
    final ResultOrError<String> resultOrError = success(expected);
    final String actual = resultOrError.getOrThrows(() -> new IllegalStateException());
    assertEquals(expected, actual);
  }

  @Test(expected = IllegalStateException.class)
  public void testGetOrThrowsWithError() {
    final ResultOrError<String> resultOrError = fail(new UnsupportedOperationException());
    resultOrError.getOrThrows(() -> new IllegalStateException());
  }

  @Test
  public void testEquals() {
    final String result = randomUUID().toString();
    final ResultOrError<String> left = success(result);
    final ResultOrError<String> right = success(result);
    assertTrue(left.equals(right));
  }

  @Test
  public void testHashCode() {
    final ResultOrError<String> resultOrError = success(randomUUID().toString());
    assertTrue(0 != resultOrError.hashCode());
  }

}
