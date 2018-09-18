/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import hera.AergoKey;
import hera.AergoKeyGenerator;
import hera.api.model.BytesValue;
import org.junit.Test;

public class SignTemplateTest extends AbstractTestCase {

  @Test
  public void test() throws Exception {
    final BytesValue plainText = new BytesValue(randomUUID().toString().getBytes());
    final AergoKey key = new AergoKeyGenerator().create();
    final SignTemplate signTemplate = new SignTemplate();
    final BytesValue bytesValue = signTemplate.sign(key, plainText);

    assertTrue(signTemplate.verify(key, plainText, bytesValue));
  }

}