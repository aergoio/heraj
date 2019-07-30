/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BigNumber;
import hera.spec.AergoSpec;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class BigNumberResolver {

  protected static final Logger logger = getLogger(BigNumberResolver.class);

  public static String toJsonForm(final BigNumber bigNumber) {
    return "{ \"" + AergoSpec.BIGNUM_JSON_KEY + "\": \"" + bigNumber.getValue() + "\" }";
  }

  /**
   * Convert aergo bignum in java {@link Map} form into {@link BigNumber} instance.
   *
   * @param bigNumberAndValue a bignum in java map form
   * @return converted {@link BigNumber} instance
   */
  public static BigNumber fromMap(final Map<String, String> bigNumberAndValue) {
    if (1 != bigNumberAndValue.size()) {
      throw new IllegalArgumentException("Map size must be 1");
    }

    BigNumber ret = null;
    for (final Entry<String, String> element : bigNumberAndValue.entrySet()) {
      final String key = element.getKey();
      final String value = element.getValue();
      if (!AergoSpec.BIGNUM_JSON_KEY.equals(key)) {
        throw new IllegalArgumentException("Map key must be " + AergoSpec.BIGNUM_JSON_KEY);
      }
      ret = new BigNumber(value);
    }

    return ret;
  }

}
