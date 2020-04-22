/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.slf4j.LoggerFactory.getLogger;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
class GrpcStreamSubscription<T> implements hera.api.model.Subscription<T> {

  protected final transient Logger logger = getLogger(getClass());

  protected final Object lock = new Object();

  @NonNull
  protected final io.grpc.Context.CancellableContext context;

  @Override
  public void unsubscribe() {
    if (!context.isCancelled()) {
      synchronized (lock) {
        if (!context.isCancelled()) {
          logger.debug("Unsubscribe subscription");
          context.close();
        } else {
          logger.debug("Subscription is already cancelled");
        }
      }
    } else {
      logger.debug("Subscription is already cancelled");
    }
  }

  @Override
  public boolean isUnsubscribed() {
    return context.isCancelled();
  }


  @Override
  public String toString() {
    return String.format("GrpcStreamSubscription(cancalled=%s)", context.isCancelled());
  }

}
