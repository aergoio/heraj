/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import hera.api.model.Signature;
import hera.key.AergoKey;
import java.io.InputStream;

public interface KeyHoldable {

  /**
   * Sign to plain text.
   *
   * @param plainText text to sign to
   *
   * @return signature
   */
  Signature sign(final InputStream plainText);

  /**
   * Check if {@code signature} is valid for {@code plainText}.
   *
   * @param plainText plain text
   * @param signature a signature
   *
   * @return if valid
   */
  boolean verify(final InputStream plainText, final Signature signature);

  /**
   * Get {@code AergoKey}.
   * 
   * @return {@code AergoKey}
   */
  AergoKey getKey();

}
