/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.FailoverHandler;

public abstract class ComparableFailoverHandler implements FailoverHandler,
    Comparable<FailoverHandler> {

  protected abstract int getPriority();

  @Override
  public int compareTo(final FailoverHandler o) {
    if (!(o instanceof ComparableFailoverHandler)) {
      // prefer handler with priority over handler without priority
      return 1;
    }

    // lower one has power
    final Integer currentPriotity = getPriority();
    final Integer targetPriority = ((ComparableFailoverHandler) o).getPriority();
    return targetPriority.compareTo(currentPriotity);
  }

}
