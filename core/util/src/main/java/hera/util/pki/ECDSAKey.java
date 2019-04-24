/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import static hera.util.ValidationUtils.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

import hera.util.HexUtils;
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
   * Sign to message.
   *
   * @param hashedMessage a sha256-hashed message
   *
   * @return signature ECDSA signature
   */
  public ECDSASignature sign(final byte[] hashedMessage) {
    try {
      assertEquals(hashedMessage.length, 32, "Sha-256 hashed message should have 32 bytes length");
      final ECDSASignature signature = sign(this.privateKey, hashedMessage);
      if (logger.isTraceEnabled()) {
        logger.trace("Message in hexa: {}", HexUtils.encode(hashedMessage));
        logger.trace("ECDSASignature signature: {}", signature);
      }
      return signature;
    } catch (final Throwable e) {
      throw new IllegalStateException(e);
    }
  }

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
   * @param hashedMessage a sha256-hashed message
   * @param signature ECDSA signature
   *
   * @return if valid
   */
  public boolean verify(final byte[] hashedMessage, final ECDSASignature signature) {
    try {
      return verifier.verify(getPublicKey(), hashedMessage, signature);
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

}
