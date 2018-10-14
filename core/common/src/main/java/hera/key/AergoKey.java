/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static org.slf4j.LoggerFactory.getLogger;

import hera.VersionUtils;
import hera.api.encode.Base58WithCheckSum;
import hera.api.encode.EncodedString;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.exception.HerajException;
import hera.util.Base58Utils;
import hera.util.CryptoUtils;
import hera.util.HexUtils;
import hera.util.Sha256Utils;
import hera.util.pki.ECDSAKey;
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

  // [odd|even] of publickey.y + [optional 0x00] + publickey.x
  protected static final int ADDRESS_LENGTH = 33;

  // minimum length of a DER encoded signature which both R and S are 1 byte each.
  // 0x30 + <1-byte> + 0x02 + 0x01 + <r.byte> + 0x2 + 0x01 + <s.byte>
  protected static final int MINIMUM_SIGNATURE_LEN = 8;

  /**
   * Create a key pair with encoded encrypted private key and password.
   *
   * @param encodedEncryptedPrivateKey encrypted private key
   * @param password password to decrypt
   * @return key instance
   * @throws Exception on failure of decryption
   */
  public static AergoKey of(final String encodedEncryptedPrivateKey, final String password)
      throws Exception {
    Base58WithCheckSum encoded = () -> encodedEncryptedPrivateKey;
    return of(encoded, password);
  }

  /**
   * Create a key pair with encrypted private key and password.
   *
   * @param encryptedPrivateKey encrypted private key
   * @param password password to decrypt
   * @return key instance
   * @throws Exception on failure of decryption
   */
  public static AergoKey of(final EncodedString encryptedPrivateKey, final String password)
      throws Exception {
    return new AergoKey(encryptedPrivateKey, password);
  }

  protected final transient Logger logger = getLogger(getClass());

  protected final ECDSAKey ecdsakey;

  @Getter
  protected final AccountAddress address;

  /**
   * AergoKey constructor.
   *
   * @param encryptedPrivateKey encrypted private key
   * @param password password to decrypt
   * @throws Exception on failure of decryption
   */
  public AergoKey(final EncodedString encryptedPrivateKey, final String password) throws Exception {
    final byte[] rawPrivateKey =
        decrypt(encryptedPrivateKey.decode().getValue(), password.getBytes("UTF-8"));
    this.ecdsakey = ECDSAKey.of(rawPrivateKey);
    final org.bouncycastle.jce.interfaces.ECPublicKey ecPublicKey =
        (org.bouncycastle.jce.interfaces.ECPublicKey) this.ecdsakey.getPublicKey();
    this.address = buildAddress(ecPublicKey);
  }

  /**
   * AergoKey constructor.
   *
   * @param ecdsakey keypair
   */
  public AergoKey(final ECDSAKey ecdsakey) {
    this.ecdsakey = ecdsakey;
    final org.bouncycastle.jce.interfaces.ECPublicKey ecPublicKey =
        (org.bouncycastle.jce.interfaces.ECPublicKey) this.ecdsakey.getPublicKey();
    this.address = buildAddress(ecPublicKey);
  }

  protected AccountAddress buildAddress(
      final org.bouncycastle.jce.interfaces.ECPublicKey ecPublicKey) {
    final byte[] rawAddress = new byte[ADDRESS_LENGTH];
    rawAddress[0] = (byte) (ecPublicKey.getQ().getY().toBigInteger().testBit(0) ? 0x03 : 0x02);
    final byte[] xbyteArray = getPureByteArrayOf(ecPublicKey.getQ().getX().toBigInteger());
    System.arraycopy(xbyteArray, 0, rawAddress, rawAddress.length - xbyteArray.length,
        xbyteArray.length);
    return AccountAddress
        .of(BytesValue.of(VersionUtils.envelop(rawAddress, AccountAddress.VERSION)));
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
  public BytesValue sign(InputStream plainText) {
    final BytesValue serialized = BytesValue.of(serialize(ecdsakey.sign(plainText)));
    if (logger.isTraceEnabled()) {
      logger.trace("Serialized signature: {}", serialized);
    }
    return serialized;
  }

  protected byte[] serialize(final ECDSASignature signature) {
    final BigInteger order = ECDSAKey.getEcParams().getN();
    final BigInteger halfOrder = order.divide(BigInteger.valueOf(2L));

    final BigInteger r = signature.getR();
    BigInteger s = signature.getS();
    if (s.compareTo(halfOrder) == 1) {
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
    serialized[0] = 0x30;
    serialized[1] = (byte) (serialized.length - 2);

    // <0x02> + <R.length> + <R.bytes>
    serialized[2] = 0x02;
    serialized[3] = (byte) rbyteArray.length;
    System.arraycopy(rbyteArray, 0, serialized, 4, rbyteArray.length);

    // <0x02> + <S.length> + <S.bytes>
    final int offset = 4 + rbyteArray.length;
    serialized[offset] = 0x02;
    serialized[offset + 1] = (byte) sbyteArray.length;
    System.arraycopy(sbyteArray, 0, serialized, offset + 2, sbyteArray.length);

    return serialized;
  }

  @Override
  public boolean verify(final InputStream plainText, final BytesValue signature) {
    final ECDSASignature parsedSignature = parseSignature(signature);
    logger.trace("Parsed ECDSASignature: {}", parsedSignature);
    return null != parsedSignature ? ecdsakey.verify(plainText, parsedSignature) : false;
  }

  /**
   * Parse {@link ECDSASignature} from the serialized one.
   *
   * @param signature serialized ecdsa signature
   * @return parsed {@link ECDSASignature}. null if parsing failed.
   */
  protected ECDSASignature parseSignature(final BytesValue signature) {
    if (null == signature) {
      logger.trace("Serialized signature is null");
      return null;
    }

    final BigInteger order = ECDSAKey.getEcParams().getN();

    final byte[] rawSignature = signature.getValue();
    if (logger.isTraceEnabled()) {
      logger.trace("Raw signature: {}, len: {}", HexUtils.encode(rawSignature),
          rawSignature.length);
    }

    if (rawSignature.length < MINIMUM_SIGNATURE_LEN) {
      logger.trace("Invalid serialized length: length is shorter than {}", MINIMUM_SIGNATURE_LEN);
      return null;
    }

    int index = 0;

    // validate magic number
    if (rawSignature[index] != 0x30) {
      logger.trace("Invalid magic number. expected: {}, but was: {}", 0x30, rawSignature[index]);
      return null;
    }
    ++index;

    // validate length
    int sigDataLen = rawSignature[index];
    if (sigDataLen < MINIMUM_SIGNATURE_LEN || (rawSignature.length - 2) < sigDataLen) {
      logger.trace("Invalid signature length");
      return null;
    }
    ++index;

    // validate r header
    if (rawSignature[index] != 0x02) {
      logger.trace("Invalid r header. expected: {}, but was: {}", 0x02, rawSignature[index]);
      return null;
    }
    ++index;

    // parse r length
    final int rLen = rawSignature[index];
    ++index;

    // parse r
    byte[] rawR = Arrays.copyOfRange(rawSignature, index, index + rLen);
    final BigInteger r = new BigInteger(rawR);
    if (r.compareTo(order) >= 0) {
      logger.trace("Signature R is greater then curve order");
      return null;
    }
    index += rLen;

    // validate s header
    if (rawSignature[index] != 0x02) {
      logger.trace("Invalid s header. expected: {}, but was: {}", 0x02, rawSignature[index]);
      return null;
    }
    ++index;

    // parse s length
    final int sLen = rawSignature[index];
    ++index;

    // parse s
    byte[] rawS = Arrays.copyOfRange(rawSignature, index, index + sLen);
    final BigInteger s = new BigInteger(rawS);
    if (s.compareTo(order) >= 0) {
      logger.trace("Signature S is greater then curve order");
      return null;
    }
    index += sLen;

    if (index < rawSignature.length) {
      logger.trace(
          "Invalid length of r or s, still ramains bytes after parsing. index: {}, length: {}",
          index, rawSignature.length);
      return null;
    }

    return ECDSASignature.of(r, s);
  }

  @Override
  public String getEncodedPrivateKey() {
    try {
      return Base58Utils.encodeWithCheck(getRawPrivateKey());
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public EncryptedPrivateKey getEncryptedPrivateKey(final String password) {
    try {
      final byte[] rawPrivateKey = getRawPrivateKey();
      final byte[] rawPassword = password.getBytes("UTF-8");
      return new EncryptedPrivateKey(BytesValue.of(encrypt(rawPrivateKey, rawPassword)));
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public String getEncodedEncryptedPrivateKey(final String password) {
    return Base58Utils.encodeWithCheck(getEncryptedPrivateKey(password).getBytesValue().getValue());
  }

  protected byte[] getRawPrivateKey() {
    final org.bouncycastle.jce.interfaces.ECPrivateKey ecPrivateKey =
        (org.bouncycastle.jce.interfaces.ECPrivateKey) getPrivateKey();
    final BigInteger d = ecPrivateKey.getD();
    return getPureByteArrayOf(d);
  }

  protected byte[] getPureByteArrayOf(final BigInteger bigInteger) {
    final byte[] raw = bigInteger.toByteArray();
    final int byteLen = (bigInteger.bitLength() + 7) >>> 3;
    if (raw.length > byteLen) {
      return Arrays.copyOfRange(raw, 1, raw.length);
    }
    return raw;
  }

  @Override
  public String getEncodedAddress() {
    try {
      return Base58Utils.encodeWithCheck(getAddress().getBytesValue().getValue());
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public String toString() {
    return String.format("Privatekey: %s\nAddress: %s", getEncodedPrivateKey(),
        getEncodedAddress());
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
