/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Optional.ofNullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Signature {

  @Getter
  @Setter
  protected BytesValue sign = new BytesValue(null);

  @Getter
  @Setter
  protected Hash hash = new Hash(null);

  /**
   * Create {@link Signature}.
   *
   * @param sign sign value
   * @param hash hash value
   *
   * @return created signature
   */
  public static Signature of(final BytesValue sign, final Hash hash) {
    final Signature signature = new Signature();
    signature.setSign(sign);
    signature.setHash(hash);
    return signature;
  }

  /**
   * Copy deep.
   *
   * @param source {@link Signature} to copy
   *
   * @return copied signature
   */
  public static Signature copyOf(final Signature source) {
    if (null == source) {
      return null;
    }
    final Signature copy = new Signature();
    ofNullable(source.getSign()).ifPresent(copy::setSign);
    ofNullable(source.getHash()).ifPresent(copy::setHash);
    return copy;
  }
}
