/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import io.grpc.ManagedChannelBuilder;
import lombok.ToString;

@ApiAudience.Private
@ApiStability.Unstable
@ToString
public class PlainTextChannelStrategy implements SecurityConfigurationStrategy {

  @Override
  public void configure(final ManagedChannelBuilder<?> builder) {
    builder.usePlaintext();
  }

}
