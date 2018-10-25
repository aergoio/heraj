/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.exception.UnableToGenerateKeyException;
import hera.util.pki.ECDSAKeyGenerator;
import hera.util.pki.KeyGenerator;

public class AergoKeyGenerator implements KeyGenerator<AergoKey> {

  protected final ECDSAKeyGenerator ecdsaKeyGenerator = new ECDSAKeyGenerator();

  /**
   * Create an {@code Aergokey}.
   *
   * @return created {@code AergoKey}
   * @throws UnableToGenerateKeyException if an error occured in creating key
   */
  public AergoKey create() {
    try {
      return new AergoKey(ecdsaKeyGenerator.create());
    } catch (Exception e) {
      throw new UnableToGenerateKeyException(e);
    }
  }

}
