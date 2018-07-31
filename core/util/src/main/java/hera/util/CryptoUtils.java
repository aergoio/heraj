/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static java.lang.System.arraycopy;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

  public static final String CIPHER_CHARSET = "UTF-8";
  public static final String CIPHER_NAME = "AES/ECB/PKCS5Padding";
  protected static final String DEFAULT_PASSWORD = "tn595hil2n9kolh9";

  protected static SecureRandom random;
  protected static MessageDigest sha;
  protected static MessageDigest md5;
  protected static SecretKeySpec keySpec;

  static {
    try {
      random = SecureRandom.getInstance("SHA1PRNG");
      sha = MessageDigest.getInstance("SHA-1");
      md5 = MessageDigest.getInstance("MD5");
      keySpec = new SecretKeySpec(DEFAULT_PASSWORD.getBytes("UTF-8"), "AES");
    } catch (final Throwable th) {
      throw new IllegalStateException(th);
    }
  }

  /**
   * Create secret from password.
   *
   * @param password string to create secret from
   * @param length secret length
   *
   * @return secret to be created
   */
  public static SecretKeySpec createSecret(final byte[] password, final int length) {
    final byte[] fixedLengthPassword = new byte[length];
    Arrays.fill(fixedLengthPassword, (byte) 0);
    arraycopy(password, 0, fixedLengthPassword, 0,
        Math.min(fixedLengthPassword.length, password.length));
    return new SecretKeySpec(fixedLengthPassword, "AES");
  }


  /**
   * Encoding with Base64 and Aes128Ecb.
   *
   * @param source string to encode
   *
   * @return encoded string
   *
   * @throws NoSuchAlgorithmException if no algorithm
   * @throws NoSuchPaddingException if no padding exist
   * @throws InvalidKeyException if invalid key used
   * @throws IllegalBlockSizeException if wrong block size
   * @throws BadPaddingException if padding violate rule
   */
  public static String encryptToAes128EcbWithBase64(
      final String source)
      throws NoSuchAlgorithmException, NoSuchPaddingException,
      InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    return encryptToAes128EcbWithBase64(source, DEFAULT_PASSWORD);
  }

  /**
   * Encoding with Base64 and Aes128Ecb.
   *
   * @param source    string to encode
   * @param password  password to encode with
   *
   * @return encoded string
   *
   * @throws NoSuchAlgorithmException if no algorithm
   * @throws NoSuchPaddingException if no padding exist
   * @throws InvalidKeyException if invalid key used
   * @throws IllegalBlockSizeException if wrong block size
   * @throws BadPaddingException if padding violate rule
   */
  public static String encryptToAes128EcbWithBase64(
      final String source,
      final String password)
      throws NoSuchAlgorithmException, NoSuchPaddingException,
      InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    try {
      SecretKeySpec secretKeySpec = new SecretKeySpec(password.getBytes("UTF-8"), "AES");
      return encryptToAes128EcbWithBase64(source.getBytes(CIPHER_CHARSET), secretKeySpec);
    } catch (final UnsupportedEncodingException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Encoding with Base64 and Aes128Ecb.
   *
   * @param message byte array to encode
   * @param secretKeySpec secret key information
   * @return encoded string
   * @throws NoSuchAlgorithmException if no algorithm
   * @throws NoSuchPaddingException if no padding exist
   * @throws InvalidKeyException if invalid key used
   * @throws IllegalBlockSizeException if wrong block size
   * @throws BadPaddingException if padding violate rule
   */
  public static String encryptToAes128EcbWithBase64(
      final byte[] message,
      final SecretKeySpec secretKeySpec)
      throws NoSuchAlgorithmException, NoSuchPaddingException,
      InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    // Encrypt in 16 bytes (128bit) Block
    final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CIPHER_NAME);
    cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKeySpec);
    final byte[] aes128ecb = cipher.doFinal(message);
    return java.util.Base64.getEncoder().encodeToString(aes128ecb);
  }

  /**
   * Decode with Base64 and Aes128Ecb.
   *
   * @param source string to decode
   * @return decoded string
   * @throws NoSuchAlgorithmException if no algorithm
   * @throws NoSuchPaddingException if no padding exist
   * @throws InvalidKeyException if invalid key used
   * @throws IllegalBlockSizeException if wrong block size
   * @throws BadPaddingException if padding violate rule
   */
  public static byte[] decryptFromAes128EcbWithBase64(
      final String source)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
      IllegalBlockSizeException, BadPaddingException {
    return decryptFromAes128EcbWithBase64(source, DEFAULT_PASSWORD);
  }

  /**
   * Decode with Base64 and Aes128Ecb.
   *
   * @param source string to decode
   * @param password spring to decode with
   *
   * @return decoded string
   * @throws NoSuchAlgorithmException if no algorithm
   * @throws NoSuchPaddingException if no padding exist
   * @throws InvalidKeyException if invalid key used
   * @throws IllegalBlockSizeException if wrong block size
   * @throws BadPaddingException if padding violate rule
   */
  public static byte[] decryptFromAes128EcbWithBase64(
      final String source,
      final String password)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
      IllegalBlockSizeException, BadPaddingException {
    try {
      SecretKeySpec secretKeySpec = new SecretKeySpec(password.getBytes("UTF-8"), "AES");
      return decryptFromAes128EcbWithBase64(source, secretKeySpec);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Decode with Base64 and Aes128Ecb.
   *
   * @param source string to decode
   * @param secretKeySpec secret to decode with
   *
   * @return decoded string
   * @throws NoSuchAlgorithmException if no algorithm
   * @throws NoSuchPaddingException if no padding exist
   * @throws InvalidKeyException if invalid key used
   * @throws IllegalBlockSizeException if wrong block size
   * @throws BadPaddingException if padding violate rule
   */
  public static byte[] decryptFromAes128EcbWithBase64(
      final String source,
      final SecretKeySpec secretKeySpec)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
      IllegalBlockSizeException, BadPaddingException {
    final byte[] decodedBase64 = java.util.Base64.getDecoder().decode(source);
    final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CIPHER_NAME);
    cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKeySpec);
    final byte[] decoded = cipher.doFinal(decodedBase64);
    return decoded;
  }
}
