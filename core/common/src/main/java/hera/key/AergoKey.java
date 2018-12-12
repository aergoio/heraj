/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.encode.Base58WithCheckSum;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Signature;
import hera.exception.HerajException;
import hera.exception.UnableToGenerateKeyException;
import hera.util.AddressUtils;
import hera.util.CryptoUtils;
import hera.util.HexUtils;
import hera.util.NumberUtils;
import hera.util.Pair;
import hera.util.Sha256Utils;
import hera.util.VersionUtils;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import hera.util.pki.ECDSASignature;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.slf4j.Logger;

@EqualsAndHashCode
public class AergoKey implements KeyPair {

  protected static final int HEADER_MAGIC = 0x30;

  protected static final int INT_MARKER = 0x02;

  // minimum length of a DER encoded signature which both R and S are 1 byte each.
  // <header-magic> + <1-byte> + <int-marker> + 0x01 + <r.byte> + <int-marker> + 0x01 + <s.byte>
  protected static final int MINIMUM_SIGNATURE_LEN = 8;

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
   * @param encodedEncryptedPrivateKey base58 with checksum encoded encrypted private key
   * @param password password to decrypt
   * @return key instance
   *
   * @throws UnableToGenerateKeyException on failure of creation
   */
  public static AergoKey of(final Base58WithCheckSum encodedEncryptedPrivateKey,
      final String password) {
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
    this(() -> encodedEncryptedPrivateKey, password);
  }

