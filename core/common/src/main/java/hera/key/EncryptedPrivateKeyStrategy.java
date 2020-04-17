/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static hera.util.BytesValueUtils.append;
import static hera.util.BytesValueUtils.trimPrefix;
import static hera.util.CryptoUtils.decryptFromAesGcm;
import static hera.util.CryptoUtils.encryptToAesGcm;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.exception.HerajException;
import hera.util.Sha256Utils;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import java.math.BigInteger;
import java.util.Arrays;

@ApiAudience.Private
@ApiStability.Unstable
public class EncryptedPrivateKeyStrategy implements KeyCipherStrategy<EncryptedPrivateKey> {

  protected static final String CHAR_SET = "UTF-8";

  @Override
  public EncryptedPrivateKey encrypt(final AergoKey aergoKey, final String passphrase) {
    try {
      final byte[] rawPassword = passphrase.getBytes(CHAR_SET);
      final byte[] hashedPassword = Sha256Utils.digest(rawPassword);
      final byte[] encryptKey = Sha256Utils.digest(rawPassword, hashedPassword);
      final byte[] nonce = calculateNonce(hashedPassword);
      final byte[] encrypted =
          encryptToAesGcm(aergoKey.getRawPrivateKey().getValue(), encryptKey, nonce);
      final byte[] withVersion = append(encrypted, EncryptedPrivateKey.ENCRYPTED_PREFIX);
      return EncryptedPrivateKey.of(BytesValue.of(withVersion));
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public AergoKey decrypt(final EncryptedPrivateKey encryptedPrivateKey,
      final String passphrase) {
    try {
      final byte[] rawEncrypted = encryptedPrivateKey.getBytesValue().getValue();
      final byte[] rawPassword = passphrase.getBytes(CHAR_SET);
      final byte[] withoutVersion = trimPrefix(rawEncrypted);
      final byte[] hashedPassword = Sha256Utils.digest(rawPassword);
      final byte[] decryptKey = Sha256Utils.digest(rawPassword, hashedPassword);
      final byte[] nonce = calculateNonce(hashedPassword);
      final byte[] rawPrivateKey = decryptFromAesGcm(withoutVersion, decryptKey, nonce);
      final ECDSAKey ecdsakey = new ECDSAKeyGenerator().create(new BigInteger(1, rawPrivateKey));
      return new AergoKey(ecdsakey);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  protected byte[] calculateNonce(final byte[] hashedPassword) {
    return Arrays.copyOfRange(hashedPassword, 4, 16);
  }

}
