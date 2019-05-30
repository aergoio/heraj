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

  @Override
  public boolean equals(final Object obj) {
    return (null != obj) && (obj instanceof SecurityConfigurationStrategy);
  }

  @Override
  public int hashCode() {
    return PlainTextChannelStrategy.class.hashCode();
  }

}