/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import static org.slf4j.LoggerFactory.getLogger;

import hera.util.HexUtils;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;

public class RFC6979Utils {

  protected static final Logger logger = getLogger(RFC6979Utils.class);

  /**
   * Generate k according to rfc6979.
   *
   * @see <a href="https://tools.ietf.org/html/rfc6979#section-3.2">rfc6979 section-3.2</a>
   *
   * @param d D value of private key
   * @param n order of the DSA generator used in the signature
   * @param mac mac algorithm
   * @param message message data
   * @return generated k
   */
  public static BigInteger generatek(final BigInteger d, final BigInteger n, final Mac mac,
      final byte[] message) throws Exception {

    int qlen = n.bitLength();
    int holen = mac.getMacLength(); // hash octets length?
    int rolen = (qlen + 7) >>> 3;

    logger.trace("qlen: {}", qlen);
    logger.trace("holen: {}", holen);
    logger.trace("rolen: {}", rolen);

    byte[] bx = concat(int2octets(d, rolen), bits2octets(message, n, rolen));
    logger.trace("bx: {}", HexUtils.encode(bx));

    final ByteArrayOutputStream os = new ByteArrayOutputStream();

    // step b.
    byte[] u = new byte[holen];
    for (int i = 0; i < holen; i++) {
      u[i] = 0x01;
    }
    logger.trace("Step B: {}", HexUtils.encode(u));

    // step c
    byte[] k = new byte[holen];
    logger.trace("Step C: {}", HexUtils.encode(k));

    // step d
    os.reset();
    os.write(u);
    os.write((byte) 0x00);
    os.write(bx);
    os.flush();
    k = generateHash(mac, k, os.toByteArray());
    logger.trace("Step D: {}", HexUtils.encode(k));

    // step e
    u = generateHash(mac, k, u);
    logger.trace("Step E: {}", HexUtils.encode(u));

    // step f
    os.reset();
    os.write(u);
    os.write((byte) 0x01);
    os.write(bx);
    k = generateHash(mac, k, os.toByteArray());
    logger.trace("Step F: {}", HexUtils.encode(k));

    // step g
    u = generateHash(mac, k, u);
    logger.trace("Step G: {}", HexUtils.encode(u));

    // step h
    byte[] t = new byte[rolen];
    while (true) {
      /*
       * We want qlen bits, but we support only hash functions with an output length multiple of
       * 8;acd hence, we will gather rlen bits, i.e., rolen octets.
       */
      int toff = 0;
      while (toff < rolen) {
        u = generateHash(mac, k, u);
        int cc = Math.min(u.length, t.length - toff);
        System.arraycopy(u, 0, t, toff, cc);
        toff += cc;
      }
      BigInteger generatedK = bits2int(t, qlen);
      if (generatedK.signum() > 0 && generatedK.compareTo(n) < 0) {
        logger.trace("Generted k: {}", generatedK);
        return generatedK;
      }

      /*
       * k is not in the proper range; update K and V, and loop.
       */

      os.reset();
      os.write(u);
      os.write((byte) 0x00);
      os.flush();
      k = generateHash(mac, k, os.toByteArray());

      u = generateHash(mac, k, u);
    }
  }

  protected static byte[] generateHash(final Mac mac, final byte[] key, byte[] message)
      throws NoSuchAlgorithmException, InvalidKeyException {
    final String algorithm = mac.getAlgorithm();
    final Mac macClone = Mac.getInstance(algorithm);
    macClone.init(new SecretKeySpec(key, algorithm));
    macClone.update(message);
    return macClone.doFinal();
  }

  /**
   * Converts a hash value to an integer.
   *
   * @see <a href="https://tools.ietf.org/html/rfc6979#section-2.3.2">rfc6979 section-2.3.2</a>
   *
   * @param hash input hash
   * @param nbitslen bit length of subgroup order n of the base point
   * @return converted integer
   */
  public static BigInteger bits2int(byte[] hash, int nbitslen) {
    BigInteger v = new BigInteger(1, hash);
    int vbitslen = hash.length * 8;
    if (vbitslen > nbitslen) {
      v = v.shiftRight(vbitslen - nbitslen);
    }
    return v;
  }

  /**
   * Convert integer to octet string.
   *
   * @see <a href="https://tools.ietf.org/html/rfc6979#section-2.3.3">rfc6979 section-2.3.3</a>
   *
   * @param v integer value
   * @param rolen (qlen + 7) / 8 where qlen = n.bitLength, byte length of n
   * @return converted octec string
   */
  public static byte[] int2octets(final BigInteger v, final int rolen) {
    byte[] out = v.toByteArray();

    // left pad with zeros if it's too short
    if (out.length < rolen) {
      byte[] out2 = new byte[rolen];
      System.arraycopy(out, 0, out2, rolen - out.length, out.length);
      return out2;
    }

    // drop most significant bytes if it's too long
    if (out.length > rolen) {
      byte[] out2 = new byte[rolen];
      System.arraycopy(out, out.length - rolen, out2, 0, rolen);
      return out2;
    }

    return out;
  }

  /**
   * Convert bit string to octet string.
   *
   * @see <a href="https://tools.ietf.org/html/rfc6979#section-2.3.4">rfc6979 section-2.3.4</a>
   *
   * @param in input bits
   * @param n subgroup order of the base point
   * @param rolen (qlen + 7) / 8 where qlen = n.bigLength, byte length of n
   * @return
   */
  public static byte[] bits2octets(byte[] in, BigInteger n, final int rolen) {
    BigInteger z1 = bits2int(in, n.bitLength());
    BigInteger z2 = z1.subtract(n);
    return int2octets(z2.signum() < 0 ? z1 : z2, rolen);
  }

  /**
   * Concat two arrays.
   *
   * @param a first array
   * @param b second array
   * @return a concated array
   */
  public static byte[] concat(final byte[] a, final byte[] b) {
    byte[] concated = new byte[a.length + b.length];
    System.arraycopy(a, 0, concated, 0, a.length);
    System.arraycopy(b, 0, concated, a.length, b.length);
    return concated;
  }

}
