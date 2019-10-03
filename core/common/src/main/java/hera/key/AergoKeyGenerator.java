/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.exception.UnableToGenerateKeyException;
import hera.spec.resolver.EncryptedPrivateKeyResolver;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import hera.util.pki.KeyGenerator;
import java.math.BigInteger;

@ApiAudience.Public
@ApiStability.Unstable
public class AergoKeyGenerator implements KeyGenerator<AergoKey> {

  protected final ECDSAKeyGenerator ecdsaKeyGenerator = new ECDSAKeyGenerator();

  /**
   * Create an {@code Aergokey}.
   *
   * @return created {@code AergoKey}
   * @throws UnableToGenerateKeyException if an error occured in creating key
   */
  @Override
  public AergoKey create() {
    try {
      final ECDSAKey ecdsaKey = ecdsaKeyGenerator.create();
      return new AergoKey(ecdsaKey);
    } catch (Exception e) {
      throw new UnableToGenerateKeyException(e);
    }
  }

  /**
   * Create an {@code Aergokey} with a seed.
   *
   * @param seed a seed to create aergo key
   *
   * @return created {@code AergoKey}
   * @throws UnableToGenerateKeyException if an error occured in creating key
   */
  @Override
  public AergoKey create(final String seed) {
    try {
      final ECDSAKey ecdsaKey = ecdsaKeyGenerator.create(seed);
      return new AergoKey(ecdsaKey);
    } catch (Exception e) {
      throw new UnableToGenerateKeyException(e);
    }
  }

  /**
   * Create an {@code Aergokey}.
   *
   * @param encrypted an encrypted private key
   * @param password a password to decrypt
   *
   * @return created {@code AergoKey}
   * @throws UnableToGenerateKeyException if an error occured in creating key
   */
  public AergoKey create(final String encrypted, final String password) {
    return create(EncryptedPrivateKey.of(encrypted), password);
  }

  /**
   * Create an {@code Aergokey}.
   *
   * @param encrypted an encrypted private key
   * @param password a password to decrypt
   *
   * @return created {@code AergoKey}
   * @throws UnableToGenerateKeyException if an error occured in creating key
   */
  public AergoKey create(final EncryptedPrivateKey encrypted, final String password) {
    try {
      final BytesValue decrypted = EncryptedPrivateKeyResolver.decrypt(encrypted, password);
      final ECDSAKey ecdsaKey =
          new ECDSAKeyGenerator().create(new BigInteger(1, decrypted.getValue()));
      return new AergoKey(ecdsaKey);
    } catch (Exception e) {
      throw new UnableToGenerateKeyException(e);
    }
  }

}
