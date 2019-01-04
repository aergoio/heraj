package hera.client;

import hera.Context;
import hera.exception.RpcException;
import hera.strategy.ChannelConfigurationStrategy;
import hera.strategy.ConnectStrategy;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.function.Function;

public class ManagedChannelFactory implements Function<Context, ManagedChannel> {

  @Override
  public ManagedChannel apply(final Context context) {
    final ConnectStrategy<?> connectStrategy = context.getStrategy(ConnectStrategy.class);
    if (null == connectStrategy) {
      throw new RpcException("ConnectStrategy must be present in context");
    }
    final ManagedChannelBuilder<?> builder =
        (ManagedChannelBuilder<?>) connectStrategy.connect();

    context.getStrategies().stream().filter(ChannelConfigurationStrategy.class::isInstance)
        .forEach(s -> ((ChannelConfigurationStrategy) s).configure(builder));
    return builder.build();
  }
}
