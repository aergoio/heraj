/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import org.junit.Test;

public class GrpcStreamSubscriptionTest extends AbstractTestCase {

  @Test
  public void testUnsubscribe() {
    final io.grpc.Context.CancellableContext context = io.grpc.Context.current().withCancellation();
    context.run(new Runnable() {
      @Override
      public void run() {
      }
    });
    final GrpcStreamSubscription<Object> subscription = new GrpcStreamSubscription<>(context);
    subscription.unsubscribe();
    assertTrue(subscription.isUnsubscribed());
  }

}
