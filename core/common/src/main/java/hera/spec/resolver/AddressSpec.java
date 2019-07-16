/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class AddressSpec {

  public static final byte PREFIX = 0x42;

  // [odd|even] of publickey.y + [optional 0x00] + publickey.x
  // which is equivalent with s compressed public key (see also X9.62 s 4.2.1)
  public static final int LENGTH = 33;

  /**
   * An unit to represent aergo. Consist of {@code AER, GAER, AERGO}.
   */
  @RequiredArgsConstructor
  public enum Unit {
    AER("aer", new BigDecimal("1"), new BigDecimal("1")),
    GAER("gaer", new BigDecimal("1.E-9"), new BigDecimal("1.E9")),
    AERGO("aergo", new BigDecimal("1.E-18"), new BigDecimal("1.E18"));

    @Getter
    protected final String name;

    @Getter
    protected final BigDecimal minimum;

    @Getter
    protected final BigDecimal timesToAer;
  }

}
