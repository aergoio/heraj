/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import com.google.common.base.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Pair<T1, T2> {
  public final T1 v1;
  public final T2 v2;

  @Override
  public int hashCode() {
    return ((null == v1) ? 0 : (v1.hashCode())) + ((null == v2) ? 0 : (v2.hashCode()));
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Pair)) {
      return false;
    }
    final Pair<Object, Object> other = (Pair<Object, Object>) obj;
    return Objects.equal(v1, other.v1) && Objects.equal(v2, other.v2);
  }
}
