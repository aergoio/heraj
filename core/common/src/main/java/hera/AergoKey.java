/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.IoUtils.from;

import hera.api.Base58Transformer;
import hera.api.Transformer;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.exception.HerajException;
import hera.util.pki.ECDSAKey;
import hera.util.pki.KeyPair;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.Getter;

public class AergoKey implements BlockChainKeyPair {

  /**
   * Create a key pair with base58 encoded private key.
   *
   * @param encodedPrivateKey base58 encoded private key
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

  protected final KeyPair ecdsakey;

  @Getter
  protected final AccountAddress address;

  /**
   * AergoKey constructor.
   *
   * @param keyPair keypair
   */
  public AergoKey(final KeyPair keyPair) {
    this.ecdsakey = keyPair;

    final org.bouncycastle.jce.interfaces.ECPublicKey ecPublicKey =
        (org.bouncycastle.jce.interfaces.ECPublicKey) ecdsakey.getPublicKey();
    final byte[] rawAddress = new byte[ADDRESS_LENGTH];
    byte[] xbyteArray = ecPublicKey.getQ().getX().toBigInteger().toByteArray();
    // xbyteArray may have an extra sign bit
    final int extraBytesCount = xbyteArray.length - PUBLIC_X_LEN;
    System.arraycopy(xbyteArray, extraBytesCount, rawAddress, 0,
        xbyteArray.length - extraBytesCount);
    rawAddress[ADDRESS_LENGTH - 1] =
        (byte) (ecPublicKey.getQ().getY().toBigInteger().testBit(0) ? 0x03 : 0x02);
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
    return ecdsakey.sign(plainText);
  }

  @Override
  public boolean verify(InputStream plainText, byte[] signature) {
    return ecdsakey.verify(plainText, signature);
  }

  public boolean verify(InputStream plainText, final BytesValue signature) {
    return verify(plainText, signature.getValue());
  }

  @Override
  public String getEncodedPrivateKey() {
    try {
      final org.bouncycastle.jce.interfaces.ECPrivateKey ecPrivateKey =
          (org.bouncycastle.jce.interfaces.ECPrivateKey) ecdsakey;
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
