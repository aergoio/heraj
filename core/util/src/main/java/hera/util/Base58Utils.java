/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.slf4j.Logger;

/**
 * Base58 utils class. Encoding, decoding logic is copied from bitcoinj.
 */
public class Base58Utils {

  protected static final Logger logger = getLogger(Base58Utils.class);

  protected static final char[] BASE58_CHARS =
      "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();

  protected static final char ENCODED_ZERO = BASE58_CHARS[0];

  protected static final int[] INDEXES = new int[128];

  protected static final int CHECKSUM_LEN = 4;

  static {
    Arrays.fill(INDEXES, -1);
    for (int i = 0; i < BASE58_CHARS.length; i++) {
      INDEXES[BASE58_CHARS[i]] = i;
    }
  }

  /**
   * Encodes the given bytes as a base58 string (no checksum is appended).
   *
   * @param input the bytes to encode.
   * @return the base58-encoded string. empty string if {@code input} is empty or null.
   */
  public static String encode(final byte[] input) {
    if (null == input || input.length == 0) {
      return "";
    }

    final char[] encoded = new char[input.length * 2];
    final byte[] copy = Arrays.copyOf(input, input.length); // since we modify it in-place

    // Count leading zeros.
    int zeros = 0;
    while (zeros < input.length && input[zeros] == 0) {
      ++zeros;
    }

    int inputIndex = zeros;
    int outputIndex = encoded.length;
    while (inputIndex < copy.length) {
      encoded[--outputIndex] = BASE58_CHARS[divmod(copy, inputIndex, 256, 58)];
      if (copy[inputIndex] == 0) {
        ++inputIndex; // optimization - skip leading zeros
      }
    }
    // Preserve exactly as many leading encoded zeros in output as there were leading zeros in
    // input.
    while (outputIndex < encoded.length && encoded[outputIndex] == ENCODED_ZERO) {
      ++outputIndex;
    }
    while (--zeros >= 0) {
      encoded[--outputIndex] = ENCODED_ZERO;
    }
    // Return encoded string (including encoded leading zeros).
    return new String(encoded, outputIndex, encoded.length - outputIndex);
  }

  /**
   * Decodes the given base58 string into the original data bytes.
   *
   * @param input the base58-encoded string to decode
   * @return the decoded data bytes. empty string if {@code input} is empty or null.
   * @throws IOException when decoding failed
   */
  public static byte[] decode(final String input) throws IOException {
    if (null == input || input.length() == 0) {
      return new byte[0];
    }
    // Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).

    final byte[] input58 = new byte[input.length()];
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      int digit = c < 128 ? INDEXES[c] : -1;
      if (digit < 0) {
        if (logger.isInfoEnabled()) {
          logger.info("Input:\n{}", HexUtils.dump(input.getBytes()));
        }
        throw new UnsupportedEncodingException("Base58 decoding failed: " + digit + " at " + i);
      }
      input58[i] = (byte) digit;
    }
    // Count leading zeros.
    int zeros = 0;
    while (zeros < input58.length && input58[zeros] == 0) {
      ++zeros;
    }
    // Convert base-58 digits to base-256 digits.
    byte[] decoded = new byte[input.length()];
    int outputStart = decoded.length;
    for (int inputStart = zeros; inputStart < input58.length;) {
      decoded[--outputStart] = divmod(input58, inputStart, 58, 256);
      if (input58[inputStart] == 0) {
        ++inputStart; // optimization - skip leading zeros
      }
    }
    // Ignore extra leading zeroes that were added during the calculation.
    while (outputStart < decoded.length && decoded[outputStart] == 0) {
      ++outputStart;
    }
    // Return decoded data (including original number of leading zeros).
    return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length);
  }

  /**
   * Divides a number, represented as an array of bytes each containing a single digit in the
   * specified base, by the given divisor. The given number is modified in-place to contain the
   * quotient, and the return value is the remainder.
   *
   * @param number the number to divide
   * @param firstDigit the index within the array of the first non-zero digit (this is used for
   *        optimization by skipping the leading zeros)
   * @param base the base in which the number's digits are represented (up to 256)
   * @param divisor the number to divide by (up to 256)
   * @return the remainder of the division operation
   */
  private static byte divmod(byte[] number, int firstDigit, int base, int divisor) {
    // this is just long division which accounts for the base of the input digits
    int remainder = 0;
    for (int i = firstDigit; i < number.length; i++) {
      int digit = (int) number[i] & 0xff;
      int temp = remainder * base + digit;
      number[i] = (byte) (temp / divisor);
      remainder = temp % divisor;
    }
    return (byte) remainder;
  }

  /**
   * Encode byte array to base58 with checksum.
   *
   * @param rawData raw byte array
   * @return base58 encoded string with checksum. empty string if {@code rawData} is empty or null.
   */
  public static String encodeWithCheck(final byte[] rawData) {
    if (null == rawData || rawData.length == 0) {
      return "";
    }
    final byte[] checkSum = calculateCheckSum(rawData);
    final byte[] rawTotal = new byte[rawData.length + CHECKSUM_LEN];
    System.arraycopy(rawData, 0, rawTotal, 0, rawData.length);
    System.arraycopy(checkSum, 0, rawTotal, rawTotal.length - CHECKSUM_LEN, CHECKSUM_LEN);
    return encode(rawTotal);
  }

  /**
   * Decode base58 string with checksum to byte array.
   *
   * @param encoded base58 encoded string
   * @return decoded byte array. empty string if {@code encoded} is empty or null.
   * @throws IOException when decoding failed
   */
  public static byte[] decodeWithCheck(final String encoded) throws IOException {
    if (null == encoded || encoded.length() == 0) {
      return new byte[0];
    }
    final byte[] rawTotal = decode(encoded);
    final byte[] rawData = Arrays.copyOfRange(rawTotal, 0, rawTotal.length - CHECKSUM_LEN);
    final byte[] checkSum =
        Arrays.copyOfRange(rawTotal, rawTotal.length - CHECKSUM_LEN, rawTotal.length);
    final byte[] calculatedCheckSum = calculateCheckSum(rawData);
    if (!Arrays.equals(checkSum, calculatedCheckSum)) {
      logger.info("Checksum is mismatch - Input: {}, Computed: {}", checkSum, calculatedCheckSum);
      throw new IllegalArgumentException("Checksum is mismatch");
    }
    return rawData;
  }

  /**
   * Calculate checksum with a {@code rawData}.
   *
   * @param rawData raw data
   * @return calculated checksum
   */
  protected static byte[] calculateCheckSum(final byte[] rawData) {
    final byte[] doubleHashed = Sha256Utils.digest(Sha256Utils.digest(rawData));
    return Arrays.copyOfRange(doubleHashed, 0, CHECKSUM_LEN);
  }

}
