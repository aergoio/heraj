/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.transaction;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.api.model.ContractInvocation;
import org.junit.Test;

public class TransactionInfoExtractorFactoryTest extends AbstractTestCase {

  @Test
  public void testCreate() {
    final Class<?>[] possibleClasses = new Class<?>[] {
        ContractInvocation.class
    };

    for (int i = 0; i < possibleClasses.length; ++i) {
      final Class<?> target = possibleClasses[i];
      final TransactionInfoExtractor<?> extractor =
          new TransactionInfoExtractorFactory().create(target);
      assertNotNull(extractor);
    }
  }

  @Test
  public void shouldThrowErrorOnUnSupportedType() {
    try {
      // when
      new TransactionInfoExtractorFactory().create(Object.class);
      fail();
    } catch (Exception e) {
      // then
    }
  }

}
