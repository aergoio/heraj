/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Context;
import hera.ContextAware;
import hera.api.AergoAsyncApi;
import hera.client.AergoAsyncClient;
import io.grpc.ManagedChannel;
import lombok.Setter;

public class AergoAsyncClientApiStrategy implements AsyncApiStrategy, ContextAware {

  @Setter
  protected Context context;

  @SuppressWarnings("rawtypes")
  private static AergoAsyncClient apply(ConnectStrategy strategy) {
    return new AergoAsyncClient((ManagedChannel) strategy.connect());
  }

  @Override
  public AergoAsyncApi getApi() {
    return context.getStrategy(ConnectStrategy.class)
        .map(AergoAsyncClientApiStrategy::apply).get();
  }

}
