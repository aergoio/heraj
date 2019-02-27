/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.EncodingUtils.decodeHexa;

import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Identity;
import hera.exception.HerajException;
import java.security.PublicKey;

public class AddressUtils {

  // [odd|even] of publickey.y + [optional 0x00] + publickey.x
  public static final int ADDRESS_LENGTH = 33;

  /**
   * Derive an {@link AccountAddress} from a public key.
   *
   * @param publicKey a public key
   * @return an {@link AccountAddress}
   */
  public static AccountAddress deriveAddress(final PublicKey publicKey) {
    final byte[] rawAddress = new byte[ADDRESS_LENGTH];
    final org.bouncycastle.jce.interfaces.ECPublicKey ecPublicKey =
        (org.bouncycastle.jce.interfaces.ECPublicKey) publicKey;
    rawAddress[0] = (byte) (ecPublicKey.getQ().getYCoord().toBigInteger().testBit(0) ? 0x03 : 0x02);
    final byte[] xbyteArray =
        NumberUtils.positiveToByteArray(ecPublicKey.getQ().getXCoord().toBigInteger());
    System.arraycopy(xbyteArray, 0, rawAddress, rawAddress.length - xbyteArray.length,
        xbyteArray.length);
    return AccountAddress
        .of(BytesValue.of(VersionUtils.envelop(rawAddress, AccountAddress.VERSION)));
  }

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
      if ((derived.getBytesValue().getValue().length - 1) != ADDRESS_LENGTH) {
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
