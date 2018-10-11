/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.Strategy;
import hera.api.AergoApi;
import hera.api.AergoAsyncApi;
import hera.api.AergoEitherApi;
import hera.strategy.NettyConnectStrategy;
import lombok.Getter;

public class AergoClientBuilder {

  @Getter
  protected static Context defaultContext;

  static {
    defaultContext = new Context();
    defaultContext.addStrategy(new NettyConnectStrategy());
  }

  protected Context context = Context.copyOf(defaultContext);

  public AergoClientBuilder addStrategy(final Strategy strategy) {
    context.addStrategy(strategy);
    return this;
  }

  public AergoClientBuilder bind(final Context context) {
    this.context = context;
    return this;
  }

  public AergoApi build() {
    return new AergoClient(context);
  }

  public AergoEitherApi buildEither() {
    return new AergoEitherClient(context);
  }

  public AergoAsyncApi buildAsync() {
    return new AergoAsyncClient(context);
  }

}
