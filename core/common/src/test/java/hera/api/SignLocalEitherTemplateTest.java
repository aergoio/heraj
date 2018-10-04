/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static org.junit.Assert.assertTrue;

import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class SignLocalEitherTemplateTest {

  @Test
  public void test() throws Exception {
    final AergoKey key = new AergoKeyGenerator().create();
    final SignLocalEitherTemplate signLocalEitherTemplate = new SignLocalEitherTemplate(key);

    final Transaction transaction = new Transaction();
    final Signature signature = signLocalEitherTemplate.sign(transaction).getResult();
    transaction.setSignature(signature);
    assertTrue(signLocalEitherTemplate.verify(transaction).getResult());
  }

}
