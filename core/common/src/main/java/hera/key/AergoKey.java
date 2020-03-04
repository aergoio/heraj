/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static hera.key.SignatureSpec.serialize;
import static hera.util.Sha256Utils.digest;
import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Encrypted;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Hash;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.HerajException;
import hera.util.NumberUtils;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSASignature;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode
public class AergoKey implements KeyPair, Signer {

  /**
   * Create a key pair with encoded encrypted private key and password.
   *
   * @param encodedEncryptedPrivateKey a base58 with checksum encoded encrypted private key
   * @param passphrase a passphrase to decrypt
   * @return key instance
   */
  public static AergoKey of(final String encodedEncryptedPrivateKey, final String passphrase) {
    return new AergoKey(encodedEncryptedPrivateKey, passphrase);
  }

  /**
   * Create a key pair with encrypted private key and password.
   *
   * @param encryptedPrivateKey encrypted private key
   * @param passphrase a passphrase to decrypt
   * @return key instance
   */
  public static AergoKey of(final EncryptedPrivateKey encryptedPrivateKey,
      final String passphrase) {
    return new AergoKey(encryptedPrivateKey, passphrase);
  }

  protected final transient Logger logger = getLogger(getClass());

  protected final ECDSAKey ecdsakey;

  @Getter
  protected final AccountAddress address;

  /**
   * AergoKey constructor.
   *
   * @param encodedEncryptedPrivateKey base58 with checksum encoded encrypted private key
   * @param passphrase a passphrase to decrypt
   */
  public AergoKey(final String encodedEncryptedPrivateKey, final String passphrase) {
    this(new EncryptedPrivateKeyStrategy(), EncryptedPrivateKey.of(encodedEncryptedPrivateKey),
        passphrase);
  }

  /**
   * AergoKey constructor.
   *
   * @param encryptedPrivateKey encrypted private key
   * @param passphrase a passphrase to decrypt
   */
  public AergoKey(final EncryptedPrivateKey encryptedPrivateKey, final String passphrase) {
    this(new EncryptedPrivateKeyStrategy(), encryptedPrivateKey, passphrase);
  }

  protected <T extends Encrypted> AergoKey(final AergoKeyCipherStrategy<T> strategy,
      final T encrypted, final String passphrase) {
    assertNotNull(encrypted, "An encrypted must not null");
    assertNotNull(passphrase, "A passphrase must not null");
    final AergoKey decrypted = strategy.decrypt(encrypted, passphrase);
    this.ecdsakey = decrypted.ecdsakey;
    this.address = decrypted.getAddress();
  }

  /**
   * AergoKey constructor.
   *
   * @param ecdsakey keypair
   */
  public AergoKey(final ECDSAKey ecdsakey) {
    assertNotNull(ecdsakey, "ECDSAKey must not null");
    this.ecdsakey = ecdsakey;
    this.address = AccountAddressSpec.deriveAddress(ecdsakey.getPublicKey());
  }

  @Override
  public PrivateKey getPrivateKey() {
    return ecdsakey.getPrivateKey();
  }

  @Override
  public PublicKey getPublicKey() {
    return ecdsakey.getPublicKey();
  }

  @Override
  public AccountAddress getPrincipal() {
    return getAddress();
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    try {
      logger.debug("Sign raw transaction: {}", rawTransaction);
      final TxHash withoutSignature = rawTransaction.calculateHash();
      final ECDSASignature ecdsaSignature =
          ecdsakey.sign(withoutSignature.getBytesValue().getValue());
      final Signature signature = serialize(ecdsaSignature);
      logger.trace("Raw signature: {}", ecdsaSignature);
      logger.trace("Serialized signature: {}", signature);
      final TxHash withSignature = rawTransaction.calculateHash(signature);
      final Transaction transaction = Transaction.newBuilder()
          .rawTransaction(rawTransaction)
          .signature(signature)
          .hash(withSignature)
          .build();
      return transaction;
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public Signature signMessage(final BytesValue message) {
    try {
      logger.debug("Sign to message: {}", message);
      final Hash hashedMessage = Hash.of(BytesValue.of(digest(message.getValue())));
      return signMessage(hashedMessage);
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public Signature signMessage(final Hash hashedMessage) {
    try {
      logger.debug("Sign to hashed message: {}", hashedMessage);
      final ECDSASignature ecdsaSignature = ecdsakey.sign(hashedMessage.getBytesValue().getValue());
      final Signature signature = serialize(ecdsaSignature);
      logger.trace("Serialized signature: {}", signature);
      return signature;
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  /**
   * Return encrypted private key.
   *
   * @param passphrase an passphrase to encrypt key
   * @return encrypted key
   *
   * @deprecated use {@link #exportAsWif(String)}
   */
  public EncryptedPrivateKey export(final String passphrase) {
    return export(new EncryptedPrivateKeyStrategy(), passphrase);
  }

  protected <T extends Encrypted> T export(final AergoKeyCipherStrategy<T> strategy,
      final String passphrase) {
    return strategy.encrypt(this, passphrase);
  }

  /**
   * Return encryptd private key as wallet import format.
   *
   * @param passphrase an passphrase to encrypt key
   * @return encrypted key
   */
  public EncryptedPrivateKey exportAsWif(final String passphrase) {
    return export(new EncryptedPrivateKeyStrategy(), passphrase);
  }

  /**
   * Get private in in a raw byte array.
   *
   * @return a raw private key
   */
  public BytesValue getRawPrivateKey() {
    final org.bouncycastle.jce.interfaces.ECPrivateKey ecPrivateKey =
        (org.bouncycastle.jce.interfaces.ECPrivateKey) getPrivateKey();
    final BigInteger d = ecPrivateKey.getD();
    return BytesValue.of(NumberUtils.positiveToByteArray(d));
  }

  @Override
  public String toString() {
    return String.format("AergoKey(address=%s)", getAddress());
  }

}
