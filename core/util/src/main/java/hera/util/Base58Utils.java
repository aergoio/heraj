/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import java.util.Arrays;

/**
 * Base58 utils class. Encoding, decoding logic is copied from bitcoinj.
 */
public class Base58Utils {

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
   * @param input the bytes to encode
   * @return the base58-encoded string
   */
  public static String encode(byte[] input) {
    if (input.length == 0) {
      return "";
    }
    // Count leading zeros.
    int zeros = 0;
    while (zeros < input.length && input[zeros] == 0) {
      ++zeros;
    }
    // Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
    input = Arrays.copyOf(input, input.length); // since we modify it in-place
    char[] encoded = new char[input.length * 2]; // upper bound
    int outputStart = encoded.length;
    for (int inputStart = zeros; inputStart < input.length;) {
      encoded[--outputStart] = BASE58_CHARS[divmod(input, inputStart, 256, 58)];
      if (input[inputStart] == 0) {
        ++inputStart; // optimization - skip leading zeros
      }
    }
    // Preserve exactly as many leading encoded zeros in output as there were leading zeros in
    // input.
    while (outputStart < encoded.length && encoded[outputStart] == ENCODED_ZERO) {
      ++outputStart;
    }
    while (--zeros >= 0) {
      encoded[--outputStart] = ENCODED_ZERO;
    }
    // Return encoded string (including encoded leading zeros).
    return new String(encoded, outputStart, encoded.length - outputStart);
  }

  /**
   * Decodes the given base58 string into the original data bytes.
   *
   * @param input the base58-encoded string to decode
   * @return the decoded data bytes
   */
  public static byte[] decode(String input) {
    if (input.length() == 0) {
      return new byte[0];
    }
    // Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).
    byte[] input58 = new byte[input.length()];
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      int digit = c < 128 ? INDEXES[c] : -1;
      if (digit < 0) {
        throw new IllegalStateException();
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
      int digit = (int) number[i] & 0xFF;
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
   * @return base58 encoded strihgn with checksum
   */
  public static String encodeWithCheck(final byte[] rawData) {
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
   * @return decoded byte array
   */
  public static byte[] decodeWithCheck(final String encoded) {
    final byte[] rawTotal = decode(encoded);
    final byte[] rawData = Arrays.copyOfRange(rawTotal, 0, rawTotal.length - CHECKSUM_LEN);
    final byte[] checkSum =
        Arrays.copyOfRange(rawTotal, rawTotal.length - CHECKSUM_LEN, rawTotal.length);
    final byte[] calculatedCheckSum = calculateCheckSum(rawData);
    if (!Arrays.equals(checkSum, calculatedCheckSum)) {
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
