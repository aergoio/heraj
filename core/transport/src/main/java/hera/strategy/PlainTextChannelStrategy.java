/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.slf4j.LoggerFactory.getLogger;

import io.grpc.ManagedChannelBuilder;
import lombok.ToString;
import org.slf4j.Logger;

@ToString
public class PlainTextChannelStrategy implements SecurityConfigurationStrategy {

  @ToString.Exclude
  protected final transient Logger logger = getLogger(getClass());

  @Override
  public void configure(final ManagedChannelBuilder<?> builder) {
    builder.usePlaintext();
  }

}
