/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import static hera.util.IoUtils.stream;
import static org.slf4j.LoggerFactory.getLogger;

import hera.exception.SignException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class ECDSAKey {

  protected static final String SIGN_ALGORITHM = "SHA256WithECDSA";

  protected final Logger logger = getLogger(getClass());

  @Getter
  protected final PrivateKey privateKey;

  @Getter
  protected final PublicKey publicKey;

  /**
   * Sign to plain text.
   *
   * @param plainText text to sign to
   *
   * @return signature
   */
  public byte[] sign(final InputStream plainText) {
    try {
      final Signature signature = Signature.getInstance(SIGN_ALGORITHM);
      signature.initSign(privateKey);
      stream(plainText, (bytes, offset, length) -> signature.update(bytes, offset, length));
      final byte[] signed = signature.sign();

      return signed;
    } catch (final Throwable e) {
      throw new SignException(e);
    }
  }

  /**
   * Check if {@code signature} is valid for {@code plainText}.
   *
   * @param plainText plain text
   * @param signature signature
   *
   * @return if valid
   */
  public boolean verify(final InputStream plainText, final byte[] signature) {
    try {
      final Signature verifier = Signature.getInstance(SIGN_ALGORITHM);
      verifier.initVerify(publicKey);
      stream(plainText, (bytes, offset, length) -> verifier.update(bytes, offset, length));

      return verifier.verify(signature);
    } catch (final Throwable e) {
      throw new SignException(e);
    }

  }
}
