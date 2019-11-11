/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.exception.WalletException;
import hera.exception.WalletExceptionConverter;
import org.junit.Test;

public class WalletExceptionConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final Throwable[] parameters = {
        new WalletException(""),
        new UnsupportedOperationException()
    };
    final Class<?>[] expected = {
        WalletException.class,
        WalletException.class
    };

    final WalletExceptionConverter converter = new WalletExceptionConverter();
    for (int i = 0; i < parameters.length; ++i) {
      final Throwable parameter = parameters[i];
      final Class<?> expectedClass = expected[i];
      final WalletException actual = converter.convert(parameter);
      assertEquals(actual.getClass(), expectedClass);
    }
  }

}
