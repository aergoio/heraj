/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.slf4j.Logger;

/**
 * Class containing hostname and port.
 * <p>
 * This is used for connection to server like aergo node.
 * </p>
 */
@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode
public class HostnameAndPort {

  protected static final Logger logger = getLogger(HostnameAndPort.class);

  /**
   * Create {@link HostnameAndPort} from string.
   *
   * @param str string to parse
   * @return parsed {@link HostnameAndPort}
   */
  public static HostnameAndPort of(final String str) {
    logger.debug("Hostname and port: {}", str);
    final String separator = ":";
    final int separatorIndex = str.indexOf(separator);
    logger.trace("Separator index: {}", separatorIndex);
    final int portIndex = separatorIndex + separator.length();
    final String hostname = (separatorIndex < 0) ? str : str.substring(0, separatorIndex);
    logger.trace("Hostname: {}", hostname);
    final String portStr = (separatorIndex < 0) ? null : str.substring(portIndex);
    logger.trace("Port: {}", portStr);
    final int port = null != portStr ? Integer.valueOf(portStr) : -1;
    return new HostnameAndPort(hostname, port);
  }

  @Getter
  protected final String hostname;

  @Getter
  protected final int port;

  public int getPort(int defaultPort) {
    return (port < 0) ? defaultPort : port;
  }

  private HostnameAndPort(final String hostname, final int port) {
    assertNotNull(hostname, "Hostname must not null");
    this.hostname = hostname;
    this.port = port;
  }

  @Override
  public String toString() {
    return (port < 0) ? hostname : (hostname + ":" + port);
  }

}
