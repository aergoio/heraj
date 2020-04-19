/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.Test;

public class TransportExceptionConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final Throwable[] parameters = {
        new HerajException(""),
        new StatusRuntimeException(Status.UNAVAILABLE),
        new UnsupportedOperationException()
    };
    final Class<?>[] expected = {
        HerajException.class,
        ConnectionException.class,
        HerajException.class
    };

    final TransportExceptionConverter converter = new TransportExceptionConverter();
    for (int i = 0; i < parameters.length; ++i) {
      final Throwable parameter = parameters[i];
      final Class<?> expectedClass = expected[i];
      final HerajException actual = converter.convert(parameter);
      assertEquals(actual.getClass(), expectedClass);
    }
  }

}
