/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static org.slf4j.LoggerFactory.getLogger;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.exception.HerajException;
import hera.spec.AergoSpec;
import hera.util.NumberUtils;
import hera.util.pki.ECDSAKeyGenerator;
import java.security.PublicKey;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class AddressResolver {

  protected static final Logger logger = getLogger(AddressResolver.class);

  /**
   * Recover public key from {@code accountAddress}.
   *
   * @param accountAddress an account address
   * @return an extracted public key
   */
  public static PublicKey recoverPublicKey(final AccountAddress accountAddress) {
    try {
      logger.trace("Recover public key from {}", accountAddress);
      final byte[] rawAddress = accountAddress.getBytesValue().getValue();
      return new ECDSAKeyGenerator().createPublicKey(rawAddress);
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  /**
   * Derive an {@link AccountAddress} from a public key.
   *
   * @param publicKey a public key
   * @return an {@link AccountAddress}
   */
  public static AccountAddress deriveAddress(final PublicKey publicKey) {
    try {
      logger.trace("derive account address from {}", publicKey);
      final byte[] rawAddress = new byte[AccountAddress.ADDRESS_BYTE_LENGTH];
      final org.bouncycastle.jce.interfaces.ECPublicKey ecPublicKey =
          (org.bouncycastle.jce.interfaces.ECPublicKey) publicKey;
      rawAddress[0] =
          (byte) (ecPublicKey.getQ().getYCoord().toBigInteger().testBit(0) ? 0x03 : 0x02);
      final byte[] xbyteArray =
          NumberUtils.positiveToByteArray(ecPublicKey.getQ().getXCoord().toBigInteger());
      System.arraycopy(xbyteArray, 0, rawAddress, rawAddress.length - xbyteArray.length,
          xbyteArray.length);
      return new AccountAddress(BytesValue.of(rawAddress));
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

}
