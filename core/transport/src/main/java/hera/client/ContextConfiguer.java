/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.strategy.ConnectStrategy;
import hera.strategy.NettyConnectStrategy;
import hera.strategy.OkHttpConnectStrategy;
import hera.strategy.RetryStrategy;
import hera.strategy.SimpleTimeoutStrategy;
import java.util.concurrent.TimeUnit;

public interface ContextConfiguer<ConfiguerT> {

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
   * Use {@link NettyConnectStrategy}. If other {@link ConnectStrategy} is already set, that will be
   * overridden.
   *
   * @return an instance of this
   */
  ConfiguerT withNonBlockingConnect();

  /**
   * Use {@link OkHttpConnectStrategy}. If other {@link ConnectStrategy} is already set, that will
   * be overridden. This method works for <strong>jdk 1.7 or higher</strong>.
   *
   * @return an instance of this
   */
  ConfiguerT withBlockingConnect();

  /**
   * Set timeout for each request with {@link SimpleTimeoutStrategy}. If timeout is already set,
   * that will be overridden.
   *
   * @param timeout time to time out
   * @param unit time's unit
   *
   * @return an instance of this
   */
  ConfiguerT withTimeout(long timeout, TimeUnit unit);

  /**
   * Use {@link RetryStrategy}. Default retry count : 0, default retry interval : 5000 milliseconds.
   *
   * @param count retry count. If it is less than 0, set as 0
   * @param interval interval value. If it's less than 0, set as 0
   * @param unit interval unit
   *
   * @return an instance of this
   */
  ConfiguerT withRetry(int count, long interval, TimeUnit unit);

}
