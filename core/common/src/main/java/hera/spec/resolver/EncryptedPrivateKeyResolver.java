/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static hera.util.CryptoUtils.encryptToAesGcm;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.spec.EncryptedPrivateKeySpec;
import hera.util.CryptoUtils;
import hera.util.Sha256Utils;
import hera.util.VersionUtils;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.bouncycastle.crypto.InvalidCipherTextException;

@ApiAudience.Private
@ApiStability.Unstable
public class EncryptedPrivateKeyResolver {

  protected static final String CHAR_SET = "UTF-8";

  /**
   * Encrypt private key with a {@code password}.
   * 
   * @param privateKeyBytes a raw private key
   * @param password a password to encrypt
   * @return encrypted private key
   * @throws InvalidCipherTextException on encryption failure
   * @throws UnsupportedEncodingException on password encoding failure
   */
  public static EncryptedPrivateKey encrypt(final BytesValue privateKeyBytes, final String password)
      throws InvalidCipherTextException, UnsupportedEncodingException {
    final byte[] rawPassword = password.getBytes(CHAR_SET);
    final byte[] hashedPassword = Sha256Utils.digest(rawPassword);
    final byte[] encryptKey = Sha256Utils.digest(rawPassword, hashedPassword);
    final byte[] nonce = calculateNonce(hashedPassword);
    final byte[] encrypted = encryptToAesGcm(privateKeyBytes.getValue(), encryptKey, nonce);
    final BytesValue encryptedBytesValue =
        new BytesValue(VersionUtils.envelop(encrypted, EncryptedPrivateKeySpec.PREFIX));
    return new EncryptedPrivateKey(encryptedBytesValue);
  }

  /**
   * Decrypt encrypted private key with a {@code password}.
   * 
   * @param encryptedPrivateKey an encrypted private key
   * @param password a password to decrypt
   * @return a decrypted private key
   * @throws InvalidCipherTextException on decryption failure
   * @throws UnsupportedEncodingException on password encoding failure
   */
  public static BytesValue decrypt(final EncryptedPrivateKey encryptedPrivateKey,
      final String password)
      throws InvalidCipherTextException, UnsupportedEncodingException {
    final byte[] rawEncrypted = encryptedPrivateKey.getBytesValue().getValue();
    final byte[] rawPassword = password.getBytes(CHAR_SET);
    final byte[] withoutVersion = VersionUtils.trim(rawEncrypted);
    final byte[] hashedPassword = Sha256Utils.digest(rawPassword);
    final byte[] decryptKey = Sha256Utils.digest(rawPassword, hashedPassword);
    final byte[] nonce = calculateNonce(hashedPassword);
    final byte[] rawPrivateKey = CryptoUtils.decryptFromAesGcm(withoutVersion, decryptKey, nonce);
    return new BytesValue(rawPrivateKey);
  }

  protected static byte[] calculateNonce(final byte[] hashedPassword) {
    return Arrays.copyOfRange(hashedPassword, 4, 16);
  }

}
