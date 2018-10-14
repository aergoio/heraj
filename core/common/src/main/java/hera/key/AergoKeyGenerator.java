/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.util.pki.ECDSAKeyGenerator;

public class AergoKeyGenerator {

  protected final ECDSAKeyGenerator ecdsaKeyGenerator = new ECDSAKeyGenerator();

  public AergoKey create() throws Exception {
    return new AergoKey(ecdsaKeyGenerator.create());
  }

}
