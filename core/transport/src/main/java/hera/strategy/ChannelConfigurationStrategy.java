/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import io.grpc.ManagedChannelBuilder;

public interface ChannelConfigurationStrategy extends Strategy {
  void configure(ManagedChannelBuilder<?> builder);
}
