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
    return BytesValue.of(serialize(ecdsakey.sign(plainText)));
  }

  protected byte[] serialize(final ECDSASignature signature) {
    final BigInteger order = ECDSAKey.ecParams.getN();
    final BigInteger halfOrder = order.divide(BigInteger.valueOf(2L));

    final BigInteger r = signature.getR();
    BigInteger s = signature.getS();
    if (s.compareTo(halfOrder) == 1) {
      s = order.subtract(s);
    }

    // in this case, use canonical byte array, not raw bytes
    final byte[] rbyteArray = r.toByteArray();
    final byte[] sbyteArray = s.toByteArray();

    final byte[] serialized = new byte[6 + rbyteArray.length + sbyteArray.length];
    serialized[0] = 0x30;
    serialized[1] = (byte) (serialized.length - 2);
    serialized[2] = 0x02;
    serialized[3] = (byte) rbyteArray.length;
    System.arraycopy(rbyteArray, 0, serialized, 4, rbyteArray.length);
    final int offset = 4 + rbyteArray.length;
    serialized[offset] = 0x02;
    serialized[offset + 1] = (byte) sbyteArray.length;
    System.arraycopy(sbyteArray, 0, serialized, offset + 2, sbyteArray.length);

    return serialized;
  }

  @Override
  public boolean verify(final InputStream plainText, final BytesValue signature) {
    // TODO : not yet implemented, do we need it?
    return true;
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
