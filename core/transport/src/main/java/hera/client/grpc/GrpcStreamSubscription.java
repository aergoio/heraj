/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.grpc;

import static org.slf4j.LoggerFactory.getLogger;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class GrpcStreamSubscription<T> implements hera.api.model.Subscription<T> {

  protected final Logger logger = getLogger(getClass());

  protected final io.grpc.Context.CancellableContext context;

  @Override
  public void unsubscribe() {
    logger.info("Unsubscribe subscription: {}", getClass());
    context.close();
  }

  @Override
  public boolean isUnsubscribed() {
    return context.isCancelled();
  }

}
