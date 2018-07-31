/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

public class ThreadUtils {

  protected static final Logger logger = getLogger(ThreadUtils.class);

  /**
   * Try to sleep thread.
   * <p>
   *   Ignore {@link InterruptedException}
   * </p>
   *
   * @param milliseconds time to sleep in milliseconds
   */
  public static void trySleep(final long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (final InterruptedException e) {
      logger.trace("A sleep broken");
    }
  }

}
