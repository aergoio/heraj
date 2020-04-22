/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import io.grpc.ManagedChannelBuilder;

@ApiAudience.Private
@ApiStability.Unstable
public interface ChannelConfigurationStrategy extends Strategy {

  void configure(ManagedChannelBuilder<?> builder);

}
