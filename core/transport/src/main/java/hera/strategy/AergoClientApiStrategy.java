/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Context;
import hera.ContextAware;
import hera.api.AergoApi;
import hera.client.AergoClient;
import io.grpc.ManagedChannel;
import lombok.Setter;

public class AergoClientApiStrategy implements ApiStrategy, ContextAware {

  @Setter
  protected Context context;

  private static AergoClient apply(ConnectStrategy strategy) {
    return new AergoClient((ManagedChannel) strategy.connect());
  }

  @Override
  public AergoApi getApi() {
    return context.getStrategy(ConnectStrategy.class)
        .map(AergoClientApiStrategy::apply).get();
  }

}
