/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static org.junit.Assert.assertTrue;

import hera.Context;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class SignLocalAsyncTemplateTest {

  protected Context context = new Context();

  @Test
  public void test() throws Exception {
    final AergoKey key = new AergoKeyGenerator().create();
    final SignLocalAsyncTemplate signLocalAsyncTemplate = new SignLocalAsyncTemplate(context);

    final Transaction transaction = new Transaction();
    final boolean result = signLocalAsyncTemplate.sign(key, transaction).flatMap(s -> {
      transaction.setSignature(s);
      return signLocalAsyncTemplate.verify(key, transaction);
    }).get().getResult();
    assertTrue(result);
  }

}
