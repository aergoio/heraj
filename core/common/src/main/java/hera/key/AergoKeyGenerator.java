/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.HerajException;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import hera.util.pki.KeyGenerator;

@ApiAudience.Public
@ApiStability.Unstable
public class AergoKeyGenerator implements KeyGenerator<AergoKey> {

  protected final ECDSAKeyGenerator ecdsaKeyGenerator = new ECDSAKeyGenerator();

  /**
   * Create an {@code Aergokey}.
   *
   * @return created {@code AergoKey}
   */
  @Override
  public AergoKey create() {
    try {
      final ECDSAKey ecdsaKey = ecdsaKeyGenerator.create();
      return new AergoKey(ecdsaKey);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  /**
   * Create an {@code Aergokey} with a seed.
   *
   * @param seed a seed to create aergo key
   *
   * @return created {@code AergoKey}
   */
  public AergoKey create(final String seed) {
    try {
      final ECDSAKey ecdsaKey = ecdsaKeyGenerator.create(seed);
      return new AergoKey(ecdsaKey);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

}
