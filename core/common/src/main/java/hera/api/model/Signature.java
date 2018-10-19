/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Optional.ofNullable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Signature {

  @Getter
  @Setter
  protected BytesValue sign;

  @Getter
  @Setter
  protected TxHash txHash;

  /**
   * Create {@link Signature}.
   *
   * @param sign sign value
   * @param hash hash value
   *
   * @return created signature
   */
  public static Signature of(final BytesValue sign, final TxHash hash) {
    final Signature signature = new Signature();
    signature.setSign(sign);
    signature.setTxHash(hash);
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
    ofNullable(source.getTxHash()).ifPresent(copy::setTxHash);
    return copy;
  }
}
