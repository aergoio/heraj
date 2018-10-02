/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import java.math.BigInteger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ECDSASignature {

  /**
   * Factory method.
   *
   * @param r R value of ECDSASignature
   * @param s S value of ECDSASignature
   * @return created {@link ECDSASignature}
   */
  public static ECDSASignature of(final BigInteger r, final BigInteger s) {
    return new ECDSASignature(r, s);
  }

  @SuppressWarnings("checkstyle:MemberName")
  @Getter
  protected final BigInteger r;

  @SuppressWarnings("checkstyle:MemberName")
  @Getter
  protected final BigInteger s;

  @Override
  public boolean equals(Object obj) {
    if (null == obj) {
      return false;
    }

    if (!obj.getClass().equals(getClass())) {
      return false;
    }

    ECDSASignature other = (ECDSASignature) obj;
    return r.equals(other.getR()) && s.equals(other.getS());
  }

  @Override
  public String toString() {
    return String.format("R: %d, S: %d\n", r, s);
  }

}

