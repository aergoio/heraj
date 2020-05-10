/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public class KeyStores {

  /**
   * Create an new in-memory keystore.
   *
   * @return an in-memory keystore
   */
  public static KeyStore newInMemoryKeyStore() {
    return new InMemoryKeyStore();
  }

  /**
   * Create aergo keystore with root directory {@code keyStoreDir}.
   *
   * @param root a keystore root directory
   * @return an aergo keystore
   */
  public static KeyStore newAergoKeyStore(final String root) {
    return new AergoKeyStore(root);
  }

  /**
   * Create a keystore which uses {@link java.security.KeyStore}.
   *
   * @param delegate a java keystore
   * @return a java keystore
   */
  public static KeyStore newJavaKeyStore(final java.security.KeyStore delegate) {
    return new JavaKeyStore(delegate);
  }

  private KeyStores() {

  }

}
