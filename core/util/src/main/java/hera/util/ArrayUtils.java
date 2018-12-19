/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import org.bouncycastle.util.Arrays;

public class ArrayUtils {
  /**
   * Check if array is empty.
   * <p>
   * return {@code false} unless array's length is 0
   * </p>
   *
   * @param arrays array to check
   * @param <T> array element's type
   * 
   * @return check result
   */
  public static <T> boolean isEmpty(final T[] arrays) {
    return (0 == length(arrays));
  }

  /**
   * Return array's length.
   * <p>
   * return 0 if array is {@code null}
   * </p>
   *
   * @param arrays array to check
   * @param <T> array element's type
   *
   * @return array's length
   */
  public static <T> int length(
      final T[] arrays) {
    if (null == arrays) {
      return 0;
    }
    return arrays.length;
  }

  /**
   * Concat two byte array.
   *
   * @param left left array
   * @param right right array
   * @return a concated array
   */
  public static byte[] concat(final byte[] left, final byte[] right) {
    if (null == left && null == right) {
      return new byte[0];
    }
    if (null == left) {
      return Arrays.copyOf(right, right.length);
    }
    if (null == right) {
      return Arrays.copyOf(left, left.length);
    }
    final byte[] concated = new byte[left.length + right.length];
    System.arraycopy(left, 0, concated, 0, left.length);
    System.arraycopy(right, 0, concated, left.length, right.length);
    return concated;
  }
}
