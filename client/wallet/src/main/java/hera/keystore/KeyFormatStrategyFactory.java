/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class KeyFormatStrategyFactory {

  protected static List<String> supported;

  static {
    List<String> list = new ArrayList<>();
    list.add("1");
    supported = Collections.unmodifiableList(list);
  }

  /**
   * Create keyformat strategy for a {@code version}. Current supported version is ("v1").
   *
   * @param version a keyformat version
   * @return a keyformat strategy instance
   */
  public KeyFormatStrategy create(final String version) {
    switch (version) {
      case "v1":
        return new KeyStoreV1Strategy();
      default:
        return new KeyStoreV1Strategy();
    }
  }

  public List<String> listSupportedVersion() {
    return supported;
  }

}
