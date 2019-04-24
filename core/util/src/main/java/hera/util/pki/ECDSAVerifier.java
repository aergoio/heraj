/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import static hera.util.ValidationUtils.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

import hera.util.HexUtils;
import java.security.PublicKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.slf4j.Logger;

@ToString
@EqualsAndHashCode(exclude = "logger")
@RequiredArgsConstructor
public class ECDSAVerifier {

  protected final transient Logger logger = getLogger(getClass());

  @Getter
  protected final ECDomainParameters params;

  /**
   * Verify signature with a message and public key x and y point value.
   *
   * @param publicKey a publicKey
   * @param hashedMessage a sha256-hashed message
   * @param signature ECDSA signature
   *
   * @return verification result
   */
  public boolean verify(final PublicKey publicKey, final byte[] hashedMessage,
      final ECDSASignature signature) {
    try {
      assertEquals(hashedMessage.length, 32, "Sha-256 hashed message should have 32 byte length");
      if (logger.isTraceEnabled()) {
        logger.trace("Message in hexa: {}", HexUtils.encode(hashedMessage));
        logger.trace("ECDSASignature signature: {}", signature);
      }
      final org.bouncycastle.jce.interfaces.ECPublicKey ecPublicKey =
          (org.bouncycastle.jce.interfaces.ECPublicKey) publicKey;
      final ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
      final ECPublicKeyParameters privKey =
          new ECPublicKeyParameters(ecPublicKey.getQ(), params);
      signer.init(false, privKey);
      return signer.verifySignature(hashedMessage, signature.getR(), signature.getS());
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

}