  /**
   * AergoKey constructor.
   *
   * @param encodedEncryptedPrivateKey base58 with checksum encoded encrypted private key
   * @param password password to decrypt
   *
   * @throws UnableToGenerateKeyException on failure of creation
   */
  public AergoKey(final Base58WithCheckSum encodedEncryptedPrivateKey, final String password) {
    this(EncryptedPrivateKey.of(encodedEncryptedPrivateKey), password);
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
          decrypt(encryptedPrivateKey.getBytesValue().getValue(), password.getBytes(UTF_8));
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
  public Signature sign(InputStream plainText) {
    final BytesValue serialized = BytesValue.of(serialize(ecdsakey.sign(plainText)));
    if (logger.isTraceEnabled()) {
      logger.trace("Serialized signature: {}", serialized);
    }
    return Signature.of(serialized);
  }

  protected byte[] serialize(final ECDSASignature signature) {
    final BigInteger order = ECDSAKey.getEcParams().getN();
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
    serialized[0] = HEADER_MAGIC;
    serialized[1] = (byte) (serialized.length - 2);

    // <int-marker> + <R.length> + <R.bytes>
    serialized[2] = INT_MARKER;
    serialized[3] = (byte) rbyteArray.length;
    System.arraycopy(rbyteArray, 0, serialized, 4, rbyteArray.length);

    // <int-marker> + <S.length> + <S.bytes>
    final int offset = 4 + rbyteArray.length;
    serialized[offset] = INT_MARKER;
    serialized[offset + 1] = (byte) sbyteArray.length;
    System.arraycopy(sbyteArray, 0, serialized, offset + 2, sbyteArray.length);

    return serialized;
  }

  @Override
  public boolean verify(final InputStream plainText, final Signature signature) {
    try {
      final ECDSASignature parsedSignature = parseSignature(signature);
      return ecdsakey.verify(plainText, parsedSignature);
    } catch (Exception e) {
      logger.info("Verification failed {}", e.getLocalizedMessage());
      return false;
    }
  }

  /**
   * Parse {@link ECDSASignature} from the serialized one.
   *
   * @param signature serialized ecdsa signature
   * @return parsed {@link ECDSASignature}. null if parsing failed.
   */
  protected ECDSASignature parseSignature(final Signature signature) {
    if (null == signature) {
      throw new HerajException("Serialized signature is null");
    }

    final byte[] rawSignature = signature.getSign().getValue();
    if (logger.isTraceEnabled()) {
      logger.trace("Raw signature: {}, len: {}", HexUtils.encode(rawSignature),
          rawSignature.length);
    }

    int index = 0;

    if (rawSignature.length < MINIMUM_SIGNATURE_LEN) {
      throw new HerajException(
          "Invalid serialized length: length is shorter than " + MINIMUM_SIGNATURE_LEN);
    }

    index = validateHeader(rawSignature, index);

    final Pair<BigInteger, Integer> rAndIndex = parseInteger(rawSignature, index);
    final BigInteger r = rAndIndex.v1;
    index = rAndIndex.v2;

    final Pair<BigInteger, Integer> sAndIndex = parseInteger(rawSignature, index);
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

    if (source[index] != HEADER_MAGIC) {
      throw new HerajException(
          "Invalid magic number. expected: " + HEADER_MAGIC + ", but was: " + source[index]);
    }
    ++index;

    int sigDataLen = source[index];
    if (sigDataLen < MINIMUM_SIGNATURE_LEN || (source.length - 2) < sigDataLen) {
      throw new HerajException("Invalid signature length");
    }
    ++index;

    return index;
  }

  protected Pair<BigInteger, Integer> parseInteger(final byte[] source, final int start) {
    int index = start;

    // parse marker
    if (source[index] != INT_MARKER) {
      throw new HerajException(
          "Invalid integer header. expected: " + INT_MARKER + ", but was: " + source[index]);
    }
    ++index;

    // parse integer length
    final int length = source[index];
    ++index;

    // parse integer
    final BigInteger order = ECDSAKey.getEcParams().getN();
    byte[] rawInteger = Arrays.copyOfRange(source, index, index + length);
    final BigInteger r = new BigInteger(rawInteger);
    if (r.compareTo(order) >= 0) {
      throw new HerajException("Integer is greater then curve order");
    }
    index += length;

    return new Pair<>(r, index);
  }

  @Override
  public EncryptedPrivateKey export(final String password) {
    try {
      final byte[] rawPrivateKey = getRawPrivateKey();
      final byte[] rawPassword = password.getBytes(UTF_8);
      return new EncryptedPrivateKey(BytesValue.of(encrypt(rawPrivateKey, rawPassword)));
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  protected byte[] getRawPrivateKey() {
    final org.bouncycastle.jce.interfaces.ECPrivateKey ecPrivateKey =
        (org.bouncycastle.jce.interfaces.ECPrivateKey) getPrivateKey();
    final BigInteger d = ecPrivateKey.getD();
    return NumberUtils.postiveToByteArray(d);
  }

  @Override
  public String toString() {
    return String.format("Address: %s", getAddress());
  }

  protected byte[] encrypt(final byte[] rawPrivateKey, final byte[] rawPassword)
      throws InvalidCipherTextException {
    final byte[] hashedPassword = Sha256Utils.digest(rawPassword);
    final byte[] encryptKey = Sha256Utils.digest(rawPassword, hashedPassword);
    final byte[] nonce = calculateNonce(hashedPassword);
    logger.trace("Encript data: {}", HexUtils.encode(rawPrivateKey));
    logger.trace("Encript key: {}", HexUtils.encode(encryptKey));
    logger.trace("Encript nonce: {}", HexUtils.encode(nonce));
    final byte[] encrypted = CryptoUtils.encryptToAesGcm(rawPrivateKey, encryptKey, nonce);
    return VersionUtils.envelop(encrypted, EncryptedPrivateKey.VERSION);
  }

  protected byte[] decrypt(final byte[] rawEncrypted, final byte[] rawPassword)
      throws InvalidCipherTextException {
    final byte[] withoutVersion = VersionUtils.trim(rawEncrypted);
    final byte[] hashedPassword = Sha256Utils.digest(rawPassword);
    final byte[] decryptKey = Sha256Utils.digest(rawPassword, hashedPassword);
    final byte[] nonce = calculateNonce(hashedPassword);
    logger.trace("Decript data: {}", HexUtils.encode(withoutVersion));
    logger.trace("Decript key: {}", HexUtils.encode(decryptKey));
    logger.trace("Decript nonce: {}", HexUtils.encode(nonce));
    final byte[] rawPrivateKey = CryptoUtils.decryptFromAesGcm(withoutVersion, decryptKey, nonce);
    logger.trace("Decripted: {}", HexUtils.encode(rawPrivateKey));
    return rawPrivateKey;
  }

  protected byte[] calculateNonce(final byte[] hashedPassword) {
    return Arrays.copyOfRange(hashedPassword, 4, 16);
  }

}
