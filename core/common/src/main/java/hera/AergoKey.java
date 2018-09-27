/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.IoUtils.from;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.Base58Transformer;
import hera.api.Transformer;
import hera.api.model.AccountAddress;
import hera.exception.HerajException;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSASignature;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.Getter;
import org.slf4j.Logger;

public class AergoKey implements KeyPair {

  protected final transient Logger logger = getLogger(getClass());

  /**
   * Create a key pair with base58 encoded private key.
   *
   * @param encodedPrivateKey base58 encoded private key
   * @return key instance
   *
   * @throws Exception on failure of recovery
   */
  public static AergoKey recover(final String encodedPrivateKey) throws Exception {
    return recover(from(transformer.decode(new StringReader(encodedPrivateKey))));
  }

  /**
   * Create a key pair with raw private key.
   *
   * @param rawPrivateKey raw private key
   * @return key instance to be recovered
   *
   * @throws Exception on failure of recovery
   */
  public static AergoKey recover(final byte[] rawPrivateKey) throws Exception {
    return new AergoKey(ECDSAKey.recover(rawPrivateKey));
  }

  // publickey.x
  protected static final int PUBLIC_X_LEN = 32;

  // publickey.x + [odd|even] of publickey.y
  protected static final int ADDRESS_LENGTH = PUBLIC_X_LEN + 1;

  protected static final Transformer transformer = new Base58Transformer();

  protected final ECDSAKey ecdsakey;

  @Getter
  protected final AccountAddress address;

  /**
   * AergoKey constructor.
   *
   * @param ecdsakey keypair
   */
  public AergoKey(final ECDSAKey ecdsakey) {
    this.ecdsakey = ecdsakey;

    final org.bouncycastle.jce.interfaces.ECPublicKey ecPublicKey =
        (org.bouncycastle.jce.interfaces.ECPublicKey) this.ecdsakey.getPublicKey();
    final byte[] rawAddress = new byte[ADDRESS_LENGTH];
    rawAddress[0] = (byte) (ecPublicKey.getQ().getY().toBigInteger().testBit(0) ? 0x03 : 0x02);

    byte[] xbyteArray = ecPublicKey.getQ().getX().toBigInteger().toByteArray();
    // xbyteArray may have an extra sign bit
    final int extraBytesCount = xbyteArray.length - PUBLIC_X_LEN;
    if (extraBytesCount >= 1) {
      System.arraycopy(xbyteArray, extraBytesCount, rawAddress, 1,
          xbyteArray.length - extraBytesCount);
    } else {
      System.arraycopy(xbyteArray, 0, rawAddress, 1, xbyteArray.length);
    }

    this.address = AccountAddress.of(rawAddress);
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
  public byte[] sign(InputStream plainText) {
    return serialize(ecdsakey.sign(plainText));
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
  public boolean verify(InputStream plainText, byte[] signature) {
    // TODO : not yet implemented, do we need it?
    return true;
  }

  @Override
  public String getEncodedPrivateKey() {
    try {
      final org.bouncycastle.jce.interfaces.ECPrivateKey ecPrivateKey =
          (org.bouncycastle.jce.interfaces.ECPrivateKey) getPrivateKey();
      final BigInteger d = ecPrivateKey.getD();
      final byte[] mayHaveExtraSignByte = d.toByteArray();
      final int byteLen = (d.bitLength() + 7) >>> 3;
      byte[] rawPrivateKey = new byte[byteLen];
      if (mayHaveExtraSignByte.length > byteLen) {
        System.arraycopy(mayHaveExtraSignByte, 1, rawPrivateKey, 0, byteLen);
      } else {
        System.arraycopy(mayHaveExtraSignByte, 0, rawPrivateKey, 0, byteLen);
      }

      return from(transformer.encode(new ByteArrayInputStream(rawPrivateKey)));
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public String getEncodedAddress() {
    try {
      return getAddress().getEncodedValue();
    } catch (IOException e) {
      throw new HerajException(e);
    }
  }

  @Override
  public String toString() {
    return String.format("Privatekey: %s\nAddress: %s", getEncodedPrivateKey(),
        getEncodedAddress());
  }

}
