/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static org.slf4j.LoggerFactory.getLogger;

import hera.spec.EncryptedPrivateKeySpec;
import hera.util.CryptoUtils;
import hera.util.HexUtils;
import hera.util.Sha256Utils;
import hera.util.VersionUtils;
import java.util.Arrays;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.slf4j.Logger;

public class EncryptedPrivateKeyResolver {

  protected final transient Logger logger = getLogger(getClass());

  /**
   * Encrypt private key with a {@code password}.
   * 
   * @param rawPrivateKey a raw private key
   * @param rawPassword a password to encrypt
   * @return encrypted private key
   * @throws InvalidCipherTextException on encryption failure
   */
  public byte[] encrypt(final byte[] rawPrivateKey, final byte[] rawPassword)
      throws InvalidCipherTextException {
    final byte[] hashedPassword = Sha256Utils.digest(rawPassword);
    final byte[] encryptKey = Sha256Utils.digest(rawPassword, hashedPassword);
    final byte[] nonce = calculateNonce(hashedPassword);
    final byte[] encrypted = CryptoUtils.encryptToAesGcm(rawPrivateKey, encryptKey, nonce);
    return VersionUtils.envelop(encrypted, EncryptedPrivateKeySpec.PREFIX);
  }

  /**
   * Decrypt encrypted private key with a {@code password}.
   * 
   * @param rawEncrypted an encrypted private key
   * @param rawPassword a password to decrypt
   * @return a decrypted private key
   * @throws InvalidCipherTextException on decryption failure
   */
  public byte[] decrypt(final byte[] rawEncrypted, final byte[] rawPassword)
      throws InvalidCipherTextException {
    final byte[] withoutVersion = VersionUtils.trim(rawEncrypted);
    final byte[] hashedPassword = Sha256Utils.digest(rawPassword);
    final byte[] decryptKey = Sha256Utils.digest(rawPassword, hashedPassword);
    final byte[] nonce = calculateNonce(hashedPassword);
    logger.trace("Decript data: {}", HexUtils.encode(withoutVersion));
    final byte[] rawPrivateKey = CryptoUtils.decryptFromAesGcm(withoutVersion, decryptKey, nonce);
    return rawPrivateKey;
  }

  protected byte[] calculateNonce(final byte[] hashedPassword) {
    return Arrays.copyOfRange(hashedPassword, 4, 16);
  }

}
