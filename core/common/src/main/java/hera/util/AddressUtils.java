/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.EncodingUtils.decodeHexa;

import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Identity;
import hera.exception.HerajException;
import hera.spec.AddressSpec;

public class AddressUtils {

  /**
   * Derive an {@link AccountAddress} from an identity.
   *
   * @param identity an identity
   * @return an {@link AccountAddress}
   */
  public static AccountAddress deriveAddress(final Identity identity) {
    if (identity instanceof AccountAddress) {
      return (AccountAddress) identity;
    }

    try {
      final String encoded = identity.getInfo();
      final BytesValue decoded = decodeHexa(encoded);
      final AccountAddress derived = AccountAddress.of(decoded);
      if ((derived.getBytesValue().getValue().length - 1) != AddressSpec.LENGTH) {
        throw new HerajException("Invalid identity of account address");
      }
      return derived;
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

}
