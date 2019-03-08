/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.exception.HerajException;
import hera.exception.UnableToGenerateKeyException;
import hera.spec.resolver.EncryptedPrivateKeyResolver;
import hera.spec.resolver.SignatureResolver;
import hera.spec.resolver.TransactionHashResolver;
import hera.util.AddressUtils;
import hera.util.Base64Utils;
import hera.util.NumberUtils;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import hera.util.pki.ECDSASignature;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode(exclude = {"transactionHashResolver", "signatureResolver", "privateKeyResolver"})
public class AergoKey implements KeyPair, Signer {

  protected static final String CHAR_SET = "UTF-8";

  /**
   * Create a key pair with encoded encrypted private key and password.
   *
   * @param encodedEncryptedPrivateKey base58 with checksum encoded encrypted private key
   * @param password password to decrypt
   * @return key instance
   *
   * @throws UnableToGenerateKeyException on failure of creation
   */
  public static AergoKey of(final String encodedEncryptedPrivateKey, final String password) {
    return new AergoKey(encodedEncryptedPrivateKey, password);
  }

  /**
   * Create a key pair with encrypted private key and password.
   *
   * @param encryptedPrivateKey encrypted private key
   * @param password password to decrypt
   * @return key instance
   *
   * @throws UnableToGenerateKeyException on failure of creation
   */
  public static AergoKey of(final EncryptedPrivateKey encryptedPrivateKey, final String password) {
    return new AergoKey(encryptedPrivateKey, password);
  }

  protected final transient Logger logger = getLogger(getClass());

  protected final TransactionHashResolver transactionHashResolver = new TransactionHashResolver();

  protected final SignatureResolver signatureResolver = new SignatureResolver();

  protected final EncryptedPrivateKeyResolver privateKeyResolver =
      new EncryptedPrivateKeyResolver();

  protected final ECDSAKey ecdsakey;

  @Getter
  protected final AccountAddress address;

  /**
   * AergoKey constructor.
   *
   * @param encodedEncryptedPrivateKey base58 with checksum encoded encrypted private key
   * @param password password to decrypt
   *
   * @throws UnableToGenerateKeyException on failure of creation
   */
  public AergoKey(final String encodedEncryptedPrivateKey, final String password) {
    this(new EncryptedPrivateKey(encodedEncryptedPrivateKey), password);
  }

  /**
   * AergoKey constructor.
   *
   * @param encryptedPrivateKey encrypted private key
   * @param password password to decrypt
   *
   * @throws UnableToGenerateKeyException on failure of creation
   */
  public AergoKey(final EncryptedPrivateKey encryptedPrivateKey, final String password) {
    try {
      final byte[] rawPrivateKey =
          privateKeyResolver.decrypt(encryptedPrivateKey.getBytesValue().getValue(),
              password.getBytes(CHAR_SET));
      this.ecdsakey = new ECDSAKeyGenerator().create(new BigInteger(1, rawPrivateKey));
      this.address = AddressUtils.deriveAddress(this.ecdsakey.getPublicKey());
    } catch (final Exception e) {
      throw new UnableToGenerateKeyException(e);
    }
  }

  /**
   * AergoKey constructor.
   *
   * @param ecdsakey keypair
   */
  public AergoKey(final ECDSAKey ecdsakey) {
    this.ecdsakey = ecdsakey;
    this.address = AddressUtils.deriveAddress(this.ecdsakey.getPublicKey());
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
  public Transaction sign(final RawTransaction rawTransaction) {
    try {
      logger.debug("Sign raw transaction: {}", rawTransaction);
      final BytesValue plainText = transactionHashResolver.calculateHash(rawTransaction);
      final ECDSASignature ecdsaSignature = ecdsakey.sign(plainText.getInputStream());
      final BytesValue serialized =
          signatureResolver.serialize(ecdsaSignature, ecdsakey.getParams().getN());
      logger.trace("Serialized signature: {}", serialized);
      final Signature signature = Signature.of(serialized);
      return Transaction.newBuilder(rawTransaction)
          .signature(signature)
          .build();
    } catch (final Exception e) {
      throw new HerajException(e);
    }
  }

  /**
   * Sign a plain message.
   *
   * @param message a message to sign
   * @return base64 encoded signature
   */
  public String signMessage(final String message) {
    try {
      logger.debug("Sign message: {}", message);
      final BytesValue plainText = new BytesValue(message.getBytes());
      final ECDSASignature ecdsaSignature = ecdsakey.sign(plainText.getInputStream());
      final BytesValue serialized =
          signatureResolver.serialize(ecdsaSignature, ecdsakey.getParams().getN());
      logger.trace("Serialized signature: {}", serialized);
      return Base64Utils.encode(serialized.getValue());
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public boolean verify(final Transaction transaction) {
    try {
      logger.debug("Verify transaction: {}", transaction);
      final BytesValue plainText = transactionHashResolver.calculateHash(transaction);
      final ECDSASignature parsedSignature = signatureResolver
          .parse(transaction.getSignature().getSign(), ecdsakey.getParams().getN());
      return ecdsakey.verify(plainText.getInputStream(), parsedSignature);
    } catch (final Exception e) {
      logger.info("Verification failed by exception {}", e.getLocalizedMessage());
      return false;
    }
  }

  /**
   * Verify message with base64 encoded signature.
   *
   * @param message a message to verify
   * @param base64EncodedSignature a base64 encoded signature
   * @return verification result
   */
  public boolean verifyMessage(final String message, final String base64EncodedSignature) {
    try {
      logger.debug("Verify message {} with signature", message, base64EncodedSignature);
      final BytesValue plainText = new BytesValue(message.getBytes());
      final BytesValue signature = new BytesValue(Base64Utils.decode(base64EncodedSignature));
      final ECDSASignature parsedSignature =
          signatureResolver.parse(signature, ecdsakey.getParams().getN());
      return ecdsakey.verify(plainText.getInputStream(), parsedSignature);
    } catch (final Exception e) {
      logger.info("Verification failed by exception {}", e.getLocalizedMessage());
      return false;
    }
  }

  @Override
  public EncryptedPrivateKey export(final String password) {
    try {
      final byte[] rawPrivateKey = getRawPrivateKey();
      final byte[] rawPassword = password.getBytes(CHAR_SET);
      return new EncryptedPrivateKey(
          BytesValue.of(privateKeyResolver.encrypt(rawPrivateKey, rawPassword)));
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  protected byte[] getRawPrivateKey() {
    final org.bouncycastle.jce.interfaces.ECPrivateKey ecPrivateKey =
        (org.bouncycastle.jce.interfaces.ECPrivateKey) getPrivateKey();
    final BigInteger d = ecPrivateKey.getD();
    return NumberUtils.positiveToByteArray(d);
  }

  @Override
  public String toString() {
    return String.format("Address: %s", getAddress());
  }
}
