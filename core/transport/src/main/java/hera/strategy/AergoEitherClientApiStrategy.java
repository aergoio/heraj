/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Context;
import hera.ContextAware;
import hera.api.AergoEitherApi;
import hera.client.AergoEitherClient;
import io.grpc.ManagedChannel;
import lombok.Setter;

public class AergoEitherClientApiStrategy implements EitherApiStrategy, ContextAware {

  @Setter
  protected Context context;

  private static AergoEitherClient apply(ConnectStrategy strategy) {
    return new AergoEitherClient((ManagedChannel) strategy.connect());
  }

  @Override
  public AergoEitherApi getEitherApi() {
    return context.getStrategy(ConnectStrategy.class)
        .map(AergoEitherClientApiStrategy::apply).get();
  }

}
