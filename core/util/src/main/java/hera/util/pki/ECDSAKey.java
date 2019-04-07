/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import static hera.util.IoUtils.stream;
import static org.slf4j.LoggerFactory.getLogger;

import hera.util.HexUtils;
import hera.util.StreamConsumer;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.slf4j.Logger;

@EqualsAndHashCode(exclude = "logger")
public class ECDSAKey {

  protected final transient Logger logger = getLogger(getClass());

  /**
   * Create ECDSAKey with keypair.
   *
   * @param privateKey a private key
   * @param publicKey a public key
   * @param ecParams an ec parameters
   * @return {@link ECDSAKey}
   */
  public static ECDSAKey of(final PrivateKey privateKey, final PublicKey publicKey,
      final ECDomainParameters ecParams) {
    return new ECDSAKey(privateKey, publicKey, ecParams);
  }

  @Getter
  protected final PrivateKey privateKey;

  @Getter
  protected final PublicKey publicKey;

  @Getter
  protected final ECDomainParameters params;

  @Getter
  protected final ECDSAVerifier verifier;

  /**
   * ECDSAKey constructor.
   *
   * @param privateKey a private key
   * @param publicKey a public key
   * @param ecParams an ec parameters
   */
  protected ECDSAKey(final PrivateKey privateKey, final PublicKey publicKey,
      final ECDomainParameters ecParams) {
    this.privateKey = privateKey;
    this.publicKey = publicKey;
    this.params = ecParams;
    this.verifier = new ECDSAVerifier(params);
  }

  /**
   * Sign to plain text.
   *
   * @param plainText text to sign to
   *
   * @return signature ECDSA signature
   */
  public ECDSASignature sign(final InputStream plainText) {
    try {
      final byte[] message = toByteArray(plainText);
      final ECDSASignature signature = sign(this.privateKey, message);
      if (logger.isTraceEnabled()) {
        logger.trace("Message in hexa: {}", HexUtils.encode(message));
        logger.trace("ECDSASignature signature: {}", signature);
      }
      return signature;
    } catch (final Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Generate a deterministic ECDSA signature according to RFC6979.
   *
   * @see <a href="https://github.com/btcsuite/btcd/blob/master/btcec/signature.go">signRFC6979
   *      function in signature.go</a>
   *
   * @param privateKey a private key
   * @param message message to sign
   * @return ECDSASignature result
   * @throws Exception when sign error occurred
   */
  protected ECDSASignature sign(final PrivateKey privateKey, final byte[] message)
      throws Exception {
    final ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
    final BigInteger d = ((org.bouncycastle.jce.interfaces.ECPrivateKey) privateKey).getD();
    final ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(d, params);
    signer.init(true, privKey);
    final BigInteger[] components = signer.generateSignature(message);

    final BigInteger r = components[0];
    final BigInteger s = params.getN().subtract(components[1]);

    return new ECDSASignature(r, s);
  }

  /**
   * Check if {@code signature} is valid for {@code plainText}.
   *
   * @param plainText plain text
   * @param signature ECDSA signature
   *
   * @return if valid
   */
  public boolean verify(final InputStream plainText, final ECDSASignature signature) {
    try {
      return verifier.verify(getPublicKey(), plainText, signature);
    } catch (final Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public String toString() {
    final org.bouncycastle.jce.interfaces.ECPrivateKey ecPrivateKey =
        (org.bouncycastle.jce.interfaces.ECPrivateKey) privateKey;
    final org.bouncycastle.jce.interfaces.ECPublicKey ecPublicKey =
        (org.bouncycastle.jce.interfaces.ECPublicKey) publicKey;
    return String.format("%s\n%s", ecPrivateKey.toString(), ecPublicKey.toString());
  }

  protected byte[] toByteArray(final InputStream plainText) throws Exception {
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    stream(plainText, new StreamConsumer() {
      @Override
      public void apply(byte[] bytes, int offset, int length) throws Exception {
        os.write(bytes);
      }
    });
    return os.toByteArray();
  }

}
