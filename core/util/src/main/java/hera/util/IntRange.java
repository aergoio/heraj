/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

public class IntRange extends Pair<Integer, Integer> {

  public IntRange(final Integer v1, final Integer v2) {
    super(Math.min(v1, v2), Math.max(v1, v2));
  }

  /**
   * Select subrange from this range.
   *
   * <p>
   *   This means intersection of two ranges.
   * </p>
   *
   * @param sub subrange
   *
   * @return intersection range
   */
  public IntRange select(final IntRange sub) {
    final int index1 = Math.min(Math.max(sub.v1, this.v1), this.v2);
    final int index2 = Math.min(Math.max(sub.v2, this.v1), this.v2);

    if (index1 == sub.v1 && index2 == sub.v2) {
      return sub;
    }

    return new IntRange(index1, index2);
  }

  @Override
  public String toString() {
    return v1 + "~" + v2;
  }
}
