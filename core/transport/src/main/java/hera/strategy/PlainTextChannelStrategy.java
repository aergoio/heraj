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
    logger.info("Configure channel with plaintext");
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
