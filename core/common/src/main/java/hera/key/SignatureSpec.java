/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.Signature;
import hera.exception.HerajException;
import hera.util.HexUtils;
import hera.util.Pair;
import hera.util.pki.ECDSAKeyGenerator;
import hera.util.pki.ECDSASignature;
import java.math.BigInteger;
import java.util.Arrays;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class SignatureSpec {

  public static final int SIGN_HEADER_MAGIC = 0x30;

  public static final int SIGN_INT_MARKER = 0x02;

  // minimum length of a DER encoded signature which both R and S are 1 byte each.
  // <header-magic> + <1-byte> + <int-marker> + 0x01 + <r.byte> + <int-marker> + 0x01 + <s.byte>
  public static final int SIGN_MINIMUM_LENGTH = 8;

  public static final BigInteger ecParamsN = ECDSAKeyGenerator.ecParams.getN();

  protected static final Logger logger = getLogger(SignatureSpec.class);

  /**
   * Serialize ecdsa signature.
   * 
   * @param ecdsaSignature a ecdsa signature
   * @return serialized signature
   */
  public static Signature serialize(final ECDSASignature ecdsaSignature) {
    assertNotNull(ecdsaSignature, "ECDSASignature must not null");

    final BigInteger order = ecParamsN;
    final BigInteger halfOrder = order.divide(BigInteger.valueOf(2L));

    final BigInteger r = ecdsaSignature.getR();
    BigInteger s = ecdsaSignature.getS();
    if (s.compareTo(halfOrder) > 0) {
      s = order.subtract(s);
    }

    // in this case, use canonical byte array, not raw bytes
    final byte[] rbyteArray = r.toByteArray();
    final byte[] sbyteArray = s.toByteArray();
    if (logger.isTraceEnabled()) {
      logger.trace("Canonical r: {}, len: {}", HexUtils.encode(rbyteArray), rbyteArray.length);
      logger.trace("Canonical s: {}, len: {}", HexUtils.encode(sbyteArray), sbyteArray.length);
    }

    final byte[] serialized = new byte[6 + rbyteArray.length + sbyteArray.length];

    // Header
    serialized[0] = SIGN_HEADER_MAGIC;
    serialized[1] = (byte) (serialized.length - 2);

    // <int-marker> + <R.length> + <R.bytes>
    serialized[2] = SIGN_INT_MARKER;
    serialized[3] = (byte) rbyteArray.length;
    System.arraycopy(rbyteArray, 0, serialized, 4, rbyteArray.length);

    // <int-marker> + <S.length> + <S.bytes>
    final int offset = 4 + rbyteArray.length;
    serialized[offset] = SIGN_INT_MARKER;
    serialized[offset + 1] = (byte) sbyteArray.length;
    System.arraycopy(sbyteArray, 0, serialized, offset + 2, sbyteArray.length);

    return Signature.newBuilder().sign(BytesValue.of(serialized)).build();
  }

  /**
   * Parse {@link ECDSASignature} from the serialized one.
   *
   * @param signature an signature
   * @return parsed {@link ECDSASignature}. null if parsing failed.
   */
  public static ECDSASignature deserialize(final Signature signature) {
    assertNotNull(signature, "Signature must not null");
    try {
      final BigInteger order = ecParamsN;
      final byte[] rawSignature = signature.getSign().getValue();
      if (logger.isTraceEnabled()) {
        logger.trace("Raw signature: {}, len: {}", HexUtils.encode(rawSignature),
            rawSignature.length);
      }

      int index = 0;

      if (rawSignature.length < SIGN_MINIMUM_LENGTH) {
        throw new HerajException(
            "Invalid serialized length: length is shorter than "
                + SIGN_MINIMUM_LENGTH);
      }

      index = validateHeader(rawSignature, index);

      final Pair<BigInteger, Integer> rAndIndex = parseInteger(rawSignature, index, order);
      final BigInteger r = rAndIndex.v1;
      index = rAndIndex.v2;

      final Pair<BigInteger, Integer> sAndIndex = parseInteger(rawSignature, index, order);
      final BigInteger s = sAndIndex.v1;
      index = sAndIndex.v2;

      if (index < rawSignature.length) {
        throw new HerajException(
            "Invalid length of r or s, still ramains bytes after parsing. index: " + index
                + ", length: " + rawSignature.length);
      }
      return ECDSASignature.of(r, s);
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  protected static int validateHeader(final byte[] source, final int start) {
    int index = start;

    if (source[index] != SIGN_HEADER_MAGIC) {
      throw new IllegalStateException(
          "Invalid magic number. expected: " + SIGN_HEADER_MAGIC + ", but was: "
              + source[index]);
    }
    ++index;

    int sigDataLen = source[index];
    if (sigDataLen < SIGN_MINIMUM_LENGTH || (source.length - 2) < sigDataLen) {
      throw new IllegalStateException("Invalid signature length");
    }
    ++index;

    return index;
  }

  protected static Pair<BigInteger, Integer> parseInteger(final byte[] source, final int start,
      final BigInteger order) {
    int index = start;

    // parse marker
    if (source[index] != SIGN_INT_MARKER) {
      throw new IllegalStateException(
          "Invalid integer header. expected: " + SIGN_INT_MARKER + ", but was: "
              + source[index]);
    }
    ++index;

    // parse integer length
    final int length = source[index];
    ++index;

    // parse integer
    byte[] rawInteger = Arrays.copyOfRange(source, index, index + length);
    final BigInteger r = new BigInteger(rawInteger);
    if (r.compareTo(order) >= 0) {
      throw new IllegalStateException("Integer is greater then curve order");
    }
    index += length;

    return new Pair<BigInteger, Integer>(r, index);
  }

}
