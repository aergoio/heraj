/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import hera.util.DangerousSupplier;
import java.io.InputStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LuaBinary {
  @Getter
  protected final DangerousSupplier<InputStream> inputSupplier;

  public InputStream getPayload() throws Exception {
    return inputSupplier.get();
  }
}
