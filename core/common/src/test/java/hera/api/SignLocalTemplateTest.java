/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static org.junit.Assert.assertTrue;

import hera.Context;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class SignLocalTemplateTest {

  protected Context context = new Context();

  @Test
  public void test() throws Exception {
    final AergoKey key = new AergoKeyGenerator().create();
    final SignLocalTemplate signTemplate = new SignLocalTemplate(context);

    final Transaction transaction = new Transaction();
    final Signature signature = signTemplate.sign(key, transaction);
    transaction.setSignature(signature);
    assertTrue(signTemplate.verify(key, transaction));
  }

}
