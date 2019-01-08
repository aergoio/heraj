package hera.client;

import hera.Context;
import hera.Strategy;
import hera.api.function.Function1;
import hera.exception.RpcException;
import hera.strategy.ChannelConfigurationStrategy;
import hera.strategy.ConnectStrategy;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ManagedChannelFactory implements Function1<Context, ManagedChannel> {

  @Override
  public ManagedChannel apply(final Context context) {
    final ConnectStrategy<?> connectStrategy = context.getStrategy(ConnectStrategy.class);
    if (null == connectStrategy) {
      throw new RpcException("ConnectStrategy must be present in context");
    }
    final ManagedChannelBuilder<?> builder =
        (ManagedChannelBuilder<?>) connectStrategy.connect();

    for (final Strategy strategy : context.getStrategies()) {
      if (strategy instanceof ChannelConfigurationStrategy) {
        ((ChannelConfigurationStrategy) strategy).configure(builder);
      }
    }
    return builder.build();
  }
}
