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
   * @see <a href="https://github.com/golang/go/blob/master/src/crypto/ecdsa/ecdsa.go">verify
   *      function in ecdsa.go</a>
   *
   * @param publicKey a publicKey
   * @param plainText a plain text
   * @param signature ECDSA signature
   *
   * @return verification result
   */
  public boolean verify(final PublicKey publicKey, final InputStream plainText,
      final ECDSASignature signature) {
    try {
      final byte[] message = toByteArray(plainText);
      if (logger.isTraceEnabled()) {
        logger.trace("Message in hexa: {}", HexUtils.encode(message));
        logger.trace("ECDSASignature signature: {}", signature);
      }
      final org.bouncycastle.jce.interfaces.ECPublicKey ecPublicKey =
          (org.bouncycastle.jce.interfaces.ECPublicKey) publicKey;
      final ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
      final ECPublicKeyParameters privKey =
          new ECPublicKeyParameters(ecPublicKey.getQ(), params);
      signer.init(false, privKey);
      return signer.verifySignature(message, signature.getR(), signature.getS());
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
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
