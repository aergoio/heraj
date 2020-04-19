/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import io.grpc.ManagedChannelBuilder;
import lombok.ToString;

@ToString
public class PlainTextChannelStrategy implements SecurityConfigurationStrategy {

  @Override
  public void configure(final ManagedChannelBuilder<?> builder) {
    builder.usePlaintext();
  }

}
