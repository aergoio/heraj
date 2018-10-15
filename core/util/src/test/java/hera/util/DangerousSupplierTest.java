/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.Test;

public class DangerousSupplierTest extends AbstractTestCase {

  @Test
  public void testToSafe() {
    final DangerousSupplier<Object> dangerousSupplier = () -> randomUUID().toString();
    final Supplier<Object> supplier = dangerousSupplier.toSafe();
    assertNotNull(supplier);
    assertNotNull(supplier.get());
  }

  @Test(expected = IllegalStateException.class)
  public void testToSafeWithException() {
    final DangerousSupplier<Object> dangerousSupplier = () -> {
      throw new UnsupportedOperationException();
    };
    final Supplier<Object> supplier = dangerousSupplier.toSafe();
    assertNotNull(supplier);
    supplier.get();
  }

  @Test
  public void testfrom() {
    final Adaptor adaptor = new Adaptor() {
      @Override
      public <T> Optional<T> adapt(final Class<T> adaptor) {
        return Optional.empty();
      }
    };
    final DangerousSupplier<Object> dangerousSupplier =
        DangerousSupplier.from(adaptor, Object.class);
    assertNotNull(dangerousSupplier);
  }

}
