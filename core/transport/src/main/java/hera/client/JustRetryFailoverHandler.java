/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.ValidationUtils.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Invocation;
import hera.Response;
import hera.api.model.Time;
import lombok.Getter;
import org.slf4j.Logger;

class JustRetryFailoverHandler extends ComparableFailoverHandler {

  protected final transient Logger logger = getLogger(getClass());

  @Getter
  protected final int priority = 2;

  protected final int count;

  protected final long interval; // milliseconds

  JustRetryFailoverHandler(final int count, final Time interval) {
    assertTrue(0 < count, "Retry count must be positive");
    this.count = count;
    this.interval = interval.toMilliseconds();
  }

  @Override
  public <T> void handle(final Invocation<T> invocation, final Response<T> response) {
    int countDown = this.count;
    while (null != response.getError() && 0 < countDown) {
      try {
        logger.debug("Just retry with {} (count left: {})", invocation, countDown);
        final T ret = invocation.invoke();
        response.success(ret);
      } catch (Exception e) {
        response.fail(e);
      }
      --countDown;
    }
  }

}
