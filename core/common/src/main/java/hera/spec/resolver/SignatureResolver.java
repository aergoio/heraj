/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.BytesValue;
import hera.api.model.Signature;
import hera.exception.HerajException;
import hera.spec.SignatureSpec;
import hera.util.HexUtils;
import hera.util.Pair;
import hera.util.pki.ECDSASignature;
import java.math.BigInteger;
import java.util.Arrays;
import org.slf4j.Logger;

public class SignatureResolver {

  protected final transient Logger logger = getLogger(getClass());

  /**
   * Serialize ecdsa signature.
   * 
   * @param signature a ecdsa signature
   * @param order an order of the sign key
   * @return serialized signature
   */
  public BytesValue serialize(final ECDSASignature signature, final BigInteger order) {
    final BigInteger halfOrder = order.divide(BigInteger.valueOf(2L));

    final BigInteger r = signature.getR();
    BigInteger s = signature.getS();
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
    serialized[0] = SignatureSpec.HEADER_MAGIC;
    serialized[1] = (byte) (serialized.length - 2);

    // <int-marker> + <R.length> + <R.bytes>
    serialized[2] = SignatureSpec.INT_MARKER;
    serialized[3] = (byte) rbyteArray.length;
    System.arraycopy(rbyteArray, 0, serialized, 4, rbyteArray.length);

    // <int-marker> + <S.length> + <S.bytes>
    final int offset = 4 + rbyteArray.length;
    serialized[offset] = SignatureSpec.INT_MARKER;
    serialized[offset + 1] = (byte) sbyteArray.length;
    System.arraycopy(sbyteArray, 0, serialized, offset + 2, sbyteArray.length);

    return new BytesValue(serialized);
  }

  /**
   * Parse {@link ECDSASignature} from the serialized one.
   *
   * @param signature serialized ecdsa signature
   * @param order a order of signature key
   * @return parsed {@link ECDSASignature}. null if parsing failed.
   */
  public ECDSASignature parse(final Signature signature, final BigInteger order) {
    if (null == signature) {
      throw new HerajException("Serialized signature is null");
    }

    final byte[] rawSignature = signature.getSign().getValue();
    if (logger.isTraceEnabled()) {
      logger.trace("Raw signature: {}, len: {}", HexUtils.encode(rawSignature),
          rawSignature.length);
    }

    int index = 0;

    if (rawSignature.length < SignatureSpec.MINIMUM_SIGNATURE_LEN) {
      throw new HerajException(
          "Invalid serialized length: length is shorter than "
              + SignatureSpec.MINIMUM_SIGNATURE_LEN);
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
  }

  protected int validateHeader(final byte[] source, final int start) {
    int index = start;

    if (source[index] != SignatureSpec.HEADER_MAGIC) {
      throw new HerajException(
          "Invalid magic number. expected: " + SignatureSpec.HEADER_MAGIC + ", but was: "
              + source[index]);
    }
    ++index;

    int sigDataLen = source[index];
    if (sigDataLen < SignatureSpec.MINIMUM_SIGNATURE_LEN || (source.length - 2) < sigDataLen) {
      throw new HerajException("Invalid signature length");
    }
    ++index;

    return index;
  }

  protected Pair<BigInteger, Integer> parseInteger(final byte[] source, final int start,
      final BigInteger order) {
    int index = start;

    // parse marker
    if (source[index] != SignatureSpec.INT_MARKER) {
      throw new HerajException(
          "Invalid integer header. expected: " + SignatureSpec.INT_MARKER + ", but was: "
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
      throw new HerajException("Integer is greater then curve order");
    }
    index += length;

    return new Pair<BigInteger, Integer>(r, index);
  }


}
