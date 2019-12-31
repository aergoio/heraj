/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore.internal;

public class KeyStoreStrategyFactory {

  /**
   * Create keystore strategy for a {@code version}. Current supported version ("v1").
   *
   * @param version a keystore version
   * @return a keystore strategy instance
   */
  public KeyStoreStrategy create(final String version) {
    switch (version) {
      case "v1":
        return new KeyStoreV1Strategy();
      default:
        return new KeyStoreV1Strategy();
    }
  }

}
