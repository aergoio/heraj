/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

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
   *   return 0 if array is {@code null}
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
}