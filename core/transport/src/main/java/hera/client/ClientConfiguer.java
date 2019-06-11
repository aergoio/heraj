/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.strategy.ConnectStrategy;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public interface ClientConfiguer<ConfiguerT> {

  /**
   * Add a configuration with {@code key} and {@code value}.
   *
   * @param key a key
   * @param value a value
   * @return an instance of this
   */
  ConfiguerT addConfiguration(String key, String value);

  /**
   * Provide an endpoint of aergo server. eg. {@code localhost:7845}
   *
   * @param endpoint aergo chain server endpoint
   * @return an instance of this
   */
  ConfiguerT withEndpoint(String endpoint);

  /**
   * Use non-blocking connection. If other {@link ConnectStrategy} is already set, that will be
   * overridden.
   *
   * @return an instance of this
   */
  ConfiguerT withNonBlockingConnect();

  /**
   * Use blocking connection. If other {@link ConnectStrategy} is already set, that will be
   * overridden. This method works for <strong>jdk 1.7 or higher</strong>.
   *
   * @return an instance of this
   */
  ConfiguerT withBlockingConnect();

  /**
   * Set timeout for each request. If timeout is already set, that will be overridden.
   *
   * @param timeout time to time out
   * @param unit time's unit
   *
   * @return an instance of this
   */
  ConfiguerT withTimeout(long timeout, TimeUnit unit);

  /**
   * If fails with non-connection error, after {@code interval} {@code count} times. Default retry
   * count : 0, default retry interval : 5000 milliseconds.
   *
   * @param count retry count. If it is less than 0, set as 0
   * @param interval interval value. If it's less than 0, set as 0
   * @param unit interval unit
   *
   * @return an instance of this
   */
  ConfiguerT withRetry(int count, long interval, TimeUnit unit);

  /**
   * Use plain text on connection.
   *
   * @return an instance of this
   */
  ConfiguerT withPlainText();

  /**
   * Use transport security on connection.
   *
   * @param serverCommonName a server common name
   * @param serverCertPath a server certification path
   * @param clientCertPath a client certification path
   * @param clientKeyPath a client key path. Must be PKCS#8 format
   *
   * @return an instance of this
   */
  ConfiguerT withTransportSecurity(String serverCommonName, String serverCertPath,
      String clientCertPath, String clientKeyPath);

  /**
   * Use transport security on connection.
   *
   * @param serverCommonName a server common name
   * @param serverCertInputStream a server certification stream
   * @param clientCertInputStream a client certification stream
   * @param clientKeyInputStream a client key stream. Must be PKCS#8 format
   *
   * @return an instance of this
   */
  ConfiguerT withTransportSecurity(String serverCommonName, InputStream serverCertInputStream,
      InputStream clientCertInputStream, InputStream clientKeyInputStream);

}
